package org.lean.presentation.component.types.chart;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanTextGeometry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize( as = LeanLineChartComponent.class )
@LeanComponentPlugin(
  id= "LeanLineChartComponent",
  name="Line chart",
  description = "A line chart component"
)
public class LeanLineChartComponent extends LeanBaseChartComponent implements ILeanComponent {

  @HopMetadataProperty
  protected boolean drawingCurvedTrendLine;

  public LeanLineChartComponent() {
    this( (String) null );
  }

  public LeanLineChartComponent( String connectorName ) {
    super( "LeanLineChartComponent", connectorName );
  }

  public LeanLineChartComponent( LeanLineChartComponent c ) {
    super( "LeanLineChartComponent", c );
    drawingCurvedTrendLine = c.drawingCurvedTrendLine;
  }

  public LeanLineChartComponent clone() {
    return new LeanLineChartComponent( this );
  }

  @Override public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    LeanComponent component = layoutResult.getComponent();
    SVGGraphics2D gc = layoutResult.getRenderPage().getGc();

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
    double bottomLeftY = y + height - verticalMargin - ( showingHorizontalLabels ? details.maxLabelHeight + verticalMargin : 0 );

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
    if ( !usingZeroBaseline ) {
      minY -= details.overshoot;
    }
    LeanTextGeometry minGeo = details.minLabelGeometry;
    if ( showingAxisTicks ) {
      gc.drawLine( (int) ( minX - tickSize / 2 ), (int) ( minY ), (int) ( minX + tickSize / 2 ), (int) minY );
    }
    if ( showingVerticalLabels && StringUtils.isNotEmpty( details.minLabel ) ) {
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
    if ( showingVerticalLabels && StringUtils.isNotEmpty( details.maxLabel ) ) {
      enableFont( gc, lookupVerticalDimensionsFont( renderContext ) );
      gc.drawString( details.maxLabel, (int) ( x + horizontalMargin ), (int) ( maxY + maxGeo.getHeight() / 2 ) );
    }

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

    // Keep the location for the label...
    //
    Point2D.Double labelPoint = null;

    for ( int series = 0; series < verticalCombinations.size(); series++ ) {
      List<String> verticalCombination = verticalCombinations.get( series );

      List<Double> xCoordinates = new ArrayList<>();
      List<Double> yCoordinates = new ArrayList<>();

      // Draw the parts: one for every bottom horizontal label
      //
      double lastX = -1;
      double lastY = -1;
      for ( int part = 0; part < details.labels.size(); part++ ) {
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
          Double factValueDouble = valueMeta.getNumber( valueData );
          if ( factValueDouble == null ) {
            factValue = 0.0d;
          } else {
            factValue = factValueDouble.doubleValue();
          }
        } catch ( HopValueException e ) {
          throw new LeanException( "Fact data conversion error", e );
        }
        double factX = bottomLeftX + part * details.partWidth + details.partWidth / 2;
        double factY = topLeftY + details.overshoot + ( ( details.maxValue - factValue ) * details.valueFactor );

        // See if we need to set a specific theme scheme for the line...
        //
        if ( theme == null ) {
          enableColor( gc, lookupDefaultColor( renderContext ) );
        } else {
          // Color is depending on the series we're drawing...
          //
          String labelValue = getCombinationString( verticalCombination );
          LeanColorRGB color = renderContext.getStableColor( theme.getName(), labelValue );
          enableColor( gc, color );
        }

        if ( dotSize > 0 ) {
          gc.drawRect( (int) ( factX - dotSize / 2 ), (int) ( factY - dotSize / 2 ), (int) dotSize, (int) dotSize );
        }

        xCoordinates.add( factX );
        yCoordinates.add( factY );

        // Keep the location of the last dot in a series to put the label on it...
        //
        if ( !verticalCombination.isEmpty() && part == details.labels.size() - 1 ) {

          labelPoint = new Point2D.Double( factX, factY );

        }
      }


      Stroke stroke = gc.getStroke();
      float lw = (float) Const.toDouble( Const.NVL( lineWidth, "1.0" ), 1.0d );

      if ( isDrawingCurvedTrendLine() && xCoordinates.size() > 2 ) {
        // Quarter of the line width, it's a trend...
        //
        gc.setStroke( new BasicStroke( lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL ) );

        double[] xs = new double[ xCoordinates.size() ];
        double[] ys = new double[ yCoordinates.size() ];
        for ( int i = 0; i < xCoordinates.size(); i++ ) {
          xs[ i ] = xCoordinates.get( i );
          ys[ i ] = yCoordinates.get( i );
        }

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        PolynomialSplineFunction function = splineInterpolator.interpolate( xs, ys );

        // TODO: make the number of curve points configurable...
        //
        int nrValues = xCoordinates.size() * 30;
        double[] xi = new double[ nrValues ];
        xi[ 0 ] = xs[ 0 ];
        xi[ xi.length - 1 ] = xs[ xs.length - 1 ];
        double diff = xs[ xs.length - 1 ] - xi[ 0 ];
        double delta = diff / nrValues;
        for ( int i = 1; i < xi.length - 1; i++ ) {
          xi[ i ] = xs[ 0 ] + delta * i;
        }

        double lx = xs[ 0 ];
        double ly = ys[ 0 ];

        Path2D path = new Path2D.Double();

        for ( int i = 1; i < xi.length; i++ ) {
          double yi = function.value( xi[ i ] );

          int fromX = Math.round( (float) lx );
          int fromY = Math.round( (float) ly );
          int toX = Math.round( (float) xi[ i ] );
          int toY = Math.round( (float) yi );

          if ( i == 1 ) {
            path.moveTo( lx, ly );
          }
          path.lineTo( xi[ i ], yi );
          // Shape lineShape = new Line2D.Double( lx, ly, xi[i], yi );
          lx = xi[ i ];
          ly = yi;
        }
        gc.draw( path );


      } else {

        // Draw the actual line
        //
        gc.setStroke( new BasicStroke( lw, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER ) );

        int[] xs = new int[ xCoordinates.size() ];
        int[] ys = new int[ yCoordinates.size() ];
        for ( int i = 0; i < xs.length; i++ ) {
          xs[ i ] = xCoordinates.get( i ).intValue();
          ys[ i ] = yCoordinates.get( i ).intValue();
        }

        gc.drawPolyline( xs, ys, xs.length );


        gc.setStroke( stroke );
      }

      if ( labelPoint != null ) {
        // At the end, draw the series name...
        //
        double factX = labelPoint.x;
        double factY = labelPoint.y;

        String seriesLabel = getCombinationString( verticalCombination );
        gc.drawString( seriesLabel, (int) factX - dotSize - 2, (int) factY - dotSize - 2 );

      }
    }
  }


  /**
   * Gets drawingCurvedTrendLine
   *
   * @return value of drawingCurvedTrendLine
   */
  public boolean isDrawingCurvedTrendLine() {
    return drawingCurvedTrendLine;
  }

  /**
   * @param drawingCurvedTrendLine The drawingCurvedTrendLine to set
   */
  public void setDrawingCurvedTrendLine( boolean drawingCurvedTrendLine ) {
    this.drawingCurvedTrendLine = drawingCurvedTrendLine;
  }
}
