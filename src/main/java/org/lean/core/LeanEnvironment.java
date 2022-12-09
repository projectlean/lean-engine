package org.lean.core;

import org.apache.hop.core.HopClientEnvironment;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.plugins.PluginRegistry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.component.type.LeanComponentPluginType;
import org.lean.presentation.connector.type.LeanConnectorPluginType;

public class LeanEnvironment {

  /** Has the Lean environment been initialized? */
  private static Boolean initialized;

  public static void init() throws LeanException {
    if (initialized == null) {

      try {
        HopClientEnvironment.init();
      } catch (HopException e) {
        throw new LeanException("Unable to initialize the Hop client API environment", e);
      }

      try {
        PluginRegistry.addPluginType(LeanComponentPluginType.getInstance());
        PluginRegistry.addPluginType(LeanConnectorPluginType.getInstance());
        PluginRegistry.init();
      } catch (Exception e) {
        throw new LeanException("Unable to register lean plugin types", e);
      }

      initialized = true;
    }
  }
}
