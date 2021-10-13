package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.ComboPresentationUtil;

public class LeanPresentationComboTest extends LeanPresentationTestBase {

  @Test
  public void testComboRender() throws Exception {

    LeanPresentation presentation =
        new ComboPresentationUtil(metadataProvider, variables).createComboPresentation(3000);
    testRendering(presentation, "combo_test");
  }
}
