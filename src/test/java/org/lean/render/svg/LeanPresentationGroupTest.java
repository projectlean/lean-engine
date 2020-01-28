package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.GroupPresentationUtil;
import org.junit.Test;

public class LeanPresentationGroupTest extends LeanPresentationTestBase {
  
  @Test
  public void testGroupRender() throws Exception {

    LeanPresentation presentation = GroupPresentationUtil.createSimpleGroupedLabelPresentation( 7000 );
    testRendering( presentation, "group_test");
  }

}