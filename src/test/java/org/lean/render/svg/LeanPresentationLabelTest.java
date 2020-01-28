package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.LabelPresentationUtil;
import org.junit.Test;

public class LeanPresentationLabelTest extends LeanPresentationTestBase {

  @Test
  public void testLabelRender() throws Exception {

    LeanPresentation presentation = LabelPresentationUtil.createLabelPresentation( 1000 );
    testRendering( presentation, "label_test");
  }
}