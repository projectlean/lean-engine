package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;

public class LeanPresentationLineChartTest extends LeanPresentationTestBase {

  @Test
  public void testLineChartRender() throws Exception {

    LeanPresentation presentation =
        new LineChartPresentationUtil(metadataProvider, variables)
            .createLineChartPresentation(4000);
    testRendering(presentation, "line_chart_test");
  }
}
