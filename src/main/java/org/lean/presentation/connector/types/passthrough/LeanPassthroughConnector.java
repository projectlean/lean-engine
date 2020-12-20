package org.lean.presentation.connector.types.passthrough;

import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.row.IRowMeta;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@JsonDeserialize( as = LeanPassthroughConnector.class )
@LeanConnectorPlugin(
  id="PassthroughConnector",
  name="A passthrough connector",
  description = "Simply passes all the rows of the selected data source"
)
public class LeanPassthroughConnector extends LeanBaseConnector implements ILeanConnector {

  @JsonIgnore
  protected ArrayBlockingQueue<Object> finishedQueue;

  public LeanPassthroughConnector() {
    super("PassthroughConnector");
    finishedQueue = null;
  }

  public LeanPassthroughConnector( String sourceConnector ) {
    this();
    setSourceConnectorName( sourceConnector );
  }

  public LeanPassthroughConnector(LeanPassthroughConnector c) {
    super(c);
    // Beyond base connector no other metadata
  }

  public LeanPassthroughConnector clone() {
    return new LeanPassthroughConnector(this);
  }

  @Override public IRowMeta describeOutput( IDataContext dataContext) throws LeanException {
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if (connector==null) {
      throw new LeanException( "Unable to find connector source '"+getSourceConnectorName()+"' for passthrough connector" );
    }
    return connector.getConnector().describeOutput( dataContext );
  }

  @Override public void startStreaming( IDataContext dataContext) throws LeanException {

    // which connector do we read from?
    //
    LeanConnector sourceConnector = dataContext.getConnector( getSourceConnectorName() );
    if (sourceConnector==null) {
      throw new LeanException( "Unable to find connector source '"+getSourceConnectorName()+"' for passthrough connector" );
    }

    if (finishedQueue!=null) {
      throw new LeanException( "Please don't start streaming twice in your application, wait until the connector has finished sending rows" );
    }
    finishedQueue = new ArrayBlockingQueue<>(10);

    // Add a row listener to the parent connector
    //
    sourceConnector.getConnector().addRowListener(new PassthroughRowListener( this, finishedQueue ));

    // Now signal start streaming...
    //
    sourceConnector.getConnector().startStreaming( dataContext );
  }

  @Override public void waitUntilFinished() throws LeanException {
    try {
      while (finishedQueue.poll( 1, TimeUnit.DAYS )==null);
    } catch(InterruptedException e) {
      throw new LeanException( "Interrupted while waiting for more rows in connector", e );
    }
    finishedQueue=null;
  }

}
