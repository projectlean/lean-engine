package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.CompositePresentationUtil;

public class LeanPresentationCompositeTest extends LeanPresentationTestBase {

  @Test
  public void testCompositeRender() throws Exception {

    LeanPresentation presentation =
        new CompositePresentationUtil(metadataProvider, variables)
            .createSimpleCompositePresentation(8000);
    testRendering(presentation, "composite_test");
  }
}
