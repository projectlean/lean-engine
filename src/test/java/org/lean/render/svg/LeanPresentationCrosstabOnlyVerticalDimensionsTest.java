package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;
import org.junit.Test;

public class LeanPresentationCrosstabOnlyVerticalDimensionsTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRenderOnlyVerticalDimensions() throws Exception {

    LeanPresentation presentation = new CrosstabPresentationUtil( metadataProvider, variables ).createCrosstabPresentationOnlyVerticalDimensions( 3100 );
    testRendering( presentation, "crosstab_verticals_test");
  }
}