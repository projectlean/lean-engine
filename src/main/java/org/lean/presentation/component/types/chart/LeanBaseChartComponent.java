package org.lean.presentation.component.types.chart;

import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanSize;
import org.lean.core.LeanTextGeometry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.types.crosstab.LeanBaseAggregatingComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.ValueMetaInterface;
import org.apache.hop.core.row.value.ValueMetaNumber;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

import java.util.ArrayList;
import java.util.List;

public abstract class LeanBaseChartComponent extends LeanBaseAggregatingComponent implements ILeanComponent {

  @MetaStoreAttribute
  protected int horizontalMargin;

  @MetaStoreAttribute
  protected int verticalMargin;

  @MetaStoreAttribute
  protected boolean showingHorizontalLabels;

  @MetaStoreAttribute
  protected boolean showingVerticalLabels;

  @MetaStoreAttribute
  protected boolean showingAxisTicks;

  @MetaStoreAttribute
  protected int dotSize;

  @MetaStoreAttribute
  protected String title;

  @MetaStoreAttribute
  protected String lineWidth;

  @MetaStoreAttribute
  protected boolean usingZeroBaseline;

  @MetaStoreAttribute
  protected boolean showingLegend;

  // Calculated at runtime
  //
  @JsonIgnore
  protected transient String titleText;

  @JsonIgnore
  protected transient boolean usingTotalHeights;

  public LeanBaseChartComponent( String pluginId, String connectorName ) {
    super( pluginId );
    this.sourceConnectorName = connectorName;
    horizontalDimensions = new ArrayList<>();
    verticalDimensions = new ArrayList<>();
    facts = new ArrayList<>();
    showingHorizontalLabels = true;
    showingVerticalLabels = true;
    showingAxisTicks = true;
    dotSize = 6;
  }

  public LeanBaseChartComponent( String pluginId, LeanBaseChartComponent c ) {
    super( pluginId, c );
    this.horizontalMargin = c.horizontalMargin;
    this.verticalMargin = c.verticalMargin;
    this.showingHorizontalLabels = c.showingHorizontalLabels;
    this.showingVerticalLabels = c.showingVerticalLabels;
    this.showingAxisTicks = c.showingAxisTicks;
    this.dotSize = c.dotSize;
    this.title = c.title;
    this.themeName = c.themeName;
    this.lineWidth = c.lineWidth;
  }

