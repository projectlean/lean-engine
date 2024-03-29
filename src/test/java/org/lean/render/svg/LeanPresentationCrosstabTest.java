package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;

public class LeanPresentationCrosstabTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRender() throws Exception {

    LeanPresentation presentation =
        new CrosstabPresentationUtil(metadataProvider, variables).createCrosstabPresentation(3000);
    testRendering(presentation, "crosstab_test");
  }
}
