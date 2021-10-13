package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;

public class LeanPresentationLineChartSeriesTest extends LeanPresentationTestBase {

  @Test
  public void testLineChartSeriesRender() throws Exception {

    LeanPresentation presentation =
        new LineChartPresentationUtil(metadataProvider, variables)
            .createLineChartSeriesPresentation(4100);
    testRendering(presentation, "line_chart_series_test");
  }
}
