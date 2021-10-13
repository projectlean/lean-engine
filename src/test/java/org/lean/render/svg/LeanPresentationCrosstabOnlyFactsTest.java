package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.CrosstabPresentationUtil;

public class LeanPresentationCrosstabOnlyFactsTest extends LeanPresentationTestBase {

  @Test
  public void testCrosstabRenderOnlyFacts() throws Exception {

    LeanPresentation presentation =
        new CrosstabPresentationUtil(metadataProvider, variables)
            .createCrosstabPresentationOnlyFacts(3300);
    testRendering(presentation, "crosstab_only_facts");
  }
}
