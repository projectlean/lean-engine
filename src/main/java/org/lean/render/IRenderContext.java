package org.lean.render;

import org.lean.core.LeanColorRGB;
import org.lean.presentation.theme.LeanTheme;

/**
 * This describes the context in which components need to render their content
 */
public interface IRenderContext {

  /**
   * Look up the theme with the given name
   * @param themeName The name of the theme to look for
   * @return The theme or null if it couldn't be found
   */
  LeanTheme lookupTheme( String themeName);

  /**
   * Look up the color for a particular string in a given theme.
   * The color is guaranteed to be the same for the same value across the render context.
   *
   * @param themeName
   * @param value
   * @return The color for the theme and value
   */
  LeanColorRGB getStableColor(String themeName, String value);

}
