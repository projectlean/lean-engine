package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.BarChartPresentationUtil;
import org.junit.Test;

public class LeanPresentationStackedBarChartTest extends LeanPresentationTestBase {

  @Test
  public void testBarChartRender() throws Exception {

    LeanPresentation presentation = BarChartPresentationUtil.createStackedBarChartPresentation( 4300 );
    testRendering(presentation, "bar_chart_stacked_test");
  }


}