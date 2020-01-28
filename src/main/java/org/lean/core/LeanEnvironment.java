package org.lean.core;

import org.lean.core.exception.LeanException;
import org.lean.core.metastore.LeanMetaStore;
import org.lean.core.metastore.MetaStoreFactory;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.LeanComponentPluginType;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.LeanConnectorPluginType;
import org.lean.presentation.theme.LeanTheme;
import org.apache.hop.core.HopClientEnvironment;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.PluginTypeInterface;
import org.apache.hop.metastore.api.IMetaStore;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class LeanEnvironment {

  /** Has the Lean environment been initialized? */
  private static Boolean initialized;

  public static void init( IMetaStore metaStore ) throws LeanException {
    if ( initialized == null ) {

      LeanMetaStore.init( metaStore );

      try {
        HopClientEnvironment.init();
      } catch ( HopException e ) {
        throw new LeanException( "Unable to initialize the Hop client API environment", e );
      }

      try {
        PluginRegistry.addPluginType( LeanComponentPluginType.getInstance() );
        PluginRegistry.addPluginType( LeanConnectorPluginType.getInstance() );

        PluginRegistry.init(true);

      } catch ( HopPluginException e ) {
        throw new LeanException( "Unable to initialize plugin registry", e );
      }

      MetaStoreFactory<LeanPresentation> presentationFactory = new MetaStoreFactory<LeanPresentation>( LeanPresentation.class, metaStore, Constants.NAMESPACE );
      LeanMetaStore.registerFactory( LeanPresentation.class, presentationFactory );

      MetaStoreFactory<LeanConnector> connectorFactory = new MetaStoreFactory<>( LeanConnector.class, metaStore, Constants.NAMESPACE );
      LeanMetaStore.registerFactory( LeanConnector.class, connectorFactory );

      MetaStoreFactory<LeanComponent> componentFactory = new MetaStoreFactory<>( LeanComponent.class, metaStore, Constants.NAMESPACE );
      LeanMetaStore.registerFactory( LeanComponent.class, componentFactory );

      MetaStoreFactory<LeanDatabaseConnection> dbFactory = new MetaStoreFactory<>( LeanDatabaseConnection.class, metaStore, Constants.NAMESPACE );
      LeanMetaStore.registerFactory( LeanDatabaseConnection.class, dbFactory );

      MetaStoreFactory<LeanTheme> themeFactory = new MetaStoreFactory<>(LeanTheme.class, metaStore, Constants.NAMESPACE);
      LeanMetaStore.registerFactory( LeanTheme.class, themeFactory);

      presentationFactory.addNameFactory( Constants.METASTORE_FACTORY_NAME_CONNECTORS, connectorFactory );
      presentationFactory.addNameFactory( Constants.METASTORE_FACTORY_NAME_COMPONENTS, componentFactory );
      presentationFactory.addNameFactory( Constants.METASTORE_FACTORY_NAME_DATABASES, dbFactory );
      presentationFactory.addNameFactory( Constants.METASTORE_FACTORY_NAME_PRESENTATION, presentationFactory);

      initialized = true;
    }
  }

  public static void addPluginClasses( Class<? extends PluginTypeInterface> pluginTypeClass, Class<?> mainPluginClass) throws HopPluginException {

    PluginRegistry registry = PluginRegistry.getInstance();

    PluginTypeInterface pluginType = registry.getPluginType( pluginTypeClass );

    PluginAnnotationType pluginAnnotationType = pluginTypeClass.getAnnotation( PluginAnnotationType.class );
    Class<? extends Annotation> annotationClass = pluginAnnotationType.value();
    Annotation pluginAnnotation = mainPluginClass.getAnnotation( annotationClass );
    if (pluginAnnotation==null) {
      throw new HopPluginException( "The plugin annotation "+annotationClass.getName()+" was not found in class"+mainPluginClass.getName() );
    }
    pluginType.handlePluginAnnotation( mainPluginClass, pluginAnnotation, new ArrayList<>( ), true, null );
  }
}
