package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;
import org.junit.Test;

public class LeanPresentationCrosstabOnlyHorizontalDimensionsTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRenderOnlyHorizontalDimensions() throws Exception {

    LeanPresentation presentation = new CrosstabPresentationUtil( metadataProvider, variables ).createCrosstabPresentationOnlyHorizontalDimensions( 3200 );
    testRendering( presentation, "crosstab_horizontals_test");
  }

}