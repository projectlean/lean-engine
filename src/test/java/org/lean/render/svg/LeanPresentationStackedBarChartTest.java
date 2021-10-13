package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.BarChartPresentationUtil;

public class LeanPresentationStackedBarChartTest extends LeanPresentationTestBase {

  @Test
  public void testBarChartRender() throws Exception {

    LeanPresentation presentation =
        new BarChartPresentationUtil(metadataProvider, variables)
            .createStackedBarChartPresentation(4300);
    testRendering(presentation, "bar_chart_stacked_test");
  }
}
