package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LabelPresentationUtil;

public class LeanPresentationLabelTest extends LeanPresentationTestBase {

  @Test
  public void testLabelRender() throws Exception {

    LeanPresentation presentation =
        new LabelPresentationUtil(metadataProvider, variables).createLabelPresentation(1000);
    testRendering(presentation, "label_test");
  }
}
