package org.lean.presentation.connector.types.sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.ILeanRowListener;
import org.lean.core.LeanColumn;
import org.lean.core.LeanSortMethod;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/** Sort rows from a source connector using a selection of columns */
@JsonDeserialize(as = LeanSortConnector.class)
@LeanConnectorPlugin(id = "SortConnector", name = "Sort rows", description = "Sorts all rows")
public class LeanSortConnector extends LeanBaseConnector implements ILeanConnector {

  @JsonIgnore protected ArrayBlockingQueue<Object> finishedQueue;
  @HopMetadataProperty private List<LeanColumn> columns;
  @HopMetadataProperty private List<LeanSortMethod> sortMethods;

  public LeanSortConnector() {
    super("SortConnector");
    finishedQueue = null;
    columns = new ArrayList<>();
    sortMethods = new ArrayList<>();
  }

  public LeanSortConnector(List<LeanColumn> columns, List<LeanSortMethod> sortMethods) {
    this();
    this.columns = columns;
    this.sortMethods = sortMethods;
  }

  public LeanSortConnector(LeanSortConnector c) {
    super(c);
    columns = new ArrayList<>();
    for (LeanColumn column : c.columns) {
      this.columns.add(new LeanColumn(column));
    }
    sortMethods = new ArrayList<>();
    for (LeanSortMethod method : c.sortMethods) {
      this.sortMethods.add(new LeanSortMethod(method));
    }
  }

  public LeanSortConnector clone() {
    return new LeanSortConnector(this);
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) throws LeanException {
    LeanConnector connector = dataContext.getConnector(getSourceConnectorName());
    if (connector == null) {
      throw new LeanException(
          "Unable to find source '" + getSourceConnectorName() + "' for sort connector");
    }
    IRowMeta sourceRowMeta = connector.getConnector().describeOutput(dataContext);
    return sourceRowMeta;
  }

  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {

    // which connector do we read from?
    //
    LeanConnector connector = dataContext.getConnector(getSourceConnectorName());
    if (connector == null) {
      throw new LeanException(
          "Unable to find source '" + getSourceConnectorName() + "' for sort connector");
    }

    if (finishedQueue != null) {
      throw new LeanException(
          "Please don't start streaming twice in your application, wait until the connector has finished sending rows");
    }
    finishedQueue = new ArrayBlockingQueue<>(10);

    // What does the input look like?
    //
    final IRowMeta inputRowMeta = connector.describeOutput(dataContext);
    final IRowMeta outputRowMeta = inputRowMeta.clone();

    final List<Object[]> rows = new ArrayList<>();
    final int[] fieldIndexes = new int[columns.size()];

    for (int i = 0; i < fieldIndexes.length; i++) {
      LeanColumn column = columns.get(i);
      LeanSortMethod sortMethod = sortMethods.get(i);
      fieldIndexes[i] = inputRowMeta.indexOfValue(column.getColumnName());
      if (fieldIndexes[i] < 0) {
        throw new LeanException("Sort column '" + column.getColumnName());
      }

      outputRowMeta.getValueMeta(fieldIndexes[i]).setSortedDescending(!sortMethod.isAscending());
    }

    // Add a row listener to the parent connector
    //
    connector
        .getConnector()
        .addRowListener(
            new ILeanRowListener() {
              private Object[] previousRow = null;

              @Override
              public void rowReceived(IRowMeta rowMeta, Object[] rowData) throws LeanException {

                if (rowData == null) {
                  // Sort the rows list
                  //
                  try {
                    Collections.sort(
                        rows,
                        new Comparator<Object[]>() {
                          @Override
                          public int compare(Object[] o1, Object[] o2) {
                            try {
                              return outputRowMeta.compare(o1, o2, fieldIndexes);
                            } catch (HopValueException e) {
                              throw new RuntimeException("Error comparing rows", e);
                            }
                          }
                        });
                  } catch (Exception e) {
                    throw new LeanException("Error sorting rows", e);
                  }

                  // Write the rows
                  //
                  for (Object[] row : rows) {
                    passToRowListeners(outputRowMeta, row);
                  }

                  // Rows are no longer needed, GC them immediately
                  //
                  rows.clear();
                  outputDone();
                  finishedQueue.add(new Object());
                  return;
                }

                rows.add(rowData);
              }
            });

    // Now signal start streaming...
    //
    connector.getConnector().startStreaming(dataContext);
  }

  @Override
  public void waitUntilFinished() throws LeanException {
    try {
      while (finishedQueue.poll(1, TimeUnit.DAYS) == null) {
        ;
      }
    } catch (InterruptedException e) {
      throw new LeanException("Interrupted while waiting for more rows in connector", e);
    }
    finishedQueue = null;
  }

  /**
   * Gets columns
   *
   * @return value of columns
   */
  public List<LeanColumn> getColumns() {
    return columns;
  }

  /** @param columns The columns to set */
  public void setColumns(List<LeanColumn> columns) {
    this.columns = columns;
  }

  /**
   * Gets sortMethods
   *
   * @return value of sortMethods
   */
  public List<LeanSortMethod> getSortMethods() {
    return sortMethods;
  }

  /** @param sortMethods The sortMethods to set */
  public void setSortMethods(List<LeanSortMethod> sortMethods) {
    this.sortMethods = sortMethods;
  }
}
