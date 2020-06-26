package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.BarChartPresentationUtil;
import org.junit.Test;

public class LeanPresentationBarChartTest extends LeanPresentationTestBase {

  @Test
  public void testBarChartRender() throws Exception {

    LeanPresentation presentation = new BarChartPresentationUtil(metadataProvider).createBarChartPresentation( 4200 );
    testRendering(presentation, "bar_chart_test");
  }


}