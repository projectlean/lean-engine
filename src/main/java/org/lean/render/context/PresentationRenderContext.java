package org.lean.render.context;

import org.lean.presentation.LeanPresentation;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;

public class PresentationRenderContext extends SimpleRenderContext implements IRenderContext {

  private LeanPresentation presentation;

  public PresentationRenderContext() {
    super();
  }

  public PresentationRenderContext(LeanPresentation presentation) {
    this();
    this.presentation = presentation;
  }

  /**
   * @param themeName The name of the theme to look for or null if you want to use the default
   * @return The theme or null if none is found.
   */
  @Override
  public LeanTheme lookupTheme(String themeName) {

    // If no theme name is given, them we'll use the default of the presentation
    //
    if (themeName == null) {
      return presentation.getDefaultTheme();
    } else {
      return presentation.lookupTheme(themeName);
    }
  }

  /**
   * Gets presentation
   *
   * @return value of presentation
   */
  public LeanPresentation getPresentation() {
    return presentation;
  }

  /** @param presentation The presentation to set */
  public void setPresentation(LeanPresentation presentation) {
    this.presentation = presentation;
  }
}
