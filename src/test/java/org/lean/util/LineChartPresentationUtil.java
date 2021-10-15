package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.AggregationMethod;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.chart.LeanLineChartComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutBuilder;

import java.util.Arrays;

public class LineChartPresentationUtil extends BasePresentationUtil {

  public static final String LINE_CHART_NAME = "LineChart";

  public LineChartPresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public static LeanLineChartComponent createColorRandomLineChart() {
    LeanLineChartComponent lineChart =
        new LeanLineChartComponent(BasePresentationUtil.CONNECTOR_SAMPLE_ROWS);
    lineChart.setHorizontalDimensions(
        Arrays.asList(
            new LeanDimension(
                "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE)));
    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.MIDDLE,
            AggregationMethod.SUM,
            "0.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.MIDDLE);
    sumFact.setFormatMask("0.00");
    lineChart.setFacts(Arrays.asList(sumFact));
    lineChart.setHorizontalMargin(10);
    lineChart.setVerticalMargin(10);
    lineChart.setBorder(true);
    lineChart.setBackground(false);
    // lineChart.setUsingZeroBaseline( true );

    lineChart.setTitle("Random by Color");

    return lineChart;
  }

  public static LeanLineChartComponent createNoLabelsTrendChart() {
    LeanLineChartComponent lineChart =
        new LeanLineChartComponent(BasePresentationUtil.CONNECTOR_SAMPLE_ROWS);

    LeanDimension nameDimension =
        new LeanDimension(
            "name", "Name", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE);
    lineChart.getHorizontalDimensions().add(nameDimension);
    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.MIDDLE,
            AggregationMethod.SUM,
            "0.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.MIDDLE);
    sumFact.setFormatMask("0.00");
    lineChart.setFacts(Arrays.asList(sumFact));
    lineChart.setHorizontalMargin(10);
    lineChart.setVerticalMargin(10);
    lineChart.setBorder(true);
    lineChart.setBackground(false);
    lineChart.setShowingHorizontalLabels(false);
    lineChart.setShowingVerticalLabels(false);
    lineChart.setDotSize(0);
    lineChart.setShowingAxisTicks(false);
    lineChart.setHorizontalMargin(0);
    lineChart.setVerticalMargin(0);
    lineChart.setTitle("Trend");

    return lineChart;
  }

  public static LeanLineChartComponent createNoLabelsTrendChartDetailed() {
    LeanLineChartComponent lineChart = createNoLabelsTrendChart();
    LeanDimension importantDimension =
        new LeanDimension(
            "important", "Important", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE);
    LeanDimension colorDimension =
        new LeanDimension(
            "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE);

    lineChart.getHorizontalDimensions().add(importantDimension);
    lineChart.getHorizontalDimensions().add(colorDimension);

    lineChart.setTitle("Very detailed");

    return lineChart;
  }

  public LeanPresentation createLineChartPresentation(int nr) throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Chart (" + nr + ")",
            "Chart " + nr + " description",
            1000,
            "Line chart filling the whole page");

    LeanLineChartComponent lineChart = createColorRandomLineChart();

    LeanComponent lineChartComponent = new LeanComponent(LINE_CHART_NAME, lineChart);
    lineChartComponent.setLayout(new LeanLayoutBuilder().all(5).build());

    // Add the table to the first page.
    //
    presentation.getPages().get(0).getComponents().add(lineChartComponent);

    return presentation;
  }

  public LeanPresentation createLineChartNoLabelsPresentation(int nr) throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Chart no labels (" + nr + ")",
            "Chart no labels " + nr + " description",
            1000,
            "Line char without labels filling page");

    LeanLineChartComponent lineChart = createNoLabelsTrendChart();
    lineChart.setDrawingCurvedTrendLine(true);

    LeanComponent lineChartComponent = new LeanComponent(LINE_CHART_NAME, lineChart);
    lineChartComponent.setLayout(new LeanLayoutBuilder().all(5).build());

    // Add the table to the first page.
    //
    presentation.getPages().get(0).getComponents().add(lineChartComponent);

    return presentation;
  }

  public LeanPresentation createLineChartSeriesPresentation(int nr) throws Exception {
    LeanPresentation presentation = createLineChartPresentation(nr);

    LeanLineChartComponent lineChart =
        (LeanLineChartComponent)
            presentation.getPages().get(0).findComponent(LINE_CHART_NAME).getComponent();

    lineChart.setVerticalDimensions(
        Arrays.asList(
            new LeanDimension(
                "country",
                "Country",
                LeanHorizontalAlignment.CENTER,
                LeanVerticalAlignment.MIDDLE)));

    lineChart.setTitle("Random value by Country and Color");
    lineChart.setDrawingCurvedTrendLine(true);

    LeanLabelComponent label =
        (LeanLabelComponent)
            presentation
                .getHeader()
                .findComponent(BasePresentationUtil.HEADER_MESSAGE_LABEL)
                .getComponent();
    label.setLabel("Line chart with series filling whole page");
    return presentation;
  }
}
