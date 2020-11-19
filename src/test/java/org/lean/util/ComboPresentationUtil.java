package org.lean.util;

import org.apache.hop.core.svg.SvgImage;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.AggregationMethod;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.chart.LeanLineChartComponent;
import org.lean.presentation.component.types.crosstab.LeanCrosstabComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.component.types.svg.LeanSvgComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

import java.util.Arrays;

public class ComboPresentationUtil extends BasePresentationUtil {


  public ComboPresentationUtil( IHopMetadataProvider metadataProvider ) {
    super( metadataProvider );
  }

  public LeanPresentation createComboPresentation( int nr ) throws Exception {
    LeanPresentation presentation = createBasePresentation(
      "Combo (" + nr + ")",
      "Combo " + nr + " description",
      1000,
      "Layout test with charts basing position off crosstab"
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    // Add a crosstab at the top left of the page.
    // This component is dynamically sized
    //
    {
      LeanCrosstabComponent crosstab = new LeanCrosstabComponent( CONNECTOR_SAMPLE_ROWS );
      crosstab.setHorizontalDimensions( Arrays.asList(
        new LeanDimension( "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE ),
        new LeanDimension( "important", "?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE )
      ) );
      crosstab.setVerticalDimensions( Arrays.asList(
        new LeanDimension( "name", "Customer", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE )
      ) );
      LeanFact sumFact = new LeanFact( "random", "Sum", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE, AggregationMethod.SUM, "0.000" );
      sumFact.setHorizontalAggregation( true );
      sumFact.setHorizontalAggregationHeader( "Total Sum" );
      sumFact.setHeaderHorizontalAlignment( LeanHorizontalAlignment.CENTER );
      sumFact.setHeaderVerticalAlignment( LeanVerticalAlignment.MIDDLE );
      LeanFact countFact = new LeanFact( "name", "Count", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE, AggregationMethod.COUNT, "0" );
      countFact.setHorizontalAggregation( true );
      countFact.setHorizontalAggregationHeader( "Total Count" );
      countFact.setHeaderHorizontalAlignment( LeanHorizontalAlignment.CENTER );
      countFact.setHeaderVerticalAlignment( LeanVerticalAlignment.MIDDLE );
      crosstab.setFacts( Arrays.asList( sumFact, countFact ) );
      crosstab.setBackground( false );
      crosstab.setBorder( false );
      crosstab.setHorizontalMargin( 3 );
      crosstab.setVerticalMargin( 2 );
      crosstab.setEvenHeights( true );
      crosstab.setHeaderOnEveryPage( true );
      crosstab.setShowingVerticalTotals( true );
      crosstab.setShowingHorizontalTotals( true );
      LeanComponent crosstabComponent = new LeanComponent( "Crosstab", crosstab );
      crosstabComponent.setLayout( new LeanLayout( 0, 0 ) );
      crosstabComponent.setSize( null );

      // Add the table to the first page.
      //
      pageOne.getComponents().add( crosstabComponent );
    }

    // Add a chart below the crosstab
    //
    {
      LeanLineChartComponent lineChart = LineChartPresentationUtil.createColorRandomLineChart();

      lineChart.setVerticalDimensions( Arrays.asList(
        new LeanDimension( "country", "Country", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE )
      ) );
      lineChart.setDrawingCurvedTrendLine( true );

      LeanComponent lineChartComponent = new LeanComponent( "LineChart", lineChart );
      // Setting imageSize forces chart on next page, comment out Right/Bottom attachments
      // lineChartComponent.setSize( new LeanSize( 600, 600 ) );
      LeanLayout chartLayout = new LeanLayout();
      // Fill the page
      chartLayout.setLeft( new LeanAttachment( 0, 0 ) );
      chartLayout.setRight( new LeanAttachment( "Crosstab", 100, 0, LeanAttachment.Alignment.RIGHT ) );
      chartLayout.setTop( new LeanAttachment( "Crosstab", 0, 20, LeanAttachment.Alignment.BOTTOM ) );
      chartLayout.setBottom( new LeanAttachment( 100, -10 ) );
      lineChartComponent.setLayout( chartLayout );

      // Add the table to the first page.
      //
      pageOne.getComponents().add( lineChartComponent );
    }

    // a detailed trend chart top-right
    {
      LeanLineChartComponent trendChart = LineChartPresentationUtil.createNoLabelsTrendChartDetailed();
      trendChart.setDrawingCurvedTrendLine( true );
      LeanComponent trendChartComponent = new LeanComponent( "TrendChartDetailed", trendChart );
      // Setting imageSize forces chart on next page, comment out Right/Bottom attachments
      // lineChartComponent.setSize( new LeanSize( 600, 600 ) );
      LeanLayout trendChartLayout = new LeanLayout();
      // Fill the page
      trendChartLayout.setLeft( new LeanAttachment( "Crosstab", 0, 10, LeanAttachment.Alignment.RIGHT ) );
      trendChartLayout.setTop( new LeanAttachment( "Crosstab", 0, 0, LeanAttachment.Alignment.TOP ) );
      trendChartLayout.setRight( new LeanAttachment( 100, 0 ) ); // Right of the page
      trendChartLayout.setBottom( new LeanAttachment( "Crosstab", 0, 0, LeanAttachment.Alignment.BOTTOM ) );
      trendChartComponent.setLayout( trendChartLayout );

      pageOne.getComponents().add( trendChartComponent );
    }


    // Add a rotated LEAN! label in the background
    //
    {
      LeanSvgComponent rotatedLabel = new LeanSvgComponent("lean-logo.svg");
      rotatedLabel.setScalePercent( "125" );
      rotatedLabel.setBorder( false );
      LeanComponent imageComponent = new LeanComponent( "Logo", rotatedLabel );
      LeanLayout imageLayout = new LeanLayout();
      imageLayout.setRight( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.RIGHT ) ); // Right of the header/page
      imageLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) ); // Top of the header/page
      imageComponent.setLayout( imageLayout );

      LeanComponent labelComponent = new LeanComponent( "RotatedLabel", rotatedLabel );
      LeanLayout labelLayout = new LeanLayout();
      labelLayout.setLeft( new LeanAttachment( "Crosstab", 0, 50, LeanAttachment.Alignment.RIGHT ) );
      labelLayout.setTop( new LeanAttachment( "Crosstab", 0, 35, LeanAttachment.Alignment.BOTTOM ) );
      labelLayout.setBottom( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.BOTTOM ) );
      labelLayout.setRight( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.RIGHT ) );
      labelComponent.setLayout( labelLayout );
      labelComponent.setRotation( "-45" );
      pageOne.getComponents().add( labelComponent );
    }


    return presentation;
  }


}
