package org.lean.presentation.connector.type;

import org.lean.core.ILeanDataStreaming;
import org.lean.core.ILeanRowListener;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize( using = ILeanConnectorDeserializer.class )
public interface ILeanConnector extends ILeanDataStreaming, Cloneable {

  /**
   * @return The ID of the component type plugin
   */
  String getPluginId();

  /**
   * Set the ID of the component type
   * @param pluginId The ID to set.
   */
  void setPluginId( String pluginId );

  /**
   * Gets rowListeners
   *
   * @return value of rowListeners
   */
  List<ILeanRowListener> getRowListeners();

  /**
   * @param rowListeners The rowListeners to set
   */
  void setRowListeners( List<ILeanRowListener> rowListeners );

  /**
   * @return The source connector for this connector, if any
   */
  String getSourceConnectorName();

  /**
   * @param sourceConnectorName the source connector to set
   */
  void setSourceConnectorName( String sourceConnectorName );

  /**
   * @return a copy of the metadata of this connector
   */
  ILeanConnector clone();

  /**
   * @return Null if the dialog class is determined automatically.  Otherwise returns the dialog class name.
   */
  String getDialogClassname();
}
