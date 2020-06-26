package org.lean.presentation.connector.type;

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

@PluginMainClassType( ILeanConnector.class )
@PluginAnnotationType( LeanConnectorPlugin.class )
public class LeanConnectorPluginType extends BaseLeanPluginType<LeanConnectorPlugin> implements IPluginType<LeanConnectorPlugin> {

  private static LeanConnectorPluginType pluginType;

  protected LeanConnectorPluginType() {
    super( LeanConnectorPlugin.class, "LeanConnector", "Connector" );
    populateFolders( "connectors" );
  }

  protected LeanConnectorPluginType( Class<LeanConnectorPlugin> pluginType, String id, String name ) {
    super( pluginType, id, name );
  }

  public static LeanConnectorPluginType getInstance() {
    if ( pluginType == null ) {
      pluginType = new LeanConnectorPluginType();
    }
    return pluginType;
  }

  @Override
  protected void addExtraClasses( Map<Class<?>, String> arg0, Class<?> arg1, LeanConnectorPlugin leanConnectorPlugin ) {
  }

  @Override
  protected String extractID( LeanConnectorPlugin annotation ) {
    return annotation.id();
  }

  @Override
  protected String extractName( LeanConnectorPlugin annotation ) {
    return annotation.name();
  }


  /**
   * Scan & register internal step plugins
   */
  protected void registerNatives() throws HopPluginException {
    // Scan the native steps...
    //
    String componentsXmlFile = Constants.XML_FILE_LEAN_CONNECTOR_PLUGINS;
    String alternative = System.getProperty( Constants.LEAN_CORE_CONNECTORS_FILE, null );
    if ( !StringUtils.isEmpty( alternative ) ) {
      componentsXmlFile = alternative;
    }

    // Load the plugins for this file...
    //
    try {
      loadPluginsFromXmlFile( componentsXmlFile, alternative, "connectors", "connector" );
    } catch ( HopException e ) {
      throw new HopPluginException( "Unable to read the lean connector plugins XML file: " + componentsXmlFile, e );
    }
  }

}
