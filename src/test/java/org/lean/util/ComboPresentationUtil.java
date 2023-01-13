package org.lean.util;

import java.util.Arrays;
import java.util.Collections;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.AggregationMethod;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.core.draw.DrawnItem;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.chart.LeanLineChartComponent;
import org.lean.presentation.component.types.crosstab.LeanCrosstabComponent;
import org.lean.presentation.component.types.svg.LeanSvgComponent;
import org.lean.presentation.component.types.svg.ScaleType;
import org.lean.presentation.interaction.LeanInteraction;
import org.lean.presentation.interaction.LeanInteractionAction;
import org.lean.presentation.interaction.LeanInteractionLocation;
import org.lean.presentation.interaction.LeanInteractionMethod;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutBuilder;
import org.lean.presentation.page.LeanPage;

public class ComboPresentationUtil extends BasePresentationUtil {

  public ComboPresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public LeanPresentation createComboPresentation(int nr) throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Combo (" + nr + ")",
            "Combo " + nr + " description",
            1000,
            "Layout test with charts basing position off crosstab");

    LeanPage pageOne = presentation.getPages().get(0);

    // Add a crosstab at the top left of the page.
    // This component is dynamically sized
    //
    {
      LeanCrosstabComponent crosstab = new LeanCrosstabComponent(CONNECTOR_SAMPLE_ROWS);
      crosstab.setHorizontalDimensions(
          Arrays.asList(
              new LeanDimension(
                  "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE),
              new LeanDimension(
                  "important", "?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE)));
      crosstab.setVerticalDimensions(
          Arrays.asList(
              new LeanDimension(
                  "name",
                  "Customer",
                  LeanHorizontalAlignment.RIGHT,
                  LeanVerticalAlignment.MIDDLE)));
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
      LeanFact countFact =
          new LeanFact(
              "name",
              "Count",
              LeanHorizontalAlignment.RIGHT,
              LeanVerticalAlignment.MIDDLE,
              AggregationMethod.COUNT,
              "0");
      countFact.setHorizontalAggregation(true);
      countFact.setHorizontalAggregationHeader("Total Count");
      countFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
      countFact.setHeaderVerticalAlignment(LeanVerticalAlignment.MIDDLE);
      crosstab.setFacts(Arrays.asList(sumFact, countFact));
      crosstab.setBackground(false);
      crosstab.setBorder(false);
      crosstab.setHorizontalMargin(3);
      crosstab.setVerticalMargin(2);
      crosstab.setEvenHeights(true);
      crosstab.setHeaderOnEveryPage(true);
      crosstab.setShowingVerticalTotals(true);
      crosstab.setShowingHorizontalTotals(true);
      LeanComponent crosstabComponent = new LeanComponent("Crosstab", crosstab);
      crosstabComponent.setLayout(new LeanLayout(0, 0));

      // Add the table to the first page.
      //
      pageOne.getComponents().add(crosstabComponent);
    }

    // Add a chart below the crosstab
    //
    {
      LeanLineChartComponent lineChart = LineChartPresentationUtil.createColorRandomLineChart();

      lineChart.setVerticalDimensions(
          Arrays.asList(
              new LeanDimension(
                  "country",
                  "Country",
                  LeanHorizontalAlignment.CENTER,
                  LeanVerticalAlignment.MIDDLE)));
      lineChart.setDrawingCurvedTrendLine(true);

      LeanComponent lineChartComponent = new LeanComponent("LineChart", lineChart);
      LeanLayout chartLayout =
          new LeanLayoutBuilder()
              .left()
              .topFromBottom("Crosstab", 0, 0)
              .bottom(0)
              .rightFromRight("Crosstab", 0, 0)
              .build();
      lineChartComponent.setLayout(chartLayout);

      // Add the table to the first page.
      //
      pageOne.getComponents().add(lineChartComponent);
    }

    // a detailed trend chart top-right
    {
      LeanLineChartComponent trendChart =
          LineChartPresentationUtil.createNoLabelsTrendChartDetailed();
      trendChart.setDrawingCurvedTrendLine(true);
      LeanComponent trendChartComponent = new LeanComponent("TrendChartDetailed", trendChart);
      // Setting imageSize forces chart on next page, comment out Right/Bottom attachments
      // lineChartComponent.setSize( new LeanSize( 600, 600 ) );
      LeanLayout trendChartLayout =
          new LeanLayoutBuilder()
              .beside("Crosstab", 5)
              .right()
              .bottom(new LeanAttachment("Crosstab", 0, 0, LeanAttachment.Alignment.BOTTOM))
              .build();
      trendChartComponent.setLayout(trendChartLayout);

      pageOne.getComponents().add(trendChartComponent);
    }

    // Add a rotated LEAN logo in the background
    //
    {
      LeanSvgComponent rotatedLabel = new LeanSvgComponent("lean-logo.svg", ScaleType.MAX);
      rotatedLabel.setBorder(false);
      LeanComponent imageComponent = new LeanComponent("Logo", rotatedLabel);
      imageComponent.setLayout(new LeanLayoutBuilder().top().right().bottom().build());

      LeanComponent labelComponent = new LeanComponent("RotatedLabel", rotatedLabel);
      LeanLayout labelLayout =
          new LeanLayoutBuilder().leftFromRight("Crosstab", 0, 15).right(-15).bottom(-35).build();
      labelComponent.setLayout(labelLayout);
      labelComponent.setRotation("15");
      pageOne.getComponents().add(labelComponent);
    }

    // Add test interactions...
    //
    presentation
        .getInteractions()
        .add(
            new LeanInteraction(
                new LeanInteractionMethod(true, false),
                new LeanInteractionLocation(
                    "LineChart",
                    null,
                    DrawnItem.DrawnItemType.ComponentItem.name(),
                    DrawnItem.Category.ChartSeriesLabel.name(),
                    Collections.emptyList()),
                new LeanInteractionAction(
                    LeanInteractionAction.ActionType.OPEN_PRESENTATION, "Other presentation")));

    return presentation;
  }
}
