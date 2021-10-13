package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.GroupCompositePresentationUtil;

public class LeanPresentationGroupCompositeTest extends LeanPresentationTestBase {

  @Test
  public void testGroupCompositeRender() throws Exception {

    LeanPresentation presentation =
        new GroupCompositePresentationUtil(metadataProvider, variables)
            .createGroupCompositePresentation(8000);
    testRendering(presentation, "grouped_composite_test");
  }
}
