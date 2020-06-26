package org.lean.presentation.connector.types.distinct;

import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.datacontext.IDataContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Select distinct values from a source connector
 */
@JsonDeserialize( as = LeanDistinctConnector.class )
public class LeanDistinctConnector extends LeanBaseConnector implements ILeanConnector {

  @JsonIgnore
  protected ArrayBlockingQueue<Object> finishedQueue;

  public LeanDistinctConnector() {
    super( "DistinctConnector" );
    finishedQueue = null;
  }

  @Override public IRowMeta describeOutput( IDataContext dataContext ) throws LeanException {
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector source '" + getSourceConnectorName() + "' for distinct connector" );
    }
    IRowMeta sourceRowMeta = connector.getConnector().describeOutput( dataContext );
    return sourceRowMeta;
  }

  public LeanDistinctConnector(LeanDistinctConnector c) {
    super(c);
    // Nothing specific beyond the base connector metadata
  }

  public LeanDistinctConnector clone() {
    return new LeanDistinctConnector(this);
  }

  @Override public void startStreaming( IDataContext dataContext ) throws LeanException {

    // which connector do we read from?
    //
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector source '" + getSourceConnectorName() + "' for passthrough connector" );
    }

    if ( finishedQueue != null ) {
      throw new LeanException( "Please don't start streaming twice in your application, wait until the connector has finished sending rows" );
    }
    finishedQueue = new ArrayBlockingQueue<>( 10 );

    // What does the input look like?
    //
    final IRowMeta inputRowMeta = connector.describeOutput( dataContext );

    AtomicBoolean firstRow = new AtomicBoolean( true );

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

        if ( firstRow.get() ) {
          passToRowListeners( rowMeta, rowData );
          firstRow.set( false );
        } else {
          // Compare all values in the row
          //
          int result;
          try {
            result = rowMeta.compare( rowData, previousRow );
          } catch( HopValueException e ) {
            throw new LeanException( "Error comparing rows of data", e );
          }
          // Only pass different rows
          if (result!=0) {
            passToRowListeners( rowMeta, rowData );
          }
        }

        previousRow = rowData;
      }

    });

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

}
