package org.lean.presentation.connector.types.chain;

import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.types.passthrough.PassthroughRowListener;
import org.lean.presentation.datacontext.ChainDataContext;
import org.lean.presentation.datacontext.IDataContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.row.RowMetaInterface;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@JsonDeserialize( as = LeanChainConnector.class )
public class LeanChainConnector extends LeanBaseConnector implements ILeanConnector {

  public static final String STRING_LAST_CONNECTOR_NAME = "_RESULT_OF_CHAIN_";

  @MetaStoreAttribute
  private List<ILeanConnector> connectors;

  @JsonIgnore
  protected ArrayBlockingQueue<Object> finishedQueue;

  public LeanChainConnector() {
    super( "ChainConnector" );
    finishedQueue = null;
    connectors = new ArrayList<>();
  }

  public LeanChainConnector(LeanChainConnector c) {
    super(c);
    connectors = new ArrayList<>(  );
    for (ILeanConnector connector : c.connectors) {
      connectors.add(connector.clone());
    }
  }

  public LeanChainConnector clone() {
    return new LeanChainConnector( this );
  }

  public LeanChainConnector( String sourceConnectorName, List<ILeanConnector> connectors ) {
    this();
    super.sourceConnectorName = sourceConnectorName;
    this.connectors = connectors;
  }

  @Override public RowMetaInterface describeOutput( IDataContext dataContext ) throws LeanException {

    // Validate input first
    //
    LeanConnector connector = dataContext.getConnector( getSourceConnectorName() );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector source '" + getSourceConnectorName() + "' for passthrough connector" );
    }

    // Get the output after chaining...
    //
    ChainDataContext chainDataContext = createChainContext( dataContext );

    // Describe output of last connector in chain
    //
    LeanConnector lastConnector = chainDataContext.getLastConnector();
    return lastConnector.getConnector().describeOutput( dataContext );
  }

  public ChainDataContext createChainContext( IDataContext parentDataContext) {
    // We want to chain all the connectors, give them sampledata names,
    //
    ChainDataContext chainDataContext = new ChainDataContext( parentDataContext );

    String previousName = null;
    for ( int i = 0; i < connectors.size(); i++ ) {
      ILeanConnector connector = connectors.get( i );
      String connectorName;
      if ( i == connectors.size() - 1 ) {
        // Last connector
        connectorName = STRING_LAST_CONNECTOR_NAME;
      } else {
        connectorName = "__ChainConnector_" + i;
      }
      LeanConnector leanConnector = new LeanConnector( connectorName, connector );
      if ( i == 0 ) {
        connector.setSourceConnectorName( getSourceConnectorName() );
      } else {
        connector.setSourceConnectorName( previousName );
      }
      chainDataContext.addConnector( leanConnector );
      previousName = connectorName;
    }

    return chainDataContext;
  }

  @Override public void startStreaming( IDataContext dataContext ) throws LeanException {

    // which connector do we read from?
    //
    LeanConnector sourceConnector = dataContext.getConnector( getSourceConnectorName() );
    if ( sourceConnector == null ) {
      throw new LeanException( "Unable to find source '" + getSourceConnectorName() + "' for chain connector" );
    }

    // Chain the rest...
    //
    ChainDataContext chainDataContext = createChainContext( dataContext );
    LeanConnector lastConnector = chainDataContext.getLastConnector();

    if ( finishedQueue != null ) {
      throw new LeanException( "Please don't start streaming twice in your application, wait until the connector has finished sending rows" );
    }
    finishedQueue = new ArrayBlockingQueue<>( 10 );

    // Add a row listener to the last connector to pass the data to the listeners of this connector
    //
    lastConnector.getConnector().addRowListener( new PassthroughRowListener( this, finishedQueue ) );

    // Now signal start streaming...
    //
    lastConnector.getConnector().startStreaming( chainDataContext );
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
   * Gets connectors
   *
   * @return value of connectors
   */
  public List<ILeanConnector> getConnectors() {
    return connectors;
  }

  /**
   * @param connectors The connectors to set
   */
  public void setConnectors( List<ILeanConnector> connectors ) {
    this.connectors = connectors;
  }
}
