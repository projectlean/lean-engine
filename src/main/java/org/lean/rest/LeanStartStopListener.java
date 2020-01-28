package org.lean.rest;

import org.lean.core.LeanEnvironment;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.stores.xml.XmlMetaStore;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class LeanStartStopListener implements ServletContextListener {

  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {
      LeanServletContext.initialize( servletContextEvent.getServletContext() );

      String leanHome = System.getProperty( "java.io.tmpdir" );
      Logger.getGlobal().info("Using "+leanHome+" to store metastore and set Hop home");

      System.setProperty( "Hop_HOME", leanHome );
      IMetaStore metaStore = new XmlMetaStore( leanHome );
      LeanEnvironment.init( metaStore );
    } catch ( Exception e ) {
      throw new RuntimeException( "Unable to initialize Lean environment", e );
    }
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }

}
