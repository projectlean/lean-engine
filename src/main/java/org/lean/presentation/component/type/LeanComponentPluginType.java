package org.lean.presentation.component.type;

import org.lean.core.BaseLeanPluginType;
import org.lean.core.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginMainClassType;
import org.apache.hop.core.plugins.IPluginType;

import java.lang.annotation.Annotation;
import java.util.Map;

@PluginMainClassType( ILeanComponent.class )
@PluginAnnotationType( LeanComponentPlugin.class )
public class LeanComponentPluginType extends BaseLeanPluginType<LeanComponentPlugin> implements IPluginType<LeanComponentPlugin> {

  private static LeanComponentPluginType pluginType;

  protected LeanComponentPluginType() {
    super( LeanComponentPlugin.class, "LeanComponentType", "Component Type" );
    populateFolders( "components_types" );
  }

  protected LeanComponentPluginType( Class<LeanComponentPlugin> pluginType, String id, String name ) {
    super( pluginType, id, name );
  }

  public static LeanComponentPluginType getInstance() {
    if ( pluginType == null ) {
      pluginType = new LeanComponentPluginType();
    }
    return pluginType;
  }

  @Override
  protected void addExtraClasses( Map<Class<?>, String> arg0, Class<?> arg1, LeanComponentPlugin arg2 ) {
  }

  @Override
  protected String extractID( LeanComponentPlugin annotation ) {
    return annotation.id();
  }

 @Override
  protected String extractName( LeanComponentPlugin annotation ) {
    return annotation.name();
  }

  /**
   * Scan & register internal step plugins
   */
  protected void registerNatives() throws HopPluginException {
    // Scan the native steps...
    //
    String componentsXmlFile = Constants.XML_FILE_LEAN_COMPONENT_PLUGINS;
    String alternative = System.getProperty( Constants.LEAN_CORE_COMPONENTS_FILE, null );
    if ( !StringUtils.isEmpty( alternative ) ) {
      componentsXmlFile = alternative;
    }

    // Load the plugins for this file...
    //
    try {
      loadPluginsFromXmlFile( componentsXmlFile, alternative, "components", "component" );
    } catch ( HopException e ) {
      throw new HopPluginException( "Unable to read the lean component plugins XML file: " + componentsXmlFile, e );
    }
  }
}
