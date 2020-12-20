package org.lean.core;

import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.junit.Test;
import org.lean.presentation.component.type.LeanComponentPluginType;
import org.lean.presentation.connector.type.LeanConnectorPluginType;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LeanEnvironmentTest {

  @Test
  public void testInit() throws Exception {

    // Load all plugins, initialize environment
    //
    LeanEnvironment.init();

    PluginRegistry registry = PluginRegistry.getInstance();

    // Check Component plugin type...
    //
    IPluginType leanComponentPluginType = registry.getPluginType( LeanComponentPluginType.class );
    assertNotNull( "Component plugin type not found ", leanComponentPluginType );
    IPlugin leanLabelComponent = registry.findPluginWithId( LeanComponentPluginType.class, "LeanLabelComponent" );
    assertNotNull( "Label component not found", leanLabelComponent );

    List<IPlugin> componentPlugins = registry.getPlugins( LeanComponentPluginType.class );
    assertTrue( "Plugins list empty", !componentPlugins.isEmpty() );


    // Check connector plugin type...
    //
    IPluginType leanConnectorPluginType = registry.getPluginType( LeanConnectorPluginType.class );
    assertNotNull( "Data connector plugin type not found ", leanConnectorPluginType );
    IPlugin sampleDataConnector = registry.findPluginWithId( LeanConnectorPluginType.class, "SampleDataConnector" );
    assertNotNull( "Sample data connector plugin type not found", sampleDataConnector );

    List<IPlugin> connectorPlugins = registry.getPlugins( LeanConnectorPluginType.class );
    assertTrue( "Plugins list empty", !connectorPlugins.isEmpty() );

    // Check the metadata plugins
    //

  }
}