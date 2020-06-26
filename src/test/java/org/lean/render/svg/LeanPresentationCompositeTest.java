package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.CompositePresentationUtil;
import org.junit.Test;

public class LeanPresentationCompositeTest extends LeanPresentationTestBase {
  
  @Test
  public void testCompositeRender() throws Exception {

    LeanPresentation presentation = new CompositePresentationUtil( metadataProvider ).createSimpleCompositePresentation( 8000 );
    testRendering( presentation, "composite_test");
  }

}