  public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                 LeanLayoutResults results ) throws LeanException {
    // Read the data
    //
    LeanConnector connector = dataContext.getConnector( sourceConnectorName );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector '" + sourceConnectorName + "'" );
    }

    validateSettings();

    // Get the rows
    //
    connector.getConnector().addRowListener( ( rowMeta, rowData ) -> {
      if ( rowData != null ) {
        // Pivot the row data...
        //
        pivotRow( rowMeta, rowData );
      }
    } );

    connector.getConnector().startStreaming( dataContext );
    connector.getConnector().waitUntilFinished();

    // Calculate the title based on data context
    //
    titleText = dataContext.getVariableSpace().environmentSubstitute( title );
  }

  protected void validateSettings() throws LeanException {

    // Validate some metadata...
    //
    for ( LeanFact fact : facts ) {
      if ( StringUtils.isEmpty( fact.getColumnName() ) ) {
        throw new LeanException( "No column name given for a fact" );
      }
      if ( fact.getAggregationMethod() == null ) {
        throw new LeanException( "No aggregation method specified for fact column '" + fact.getColumnName() + "'" );
      }
    }
    for ( LeanDimension dimension : horizontalDimensions ) {
      if ( StringUtils.isEmpty( dimension.getColumnName() ) ) {
        throw new LeanException( "No column name given for a horizontal dimension" );
      }
    }
    for ( LeanDimension dimension : verticalDimensions ) {
      if ( StringUtils.isEmpty( dimension.getColumnName() ) ) {
        throw new LeanException( "No column name given for a vertical dimension" );
      }
    }
  }

  /**
   * 1. First
   * <p>
   * Calculate the imageSize of the table, pretty much calculating the sizes of each element in the data grid
   * We store all the information in the Results data set
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param results
   * @return
   */
  public LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results ) {
    if ( component.isDynamic() ) {
      // Just a default imageSize, get's changed if a imageSize is set
      // or if attachments are used for position and imageSize
      //
      return new LeanSize( LeanSize.UNKNOWN_SIZE );
    } else {
      return component.getSize();
    }
  }

  protected String getCombinationString( List<String> combinationList ) {
    StringBuilder combo = new StringBuilder();
    for ( String combination : combinationList ) {
      if ( combo.length() > 0 ) {
        combo.append( "-" );
      }
      combo.append( combination );
    }
    return combo.toString();
  }

  protected ChartDetails calculateDetails( SVGGraphics2D gc, int x, int y, int width, int height ) throws LeanException {
    ChartDetails details = new ChartDetails( x, y, width, height );
    calculateDistinctValues( details.horizontalValues, details.verticalValues );

    getCombinations( details.horizontalValues, 0, details.horizontalCombinations, new ArrayList<>() );
    List<List<String>> sortedHorizontalCombinations = sortCombinations( details.horizontalCombinations );

    getCombinations( details.verticalValues, 0, details.verticalCombinations, new ArrayList<>() );
    List<List<String>> sortedVerticalCombinations = sortCombinations( details.verticalCombinations );


    // Calculate the title geometry
    //
    if ( StringUtils.isNotEmpty( titleText ) ) {
      details.titleGeometry = calculateTextGeometry( gc, titleText );
      details.titleHeight = details.titleGeometry.getHeight() + verticalMargin;
    } else {
      details.titleHeight = 0;
    }

    // How many series do we have in the chart?
    //
    List<List<String>> verticalCombinations = new ArrayList<>( details.verticalCombinations );
    if ( verticalCombinations.isEmpty() ) {
      // At least perform once without vertical dimensions...
      //
      verticalCombinations.add( new ArrayList<>() );
    }

    if ( facts.isEmpty() ) {
      throw new LeanException( "We need at least 1 fact to work with" );
    }
    if ( facts.size() > 1 ) {
      throw new LeanException( "Only 1 fact is supported at this time" );
    }

    // Some information about the fact
    //
    int factIndex = 0;
    LeanFact fact = facts.get( factIndex );
    ValueMetaNumber factValueMeta = new ValueMetaNumber( fact.getColumnName() );
    factValueMeta.setConversionMask( fact.getFormatMask() );

    // Calculate labels and get their sizes, fact values...
    // Also calculate the maximal height to see how much room we need at the bottom.
    //
    details.minValue = Double.MAX_VALUE;
    if ( usingZeroBaseline ) {
      details.minValue = 0.0d;
      try {
        details.minLabel = factValueMeta.getString( details.minValue );
      } catch ( HopValueException e ) {
        throw new LeanException( "Unexpected error converting number to string", e );
      }
      details.minLabelGeometry = calculateTextGeometry( gc, details.minLabel );
    }
    details.maxValue = Double.MIN_VALUE;
    details.maxFactWidth = 0;

    // How many combinations do we have?
    //
    int nrCombinations = sortedHorizontalCombinations.size();

    for ( int series = 0; series < verticalCombinations.size(); series++ ) {
      details.factLabels.add( new ArrayList<>() );
      details.factValues.add( new ArrayList<>() );
      details.factValueMetas.add( new ArrayList<>() );
    }

    for ( int part = 0; part < nrCombinations; part++ ) {
      List<String> combinationList = sortedHorizontalCombinations.get( part );
      String axisLabel = getCombinationString( combinationList );
      details.labels.add( axisLabel );

      // What's the geometry of the label?
      //
      LeanTextGeometry labelGeometry = calculateTextGeometry( gc, axisLabel );
      details.labelGeometries.add( labelGeometry );

      // Do we have any facts to look up: only 1 fact supported for now.
      //
      ValueMetaInterface valueMeta = inputRowMeta.getValueMeta( factIndexes.get( factIndex ) );

      double totalValue = 0;

      for ( int series = 0; series < verticalCombinations.size(); series++ ) {

        List<String> verticalCombination = verticalCombinations.get( series );

        List<String> factLabels = details.factLabels.get( series );
        List<Object> factValues = details.factValues.get( series );
        List<ValueMetaInterface> factValueMetas = details.factValueMetas.get( series );

        // The lookup key for the combination of horizontal and vertical dimensions...
        //
        List<String> factLookupKey = new ArrayList<>( verticalCombination );
        factLookupKey.addAll( combinationList );

        // Lookup the values...
        //
        Object valueData = pivotMapList.get( factIndex ).get( factLookupKey );

        factValues.add( valueData );
        factValueMetas.add( valueMeta );

        try {
          if ( fact.getFormatMask() != null ) {
            valueMeta.setConversionMask( fact.getFormatMask() );
          }
          String factString = Const.NVL( valueMeta.getString( valueData ), "-" );
          factLabels.add( factString );
          LeanTextGeometry factGeometry = calculateTextGeometry( gc, factString );
          if ( factGeometry.getWidth() > details.maxFactWidth ) {
            details.maxFactWidth = factGeometry.getWidth();
          }
        } catch ( HopValueException e ) {
          throw new LeanException( "Error formatting value '" + valueData + "' : ", e );
        }

        try {
          Double factValueDouble = valueMeta.getNumber( valueData );
          double factValue;
          if ( factValueDouble == null ) {
            factValue = 0.0d;
          } else {
            factValue = factValueDouble.doubleValue();
          }
          totalValue += factValue;
          if ( factValue < details.minValue ) {
            details.minValue = factValue;
            details.minLabel = factLabels.get( factLabels.size() - 1 );
            details.minLabelGeometry = calculateTextGeometry( gc, details.minLabel );
          }
          if ( factValue > details.maxValue ) {
            details.maxValue = factValue;
            details.maxLabel = factLabels.get( factLabels.size() - 1 );
            details.maxLabelGeometry = calculateTextGeometry( gc, details.maxLabel );
          }
          if ( usingTotalHeights && totalValue > details.maxValue ) {
            details.maxValue = totalValue;
            details.maxLabel = factValueMeta.getString( totalValue );
            details.maxLabelGeometry = calculateTextGeometry( gc, details.maxLabel );
          }
        } catch ( HopException e ) {
          throw new LeanException( "Data conversion error", e );
        }
      }

      if ( labelGeometry.getHeight() > details.maxLabelHeight ) {
        details.maxLabelHeight = labelGeometry.getHeight();
      }
    }


    if ( nrCombinations == 0 ) {
      details.partWidth = details.width - horizontalMargin * 2 - details.maxFactWidth;
    } else {
      // Split the graph in equal parts
      //
      details.partWidth = ( details.width - horizontalMargin * 3 - details.maxFactWidth ) / (double) nrCombinations;
    }

    // Do some calculations for the legend.
    // Let's assume it's placed at the bottom
    //
    // So we need to calculate the max widt of a vertical combination string.
    // Then we need to figure out how many of those we can fit onto the width
    // Then we know how many columns and rows we can make
    //
    List<String> legendLabels = new ArrayList<>();
    List<LeanTextGeometry> legendLabelGeos = new ArrayList<>();
    int maxLegendLabelWidth = 0;
    int maxLegendLabelHeight = 0;
    int nrLabels = 0;
    if (showingLegend) {
      nrLabels = sortedHorizontalCombinations.size();
      for ( List<String> verticalCombination : sortedVerticalCombinations ) {
        String legendLabel = getCombinationLabel( verticalCombination );
        legendLabels.add( legendLabel );
        LeanTextGeometry labelGeo = calculateTextGeometry( gc, legendLabel );
        legendLabelGeos.add( labelGeo );

        if ( labelGeo.getWidth() > maxLegendLabelWidth ) {
          maxLegendLabelWidth = labelGeo.getWidth();
        }
        if ( labelGeo.getHeight() > maxLegendLabelHeight ) {
          maxLegendLabelHeight = labelGeo.getHeight();
        }
      }
    }

    // How can we fit all legend labels?
    // Calculate how many columns and rows we need
    //
    details.legendLabels = legendLabels;
    details.legendLabelGeos = legendLabelGeos;
    details.maxLegendLabelWidth = maxLegendLabelWidth;
    details.maxLegendLabelHeight = maxLegendLabelHeight;
    details.legendMarkerSize = maxLegendLabelHeight*2/3;

    details.legendWidth = width - 2*horizontalMargin;
    details.maxNrLegendColumns = (int)Math.floor( (double)details.legendWidth / (maxLegendLabelWidth+2*horizontalMargin+details.legendMarkerSize) );
    details.nrLegendColumns = Math.min(details.legendLabels.size(), details.maxNrLegendColumns );
    if (details.nrLegendColumns>0) {
      details.nrLegendRows = 1 + (int) Math.floor( (double)nrLabels / details.nrLegendColumns );
    } else {
      details.nrLegendRows = 0;
    }
    details.legendHeight = ( details.maxLegendLabelHeight + verticalMargin ) * details.nrLegendRows;

    // OK, now we continue...
    //
    details.overshoot = (double)height / 20;
    details.partHeight = ( height - verticalMargin * 3 - details.maxLabelHeight - details.overshoot * 2 - details.titleHeight - details.legendHeight );
    if ( usingZeroBaseline ) {
      details.partHeight += details.overshoot;
    }
    details.valueRange = details.maxValue - details.minValue;
    if ( details.valueRange == 0.0 ) {
      details.valueFactor = 0.0;
    } else {
      details.valueFactor = details.partHeight / details.valueRange;
    }

    return details;
  }

  protected String getCombinationLabel( List<String> combination ) {
    StringBuilder label = new StringBuilder();
    for ( String string : combination ) {
      if ( label.length() > 0 ) {
        label.append( '-' );
      }
      label.append( string );
    }
    return label.toString();
  }






  /**
   * Gets horizontalMargin
   *
   * @return value of horizontalMargin
   */
  public int getHorizontalMargin() {
    return horizontalMargin;
  }

  /**
   * @param horizontalMargin The horizontalMargin to set
   */
  public void setHorizontalMargin( int horizontalMargin ) {
    this.horizontalMargin = horizontalMargin;
  }

  /**
   * Gets verticalMargin
   *
   * @return value of verticalMargin
   */
  public int getVerticalMargin() {
    return verticalMargin;
  }

  /**
   * @param verticalMargin The verticalMargin to set
   */
  public void setVerticalMargin( int verticalMargin ) {
    this.verticalMargin = verticalMargin;
  }

  /**
   * Gets showingHorizontalLabels
   *
   * @return value of showingHorizontalLabels
   */
  public boolean isShowingHorizontalLabels() {
    return showingHorizontalLabels;
  }

  /**
   * @param showingHorizontalLabels The showingHorizontalLabels to set
   */
  public void setShowingHorizontalLabels( boolean showingHorizontalLabels ) {
    this.showingHorizontalLabels = showingHorizontalLabels;
  }

  /**
   * Gets showingVerticalLabels
   *
   * @return value of showingVerticalLabels
   */
  public boolean isShowingVerticalLabels() {
    return showingVerticalLabels;
  }

  /**
   * @param showingVerticalLabels The showingVerticalLabels to set
   */
  public void setShowingVerticalLabels( boolean showingVerticalLabels ) {
    this.showingVerticalLabels = showingVerticalLabels;
  }

  /**
   * Gets showingAxisTicks
   *
   * @return value of showingAxisTicks
   */
  public boolean isShowingAxisTicks() {
    return showingAxisTicks;
  }

  /**
   * @param showingAxisTicks The showingAxisTicks to set
   */
  public void setShowingAxisTicks( boolean showingAxisTicks ) {
    this.showingAxisTicks = showingAxisTicks;
  }

  /**
   * Gets dotSize
   *
   * @return value of dotSize
   */
  public int getDotSize() {
    return dotSize;
  }

  /**
   * @param dotSize The dotSize to set
   */
  public void setDotSize( int dotSize ) {
    this.dotSize = dotSize;
  }

  /**
   * Gets title
   *
   * @return value of title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title The title to set
   */
  public void setTitle( String title ) {
    this.title = title;
  }

  /**
   * Gets lineWidth
   *
   * @return value of lineWidth
   */
  public String getLineWidth() {
    return lineWidth;
  }

  /**
   * @param lineWidth The lineWidth to set
   */
  public void setLineWidth( String lineWidth ) {
    this.lineWidth = lineWidth;
  }

  /**
   * Gets usingZeroBaseline
   *
   * @return value of usingZeroBaseline
   */
  public boolean isUsingZeroBaseline() {
    return usingZeroBaseline;
  }

  /**
   * @param usingZeroBaseline The usingZeroBaseline to set
   */
  public void setUsingZeroBaseline( boolean usingZeroBaseline ) {
    this.usingZeroBaseline = usingZeroBaseline;
  }

  /**
   * Gets showingLegend
   *
   * @return value of showingLegend
   */
  public boolean isShowingLegend() {
    return showingLegend;
  }

  /**
   * @param showingLegend The showingLegend to set
   */
  public void setShowingLegend( boolean showingLegend ) {
    this.showingLegend = showingLegend;
  }
}
