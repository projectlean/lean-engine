package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;
import org.junit.Test;

public class LeanPresentationCrosstabOnlyFactsTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRenderOnlyFacts() throws Exception {

    LeanPresentation presentation = CrosstabPresentationUtil.createCrosstabPresentationOnlyFacts( 3300 );
    testRendering( presentation, "crosstab_only_facts");
  }

}