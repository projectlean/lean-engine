package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;

public class LeanPresentationChartTest extends LeanPresentationTestBase {

  @Test
  public void testChartRender() throws Exception {

    LeanPresentation presentation = new LineChartPresentationUtil( metadataProvider ).createLineChartPresentation( 5000 );
    testRendering( presentation, "chart_test" );
  }

  @Test
  public void testChartNoLabelsRender() throws Exception {

    LeanPresentation presentation = new LineChartPresentationUtil( metadataProvider ).createLineChartNoLabelsPresentation( 5100 );
    testRendering( presentation, "chart_trend_test" );
  }
}