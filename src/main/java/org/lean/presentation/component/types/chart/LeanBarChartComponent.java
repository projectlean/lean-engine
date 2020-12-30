package org.lean.presentation.component.types.chart;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
import org.lean.core.LeanTextGeometry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize( as = LeanBarChartComponent.class )
@LeanComponentPlugin(
  id= "LeanBarChartComponent",
  name="Bar Chart",
  description = "A bar chart component"
)
public class LeanBarChartComponent extends LeanBaseChartComponent implements ILeanComponent {

  /**
   * % of the width allocated for the horizontal value
   */
  @HopMetadataProperty
  protected String widthPercentage;

  @HopMetadataProperty
  protected boolean showingFactValues;

  public LeanBarChartComponent() {
    this( (String) null );
  }

  public LeanBarChartComponent( String connectorName ) {
    super( "LeanBarChartComponent", connectorName );
  }

  public LeanBarChartComponent( LeanBarChartComponent c ) {
    super( "LeanBarChartComponent", c );
  }

  public LeanBarChartComponent clone() {
    return new LeanBarChartComponent( this );
  }

  @Override public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                           LeanLayoutResults results ) throws LeanException {

    // We need totals in the bar chart, always on a zero baseline
    //
    usingZeroBaseline = true;
    usingTotalHeights = true;

    super.processSourceData( presentation, page, component, dataContext, renderContext, results );
  }


  @Override public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext, LeanPosition offSet ) throws LeanException {
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    LeanComponent component = layoutResult.getComponent();
    SVGGraphics2D gc = layoutResult.getRenderPage().getGc();

    // Get the theme...
    //
    LeanTheme theme = renderContext.lookupTheme( themeName );

    int x = componentGeometry.getX();
    int y = componentGeometry.getY();
    int width = componentGeometry.getWidth();
    int height = componentGeometry.getHeight();
    int tickSize = 4;

    // Now get the horizontal dimension combinations
    //
    // Get all dimension combinations horizontally
    // Then sort this list of lists...
    //
    ChartDetails details = calculateDetails( gc, x, y, width, height );

    // Render the background and border to get started
    //
    drawBackGround( gc, componentGeometry, renderContext );
    drawBorder( gc, componentGeometry, renderContext );

    // Draw the title
    //
    if ( StringUtils.isNotEmpty( titleText ) ) {
      int titleX = x + ( width - details.titleGeometry.getWidth() ) / 2;
      int titleY = y + verticalMargin + details.titleGeometry.getHeight();
      enableColor( gc, lookupTitleColor( renderContext ) );
      enableFont( gc, lookupTitleFont( renderContext ) );
      gc.drawString( titleText, titleX, titleY );
    }

    // Draw the X and Y axis
    //
    enableColor( gc, lookupAxisColor( renderContext ) );

    // top left X and Y
    //
    double topLeftX = x + horizontalMargin + ( showingVerticalLabels ? details.maxFactWidth + horizontalMargin : 0 );
    double topLeftY = y + verticalMargin + details.titleHeight;

    // bottom left X and Y
    //
    double bottomLeftX = topLeftX;
    double bottomLeftY = y + height
      - verticalMargin
      - ( showingHorizontalLabels ? details.maxLabelHeight + verticalMargin : 0 )
      - details.legendHeight;

    // bottom right X and Y
    //
    double bottomRightX = x + width - horizontalMargin;
    double bottomRightY = bottomLeftY;

    // X axis
    //
    gc.drawLine( (int) bottomLeftX, (int) bottomLeftY, (int) bottomRightX, (int) bottomRightY );

    // Y axis
    //
    gc.drawLine( (int) topLeftX, (int) topLeftY, (int) bottomLeftX, (int) bottomLeftY );

    // Draw the min value
    //
    double minX = topLeftX;
    double minY = bottomLeftY;

    LeanTextGeometry minGeo = details.minLabelGeometry;
    if ( showingAxisTicks ) {
      gc.drawLine( (int) ( minX - tickSize / 2 ), (int) ( minY ), (int) ( minX + tickSize / 2 ), (int) minY );
    }
    if ( showingVerticalLabels ) {
      enableFont( gc, lookupVerticalDimensionsFont( renderContext ) );
      gc.drawString( details.minLabel, (int) ( x + horizontalMargin ), (int) ( minY + minGeo.getHeight() / 2 ) );
    }

    // Draw the max value
    //
    double maxX = topLeftX;
    double maxY = topLeftY + details.overshoot;
    LeanTextGeometry maxGeo = details.maxLabelGeometry;
    if ( showingAxisTicks ) {
      gc.drawLine( (int) ( maxX - tickSize / 2 ), (int) ( maxY ), (int) ( maxX + tickSize / 2 ), (int) maxY );
    }
    if ( showingHorizontalLabels ) {
      enableFont( gc, lookupVerticalDimensionsFont( renderContext ) );
      gc.drawString( details.maxLabel, (int) ( x + horizontalMargin ), (int) ( maxY + maxGeo.getHeight() / 2 ) );
    }

    List<List<Double>> seriesXCoordinates = new ArrayList<>();
    List<List<Double>> seriesFactValues = new ArrayList<>();
    List<String> seriesLabels = new ArrayList<>();

    // Loop over the vertical dimension value combinations
    // This is the chart series...
    //
    List<List<String>> verticalCombinations = new ArrayList<>();
    verticalCombinations.addAll( details.verticalCombinations );
    if ( verticalCombinations.isEmpty() ) {
      // At least perform once without vertical dimensions...
      //
      verticalCombinations.add( new ArrayList<>() );
    }

    int nrParts = details.labels.size();
    int nrSeries = verticalCombinations.size();

    for ( int series = 0; series < verticalCombinations.size(); series++ ) {
      List<String> verticalCombination = verticalCombinations.get( series );

      List<Double> sXCoordinates = new ArrayList<>();
      List<Double> sFactValues = new ArrayList<>();

      // Draw the parts: one for every bottom horizontal label
      //
      double lastX = -1;
      double lastY = -1;
      for ( int part = 0; part < nrParts; part++ ) {
        // Only draw this label once
        if ( series == 0 ) {
          String label = details.labels.get( part );
          LeanTextGeometry geometry = details.labelGeometries.get( part );

          // The texts will be centered in the part.
          //
          double labelX = bottomLeftX + part * details.partWidth + ( details.partWidth - geometry.getWidth() ) / 2 + geometry.getOffsetX();
          double labelY = bottomLeftY + verticalMargin + geometry.getOffsetY();

          if ( showingHorizontalLabels ) {
            enableColor( gc, lookupDefaultColor( renderContext ) );
            enableFont( gc, lookupHorizontalDimensionsFont( renderContext ) );
            gc.drawString( label, (int) labelX, (int) labelY );
          }

          // Draw a small tick at the end of the part
          //
          if ( showingAxisTicks ) {
            double tickX = bottomLeftX + part * details.partWidth + details.partWidth;
            double tickY = bottomLeftY - tickSize / 2;
            enableColor( gc, lookupAxisColor( renderContext ) );
            gc.drawLine( (int) tickX, (int) tickY, (int) tickX, (int) ( tickY + tickSize ) );
          }
        }

        // Draw the fact series...
        //
        List<String> factLabels = details.factLabels.get( series );
        List<Object> factValues = details.factValues.get( series );
        List<IValueMeta> factValueMetas = details.factValueMetas.get( series );

        Object valueData = factValues.get( part );
        IValueMeta valueMeta = factValueMetas.get( part );
        double factValue = 0;
        try {
          factValue = valueMeta.getNumber( valueData );
        } catch ( HopValueException e ) {
          throw new LeanException( "Fact data conversion error", e );
        }
        double factX = bottomLeftX + part * details.partWidth + details.partWidth / 2;

        sXCoordinates.add( factX );
        sFactValues.add( factValue );
      }

      seriesXCoordinates.add( sXCoordinates );
      seriesFactValues.add( sFactValues );
      seriesLabels.add( getCombinationLabel( verticalCombination ) );
    }

    double barWidth = details.partWidth * Const.toDouble( widthPercentage, 50 ) / 100;

    // Now draw the stacked charts...
    // We do this by this time going over the parts..
    //

    for ( int part = 0; part < nrParts; part++ ) {

      double lowY = bottomLeftY;

      for ( int series = 0; series < nrSeries; series++ ) {

        double factValue = seriesFactValues.get( series ).get( part );
        double factX = seriesXCoordinates.get( series ).get( part );

        double valueHeigth = factValue * details.valueFactor;
        double topY = lowY - valueHeigth;
        double leftX = factX - barWidth / 2;

        // See if we need to use a specific theme scheme for the area...
        //
        if ( theme == null ) {
          enableColor( gc, lookupDefaultColor( renderContext ) );
        } else {
          // Color is depending on the series we're drawing...
          //
          String labelValue = seriesLabels.get( series );
          LeanColorRGB color = renderContext.getStableColor( theme.getName(), labelValue );
          enableColor( gc, color );
        }

        gc.drawRect( (int) Math.round( leftX ), (int) Math.round( topY ), (int) Math.round( barWidth ), (int) Math.round( valueHeigth ) );
        gc.fillRect( (int) Math.round( leftX ), (int) Math.round( topY ), (int) Math.round( barWidth ), (int) Math.round( valueHeigth ) );

        // shift the low level
        //
        lowY = topY;

      }
    }

    // OK, now we need to render the labels at the bottom
    //
    double legendX = x + horizontalMargin;
    double legendY = bottomLeftY + details.maxLabelHeight + 2 * verticalMargin;
    double legendEntryWidth = details.maxLegendLabelWidth + 2 * horizontalMargin + details.legendMarkerSize;
    double legendEntryHeight = details.maxLegendLabelHeight + verticalMargin;

    // Do we have fewer columns than we can fit?  In that case, center the legend
    //
    if ( details.maxNrLegendColumns > details.nrLegendColumns ) {
      double emptySpace = ( details.maxNrLegendColumns - details.nrLegendColumns ) * legendEntryWidth;
      legendX += emptySpace / 2;
    }

    String themeName = null;
    if ( theme != null ) {
      themeName = theme.getName();
    }

    int colNr = 0;
    int rowNr = 0;
    for ( String seriesLabel : seriesLabels ) {

      double labelX = legendX + colNr * ( legendEntryWidth );
      double labelY = legendY + rowNr * ( details.maxLegendLabelHeight + verticalMargin );

      LeanColorRGB color = renderContext.getStableColor( themeName, seriesLabel );
      if ( color == null ) {
        color = lookupDefaultColor( renderContext );
      }
      if ( color == null ) {
        color = LeanColorRGB.BLACK;
      }

      // This is the legend color...
      //
      enableColor( gc, color );

      // Let's fill a small circle
      //
      gc.fillOval( (int) labelX, (int) labelY + ( details.maxLegendLabelHeight - details.legendMarkerSize ) / 2, (int) details.legendMarkerSize, (int) details.legendMarkerSize );

      // Print the label in the default color
      //
      enableColor( gc, lookupDefaultColor( renderContext ) );

      gc.drawString( seriesLabel, (int) ( labelX + details.legendMarkerSize + horizontalMargin / 2 ), (int) ( labelY + details.maxLegendLabelHeight ) );

      // Switch to the next position
      //
      colNr++;
      if ( colNr >= details.nrLegendColumns ) {
        colNr = 0;
        rowNr++;
      }
    }

  }

  /**
   * Gets widthPercentage
   *
   * @return value of widthPercentage
   */
  public String getWidthPercentage() {
    return widthPercentage;
  }

  /**
   * @param widthPercentage The widthPercentage to set
   */
  public void setWidthPercentage( String widthPercentage ) {
    this.widthPercentage = widthPercentage;
  }

  /**
   * Gets showingFactValues
   *
   * @return value of showingFactValues
   */
  public boolean isShowingFactValues() {
    return showingFactValues;
  }

  /**
   * @param showingFactValues The showingFactValues to set
   */
  public void setShowingFactValues( boolean showingFactValues ) {
    this.showingFactValues = showingFactValues;
  }
}
