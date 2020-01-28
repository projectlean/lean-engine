package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;
import org.junit.Test;

public class LeanPresentationCrosstabTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRender() throws Exception {

    LeanPresentation presentation = CrosstabPresentationUtil.createCrosstabPresentation( 3000 );
    testRendering( presentation, "crosstab_test");
  }
}