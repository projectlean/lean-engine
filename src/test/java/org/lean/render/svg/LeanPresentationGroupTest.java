package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.GroupPresentationUtil;

public class LeanPresentationGroupTest extends LeanPresentationTestBase {

  @Test
  public void testGroupRender() throws Exception {

    LeanPresentation presentation =
        new GroupPresentationUtil(metadataProvider, variables)
            .createSimpleGroupedLabelPresentation(7000);
    testRendering(presentation, "group_test");
  }
}
