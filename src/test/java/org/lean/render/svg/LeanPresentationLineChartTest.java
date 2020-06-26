package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.LineChartPresentationUtil;
import org.junit.Test;

public class LeanPresentationLineChartTest extends LeanPresentationTestBase {

  @Test
  public void testLineChartRender() throws Exception {

    LeanPresentation presentation = new LineChartPresentationUtil( metadataProvider ).createLineChartPresentation( 4000 );
    testRendering(presentation, "line_chart_test");
  }


}