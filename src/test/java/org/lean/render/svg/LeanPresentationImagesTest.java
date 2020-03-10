package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.ComboPresentationUtil;
import org.lean.util.ImagesPresentationUtil;

public class LeanPresentationImagesTest extends LeanPresentationTestBase {

  @Test
  public void testImagesRender() throws Exception {

    LeanPresentation presentation = ImagesPresentationUtil.createImagesPresentation( 10000 );
    testRendering( presentation, "images_test");
  }
}