package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.BarChartPresentationUtil;

public class LeanPresentationBarChartTest extends LeanPresentationTestBase {

  @Test
  public void testBarChartRender() throws Exception {

    LeanPresentation presentation =
        new BarChartPresentationUtil(metadataProvider, variables).createBarChartPresentation(4200);
    testRendering(presentation, "bar_chart_test");
  }
}
