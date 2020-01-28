package org.lean.presentation.datacontext;

import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.apache.hop.core.variables.VariableSpace;
import org.apache.hop.metastore.api.IMetaStore;

import java.util.HashMap;
import java.util.Map;

public class ChainDataContext implements IDataContext {

  private IDataContext parentDataContext;

  private Map<String, LeanConnector> connectorsMap;

  private LeanConnector lastConnector;

  public ChainDataContext() {
    connectorsMap = new HashMap<>();
  }

  public ChainDataContext( IDataContext parentDataContext ) {
    this.parentDataContext = parentDataContext;
    connectorsMap = new HashMap<>();
  }

  @Override public LeanConnector getConnector( String name ) throws LeanException {
    LeanConnector connector = parentDataContext.getConnector( name );
    if ( connector == null ) {
      connector = connectorsMap.get( name );
    }
    if (connector != null) {
      connector = new LeanConnector( connector );
    }
    return connector;
  }

  @Override public VariableSpace getVariableSpace() {
    return parentDataContext.getVariableSpace();
  }

  public void addConnector( LeanConnector leanConnector ) {
    this.lastConnector = leanConnector;
    connectorsMap.put(leanConnector.getName(), leanConnector);
  }

  /**
   * Gets parentDataContext
   *
   * @return value of parentDataContext
   */
  public IDataContext getParentDataContext() {
    return parentDataContext;
  }

  /**
   * @param parentDataContext The parentDataContext to set
   */
  public void setParentDataContext( IDataContext parentDataContext ) {
    this.parentDataContext = parentDataContext;
  }

  /**
   * Gets connectorsMap
   *
   * @return value of connectorsMap
   */
  public Map<String, LeanConnector> getConnectorsMap() {
    return connectorsMap;
  }

  /**
   * @param connectorsMap The connectorsMap to set
   */
  public void setConnectorsMap( Map<String, LeanConnector> connectorsMap ) {
    this.connectorsMap = connectorsMap;
  }

  /**
   * Gets lastConnector
   *
   * @return value of lastConnector
   */
  public LeanConnector getLastConnector() {
    return lastConnector;
  }

  /**
   * @param lastConnector The lastConnector to set
   */
  public void setLastConnector( LeanConnector lastConnector ) {
    this.lastConnector = lastConnector;
  }

  @Override public IMetaStore getMetaStore() {
    return parentDataContext.getMetaStore();
  }
}
