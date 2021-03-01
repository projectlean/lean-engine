package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;

public class LeanPresentationChartTest extends LeanPresentationTestBase {

  @Test
  public void testChartRender() throws Exception {

    LeanPresentation presentation = new LineChartPresentationUtil( metadataProvider, variables ).createLineChartPresentation( 5000 );
    testRendering( presentation, "chart_test" );
  }

  @Test
  public void testChartNoLabelsRender() throws Exception {

    LeanPresentation presentation = new LineChartPresentationUtil( metadataProvider, variables ).createLineChartNoLabelsPresentation( 5100 );
    testRendering( presentation, "chart_trend_test" );
  }
}