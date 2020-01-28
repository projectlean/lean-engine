package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;
import org.junit.Test;

public class LeanPresentationChartTest extends LeanPresentationTestBase {

  @Test
  public void testChartRender() throws Exception {

    LeanPresentation presentation = LineChartPresentationUtil.createLineChartPresentation( 5000 );
    testRendering( presentation, "chart_test");
  }

  @Test
  public void testChartNoLabelsRender() throws Exception {

    LeanPresentation presentation = LineChartPresentationUtil.createLineChartNoLabelsPresentation( 5100 );
    testRendering( presentation, "chart_trend_test");
  }
}