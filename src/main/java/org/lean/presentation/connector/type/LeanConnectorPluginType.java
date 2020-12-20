package org.lean.presentation.connector.type;

import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginMainClassType;

import java.util.Map;

@PluginMainClassType(ILeanConnector.class)
@PluginAnnotationType(LeanConnectorPlugin.class)
public class LeanConnectorPluginType extends BasePluginType<LeanConnectorPlugin>
    implements IPluginType<LeanConnectorPlugin> {

  private static LeanConnectorPluginType pluginType;

  protected LeanConnectorPluginType() {
    super(LeanConnectorPlugin.class, "LeanConnector", "Connector");
    populateFolders("connectors");
  }

  protected LeanConnectorPluginType(Class<LeanConnectorPlugin> pluginType, String id, String name) {
    super(pluginType, id, name);
  }

  public static LeanConnectorPluginType getInstance() {
    if (pluginType == null) {
      pluginType = new LeanConnectorPluginType();
    }
    return pluginType;
  }

  @Override
  protected void addExtraClasses(
      Map<Class<?>, String> arg0, Class<?> arg1, LeanConnectorPlugin leanConnectorPlugin) {}

  @Override
  protected String extractID(LeanConnectorPlugin annotation) {
    return annotation.id();
  }

  @Override
  protected String extractName(LeanConnectorPlugin annotation) {
    return annotation.name();
  }

  @Override
  protected String extractDesc(LeanConnectorPlugin annotation) {
    return annotation.description();
  }
}
