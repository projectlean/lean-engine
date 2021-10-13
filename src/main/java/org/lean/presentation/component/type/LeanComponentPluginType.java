package org.lean.presentation.component.type;

import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginMainClassType;

import java.util.Map;

@PluginMainClassType(ILeanComponent.class)
@PluginAnnotationType(LeanComponentPlugin.class)
public class LeanComponentPluginType extends BasePluginType<LeanComponentPlugin>
    implements IPluginType<LeanComponentPlugin> {

  private static LeanComponentPluginType pluginType;

  protected LeanComponentPluginType() {
    super(LeanComponentPlugin.class, "LeanComponentType", "Component Type");
  }

  protected LeanComponentPluginType(Class<LeanComponentPlugin> pluginType, String id, String name) {
    super(pluginType, id, name);
  }

  public static LeanComponentPluginType getInstance() {
    if (pluginType == null) {
      pluginType = new LeanComponentPluginType();
    }
    return pluginType;
  }

  @Override
  protected void registerNatives() throws HopPluginException {
    super.registerNatives();
  }

  @Override
  protected void addExtraClasses(
      Map<Class<?>, String> arg0, Class<?> arg1, LeanComponentPlugin arg2) {}

  @Override
  protected String extractID(LeanComponentPlugin annotation) {
    return annotation.id();
  }

  @Override
  protected String extractName(LeanComponentPlugin annotation) {
    return annotation.name();
  }

  @Override
  protected String extractDesc(LeanComponentPlugin annotation) {
    return annotation.description();
  }
}
