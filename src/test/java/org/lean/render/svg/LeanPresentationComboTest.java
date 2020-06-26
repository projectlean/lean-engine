package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.ComboPresentationUtil;
import org.junit.Test;

public class LeanPresentationComboTest extends LeanPresentationTestBase {

  @Test
  public void testComboRender() throws Exception {

    LeanPresentation presentation = new ComboPresentationUtil( metadataProvider ).createComboPresentation( 3000 );
    testRendering( presentation, "combo_test");
  }
}