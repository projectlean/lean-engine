package org.lean.core;

import org.lean.core.metastore.LeanMetaStoreUtil;
import org.lean.presentation.component.type.LeanComponentPluginType;
import org.lean.presentation.connector.type.LeanConnectorPluginType;
import org.junit.Test;
import org.apache.hop.core.plugins.PluginInterface;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.PluginTypeInterface;
import org.apache.hop.metastore.api.IMetaStore;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LeanEnvironmentTest {

  @Test
  public void testInit() throws Exception {

    // Load all plugins, initialize environment
    //
    IMetaStore metaStore = LeanMetaStoreUtil.createTestMetaStore( "Test" );
    LeanEnvironment.init(metaStore);

    PluginRegistry registry = PluginRegistry.getInstance();

    // Check Component plugin type...
    //
    PluginTypeInterface leanComponentPluginType = registry.getPluginType( LeanComponentPluginType.class );
    assertNotNull( "Component plugin type not found ", leanComponentPluginType );
    PluginInterface leanLabelComponent = registry.findPluginWithId( LeanComponentPluginType.class, "LeanLabelComponent" );
    assertNotNull( "Label component not found", leanLabelComponent );

    List<PluginInterface> componentPlugins = registry.getPlugins( LeanComponentPluginType.class );
    assertTrue( "Plugins list empty", !componentPlugins.isEmpty() );


    // Check connector plugin type...
    //
    PluginTypeInterface leanConnectorPluginType = registry.getPluginType( LeanConnectorPluginType.class );
    assertNotNull( "Data connector plugin type not found ", leanConnectorPluginType );
    PluginInterface sampleDataConnector = registry.findPluginWithId( LeanConnectorPluginType.class, "SampleDataConnector" );
    assertNotNull( "Sample data connector plugin type not found", sampleDataConnector );

    List<PluginInterface> connectorPlugins = registry.getPlugins( LeanConnectorPluginType.class );
    assertTrue( "Plugins list empty", !connectorPlugins.isEmpty() );

    LeanMetaStoreUtil.cleanupTestMetaStore( metaStore );
  }
}