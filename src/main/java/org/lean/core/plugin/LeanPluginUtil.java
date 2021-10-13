package org.lean.core.plugin;

import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.lean.core.dialog.ILeanDialog;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;

import java.lang.reflect.Constructor;

public class LeanPluginUtil {

  public static ILeanDialog loadComponentDialogClass(
      IPlugin plugin,
      LeanPresentation presentation,
      LeanComponent component,
      String dialogClassName)
      throws Exception {

    PluginRegistry registry = PluginRegistry.getInstance();
    ClassLoader classLoader = registry.getClassLoader(plugin);

    // ILeanComponent leanComponent = (ILeanComponent) registry.loadClass( plugin );

    Class<?>[] paramClasses = new Class<?>[] {LeanPresentation.class, LeanComponent.class};
    Object[] paramArgs = new Object[] {presentation, component};

    Class<ILeanDialog> dialogClass = registry.getClass(plugin, dialogClassName);
    Constructor<ILeanDialog> dialogConstructor = dialogClass.getConstructor(paramClasses);

    return dialogConstructor.newInstance(paramArgs);
  }
}
