package org.lean.presentation.connector.types.filter;

import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.datacontext.IDataContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Simple filter for rows with specific field values
 */
@JsonDeserialize( as = LeanSimpleFilterConnector.class )
public class LeanSimpleFilterConnector extends LeanBaseConnector implements ILeanConnector {

  private List<SimpleFilterValue> filterValues;

  @JsonIgnore
  protected ArrayBlockingQueue<Object> finishedQueue;

  public LeanSimpleFilterConnector() {
    super( "SimpleFilterConnector" );
    filterValues = new ArrayList<>();
    finishedQueue = null;
  }

  public LeanSimpleFilterConnector( List<SimpleFilterValue> filterValues ) {
    this();
    this.filterValues = filterValues;
  }

  public LeanSimpleFilterConnector(LeanSimpleFilterConnector c) {
    super(c);
    this.filterValues = new ArrayList<>(  );
    for (SimpleFilterValue value : c.filterValues) {
      this.filterValues.add(new SimpleFilterValue( value.getFieldName(), value.getFilterValue() ));
    }
  }

  public LeanSimpleFilterConnector clone() {
    return new LeanSimpleFilterConnector(this);
  }

  @Override public IRowMeta describeOutput( IDataContext dataContext ) throws LeanException {
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector source '" + getSourceConnectorName() + "' for simple filter connector" );
    }
    IRowMeta sourceRowMeta = connector.getConnector().describeOutput( dataContext );
    return sourceRowMeta;
  }

  @Override public void startStreaming( IDataContext dataContext ) throws LeanException {

    // which connector do we read from?
    //
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector source '" + getSourceConnectorName() + "' for simple filter connector" );
    }

    if ( finishedQueue != null ) {
      throw new LeanException( "Please don't start streaming twice in your application, wait until the connector has finished sending rows" );
    }
    finishedQueue = new ArrayBlockingQueue<>( 10 );

    // What does the input look like?
    //
    final IRowMeta inputRowMeta = connector.describeOutput( dataContext );

    // What are the simple filter row indexes?
    //
    int[] valueIndexes = new int[filterValues.size()];
    for (int i=0;i<valueIndexes.length;i++) {
      SimpleFilterValue filterValue = filterValues.get( i );
      valueIndexes[i] = inputRowMeta.indexOfValue( filterValue.getFieldName() );
      if (valueIndexes[i]<0) {
        throw new LeanException( "Unable to find filter field '"+filterValue.getFieldName()+"' in input of connector '"+getSourceConnectorName() );
      }
    }

    // Add a row listener to the parent connector
    //
    connector.getConnector().addRowListener( new ILeanRowListener() {
      private Object[] previousRow = null;

      @Override public void rowReceived( IRowMeta rowMeta, Object[] rowData ) throws LeanException {

        if ( rowData == null ) {
          outputDone();
          finishedQueue.add( new Object() );
          return;
        }

        boolean pass = true;
        for (int i=0;i<valueIndexes.length;i++) {

          SimpleFilterValue simpleFilterValue = filterValues.get( i );
          int valueIndex = valueIndexes[i];

          String filterValue = simpleFilterValue.getFilterValue();
          IValueMeta valueMeta = inputRowMeta.getValueMeta( valueIndex );

          try {
            String rowValue = valueMeta.getString( rowData[valueIndex] );

            if (filterValue==null && rowValue!=null ||
              filterValue!=null && rowValue==null ||
              filterValue.equals( rowValue )
            ) {
              pass = false;

              // stop looking
              break;
            }
          } catch(HopException e) {
            throw new LeanException( "Unable to convert simple filter input row value '"+valueMeta.toString(), e );
          }
        }

        if (pass) {
          passToRowListeners( rowMeta, rowData );
        }
      }

    } );

    // Now signal start streaming...
    //
    connector.getConnector().startStreaming( dataContext );
  }

  @Override public void waitUntilFinished() throws LeanException {
    try {
      while ( finishedQueue.poll( 1, TimeUnit.DAYS ) == null ) {
        ;
      }
    } catch ( InterruptedException e ) {
      throw new LeanException( "Interrupted while waiting for more rows in connector", e );
    }
    finishedQueue = null;
  }


  /**
   * Gets filterValues
   *
   * @return value of filterValues
   */
  public List<SimpleFilterValue> getFilterValues() {
    return filterValues;
  }

  /**
   * @param filterValues The filterValues to set
   */
  public void setFilterValues( List<SimpleFilterValue> filterValues ) {
    this.filterValues = filterValues;
  }
}
