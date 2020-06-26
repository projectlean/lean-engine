package org.lean.presentation.connector.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.datacontext.IDataContext;

import java.util.ArrayList;
import java.util.List;

public abstract class LeanBaseConnector implements ILeanConnector {

  @HopMetadataProperty
  @JsonProperty
  protected String pluginId;

  @HopMetadataProperty
  @JsonProperty
  protected String sourceConnectorName;

  @JsonIgnore
  protected List<ILeanRowListener> rowListeners;

  public LeanBaseConnector( String pluginId ) {
    this.pluginId = pluginId;
    rowListeners = new ArrayList<>();
  }

  public LeanBaseConnector( LeanBaseConnector c ) {
    this.pluginId = c.pluginId;
    this.sourceConnectorName = c.sourceConnectorName;
    // We don't copy over the listeneres!
    //
    this.rowListeners = new ArrayList<>();
  }

  public abstract LeanBaseConnector clone();

  /**
   * @return Null if the dialog class is determined automatically.  Otherwise returns the dialog class name.
   */
  @JsonIgnore
  public String getDialogClassname() {
    return null;
  }

  /**
   * Signal to all row listeners that no more rows will be forthcoming by writing a null row
   *
   * @throws LeanException
   */
  public void outputDone() throws LeanException {
    for ( ILeanRowListener rowListener : rowListeners ) {
      rowListener.rowReceived( null, null );
    }
  }

  public void passToRowListeners( IRowMeta rowMeta, Object[] rowData ) throws LeanException {
    for ( ILeanRowListener rowListener : rowListeners ) {
      rowListener.rowReceived( rowMeta, rowData );
    }
  }

  public abstract void startStreaming( IDataContext dataContext ) throws LeanException;

  public abstract void waitUntilFinished() throws LeanException;

  @Override
  public void addRowListener( ILeanRowListener rowListener ) throws LeanException {
    rowListeners.add( rowListener );
  }

  @Override public void removeDataListener( ILeanRowListener rowListener ) {
    rowListeners.remove( rowListener );
  }

  /**
   * Gets pluginId
   *
   * @return value of pluginId
   */
  public String getPluginId() {
    return pluginId;
  }

  /**
   * @param pluginId The pluginId to set
   */
  public void setPluginId( String pluginId ) {
    this.pluginId = pluginId;
  }

  /**
   * Gets sourceConnectorName
   *
   * @return value of sourceConnectorName
   */
  public String getSourceConnectorName() {
    return sourceConnectorName;
  }

  /**
   * @param sourceConnectorName The sourceConnectorName to set
   */
  public void setSourceConnectorName( String sourceConnectorName ) {
    this.sourceConnectorName = sourceConnectorName;
  }

  /**
   * Gets rowListeners
   *
   * @return value of rowListeners
   */
  public List<ILeanRowListener> getRowListeners() {
    return rowListeners;
  }

  /**
   * @param rowListeners The rowListeners to set
   */
  public void setRowListeners( List<ILeanRowListener> rowListeners ) {
    this.rowListeners = rowListeners;
  }
}
