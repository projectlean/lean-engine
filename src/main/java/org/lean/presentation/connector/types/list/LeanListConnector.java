package org.lean.presentation.connector.types.list;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMetaBuilder;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

import java.util.ArrayList;
import java.util.List;

/** A simple wrapper around a java.util.List of Strings. */
@JsonDeserialize(as = LeanListConnector.class)
@LeanConnectorPlugin(
    id = "LeanListConnector",
    name = "List",
    description = "A simple connector for embedded usage")
public class LeanListConnector extends LeanBaseConnector implements ILeanConnector {

  @HopMetadataProperty private String columnName;

  @HopMetadataProperty private List<String> list;

  public LeanListConnector() {
    super("LeanListConnector");
    this.columnName = "value";
    this.list = new ArrayList<>();
  }

  public LeanListConnector(String columnName, List<String> list) {
    this();
    this.columnName = columnName;
    this.list = list;
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) throws LeanException {
    return new RowMetaBuilder().addString(columnName).build();
  }

  @Override
  public LeanBaseConnector clone() {
    return new LeanListConnector(this.columnName, new ArrayList<>(this.list));
  }

  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {
    IRowMeta rowMeta = describeOutput(dataContext);

    for (String value : list) {
      Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
      rowData[0] = value;

      for (ILeanRowListener rowListener : rowListeners) {
        rowListener.rowReceived(rowMeta, rowData);
      }
    }
    outputDone();
  }

  @Override
  public void waitUntilFinished() throws LeanException {
    // Nothing to do here, everything was done in startStreaming()
  }

  /**
   * Gets columnName
   *
   * @return value of columnName
   */
  public String getColumnName() {
    return columnName;
  }

  /** @param columnName The columnName to set */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  /**
   * Gets list
   *
   * @return value of list
   */
  public List<String> getList() {
    return list;
  }

  /** @param list The list to set */
  public void setList(List<String> list) {
    this.list = list;
  }
}
