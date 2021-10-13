package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LabelPresentationUtil;

public class LeanPresentationLabelsTest extends LeanPresentationTestBase {

  @Test
  public void testLabelsRender() throws Exception {
    LeanPresentation presentation =
        new LabelPresentationUtil(metadataProvider, variables).createLabelsPresentation(1100);
    testRendering(presentation, "labels_test");
  }
}
