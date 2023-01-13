package org.lean.presentation.connector.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.metadata.api.HopMetadataObject;
import org.apache.hop.metadata.api.IHopMetadataObjectFactory;
import org.lean.core.ILeanDataStreaming;
import org.lean.core.ILeanRowListener;

@JsonDeserialize(using = ILeanConnectorDeserializer.class)
@HopMetadataObject(objectFactory = ILeanConnector.LeanConnectorObjectFactory.class)
public interface ILeanConnector extends ILeanDataStreaming, Cloneable {

  /**
   * @return The ID of the component type plugin
   */
  String getPluginId();

  /**
   * Set the ID of the component type
   *
   * @param pluginId The ID to set.
   */
  void setPluginId(String pluginId);

  /**
   * Gets rowListeners
   *
   * @return value of rowListeners
   */
  List<ILeanRowListener> getRowListeners();

  /**
   * @param rowListeners The rowListeners to set
   */
  void setRowListeners(List<ILeanRowListener> rowListeners);

  /**
   * @return The source connector for this connector, if any
   */
  String getSourceConnectorName();

  /**
   * @param sourceConnectorName the source connector to set
   */
  void setSourceConnectorName(String sourceConnectorName);

  /**
   * @return a copy of the metadata of this connector
   */
  ILeanConnector clone();

  /**
   * @return Null if the dialog class is determined automatically. Otherwise returns the dialog
   *     class name.
   */
  String getDialogClassname();

  final class LeanConnectorObjectFactory implements IHopMetadataObjectFactory {

    public LeanConnectorObjectFactory() {}

    @Override
    public Object createObject(String id, Object parentObject) throws HopException {
      if (id == null) {
        return null;
      }
      return PluginRegistry.getInstance()
          .loadClass(LeanConnectorPluginType.class, id, ILeanConnector.class);
    }

    @Override
    public String getObjectId(Object object) throws HopException {
      if (object == null) {
        return null;
      }
      if (!(object instanceof ILeanConnector)) {
        throw new HopException(
            "Invalid class to get a Lean Connector plugin ID from: " + object.getClass());
      }
      return ((ILeanConnector) object).getPluginId();
    }
  }
}
