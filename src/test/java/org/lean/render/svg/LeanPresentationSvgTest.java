package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.ImagesPresentationUtil;
import org.lean.util.SvgPresentationUtil;

public class LeanPresentationSvgTest extends LeanPresentationTestBase {

  @Test
  public void testSvgRender() throws Exception {

    LeanPresentation presentation =
        new SvgPresentationUtil(metadataProvider, variables).createSvgPresentation(10500);
    testRendering(presentation, "svg_test");
  }
}
