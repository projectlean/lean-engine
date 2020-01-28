package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.GroupCompositePresentationUtil;
import org.junit.Test;

public class LeanPresentationGroupCompositeTest extends LeanPresentationTestBase {
  
  @Test
  public void testGroupCompositeRender() throws Exception {

    LeanPresentation presentation = GroupCompositePresentationUtil.createGroupCompositePresentation( 8000 );
    testRendering( presentation, "grouped_composite_test");
  }

}