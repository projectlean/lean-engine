package org.lean.util;

import org.lean.core.AggregationMethod;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.chart.LeanBarChartComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;

import java.util.Arrays;

public class BarChartPresentationUtil extends BasePresentationUtil {


  public static final String LINE_CHART_NAME = "BarChart";

  public static LeanPresentation createBarChartPresentation( int nr ) throws Exception {
    LeanPresentation presentation = createBasePresentation(
      "BarChart (" + nr +")",
      "BarChart " + nr + " description",
      1000,
      "Bar chart filling whole page"
    );

    LeanBarChartComponent lineChart = createColorRandomBarChart();

    LeanComponent lineChartComponent = new LeanComponent( LINE_CHART_NAME, lineChart);
    LeanLayout chartLayout = new LeanLayout();
    // Fill the page
    chartLayout.setLeft(new LeanAttachment( 0, 5 ) );
    chartLayout.setRight(new LeanAttachment( 100, -5 ) );
    chartLayout.setTop(new LeanAttachment( 0, 5 ) );
    chartLayout.setBottom(new LeanAttachment( 100, -5 ) );
    lineChartComponent.setLayout( chartLayout );
    lineChartComponent.setSize( null );

    // Add the chart to the first page.
    //
    presentation.getPages().get(0).getComponents().add( lineChartComponent );

    return presentation;
  }

  public static LeanPresentation createStackedBarChartPresentation( int nr ) throws Exception {
    LeanPresentation presentation = createBarChartPresentation( nr );

    LeanBarChartComponent chart = (LeanBarChartComponent) presentation.getPages().get(0).findComponent( LINE_CHART_NAME ).getComponent();

    chart.setVerticalDimensions( Arrays.asList(
      new LeanDimension( "country", "Country", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE)
    ) );
    chart.setTitle( "Random value by Country and Color" );

    // Change the message label in the header
    //
    LeanLabelComponent label = (LeanLabelComponent) presentation.getHeader().findComponent( HEADER_MESSAGE_LABEL ).getComponent();
    label.setLabel( "Stacked bar chart filling the whole page" );
    return presentation;
  }


  public static LeanBarChartComponent createColorRandomBarChart() {
    LeanBarChartComponent chart = new LeanBarChartComponent( CONNECTOR_SAMPLE_ROWS );
    chart.setHorizontalDimensions( Arrays.asList(
      new LeanDimension( "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE)
    ) );
    LeanFact sumFact = new LeanFact( "random", "Sum", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE, AggregationMethod.SUM, "0.000");
    sumFact.setHorizontalAggregation( true );
    sumFact.setHorizontalAggregationHeader( "Total Sum" );
    sumFact.setHeaderHorizontalAlignment( LeanHorizontalAlignment.CENTER );
    sumFact.setHeaderVerticalAlignment( LeanVerticalAlignment.MIDDLE );
    sumFact.setFormatMask( "0.00" );
    chart.setFacts( Arrays.asList( sumFact ));
    chart.setHorizontalMargin( 10 );
    chart.setVerticalMargin( 10 );
    chart.setBorder( true );
    chart.setBackground( false );
    chart.setTitle( "Random by Color" );
    chart.setShowingLegend( true );
    chart.setWidthPercentage( "60" ); // % of the width allocated for the horizontal value
    chart.setShowingFactValues( true );

    return chart;
  }


}
