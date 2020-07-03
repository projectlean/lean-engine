package org.lean.presentation.component.types.crosstab;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.value.ValueMetaInteger;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanFact;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanSize;
import org.lean.core.LeanTextGeometry;
import org.lean.core.LeanVerticalAlignment;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonDeserialize( as = LeanCrosstabComponent.class )
public class LeanCrosstabComponent extends LeanBaseAggregatingComponent implements ILeanComponent {

  // TODO Implement sub-totals, multiple totals
  // TODO Allow sorting by all sorts of values, probably also on aggregates
  //
  public static final String DATA_CROSSTAB_DETAILS = "crosstab_details";
  public static final String DATA_START_ROW = "start_row";
  public static final String DATA_END_ROW = "end_row";

  @HopMetadataProperty
  private int horizontalMargin;

  @HopMetadataProperty
  private int verticalMargin;

  @HopMetadataProperty
  private boolean evenHeights;

  @HopMetadataProperty
  private boolean headerOnEveryPage;

  @HopMetadataProperty
  private boolean showingHorizontalSubtotals;

  @HopMetadataProperty
  private boolean showingVerticalSubtotals;

  public LeanCrosstabComponent() {
    super( "LeanCrosstabComponent" );
    horizontalDimensions = new ArrayList<>();
    verticalDimensions = new ArrayList<>();
    facts = new ArrayList<>();
  }

  public LeanCrosstabComponent( String connectorName ) {
    this();
    this.sourceConnectorName = connectorName;
  }

  public LeanCrosstabComponent( LeanCrosstabComponent c ) {
    super( "LeanCrosstabComponent", c );
    this.sourceConnectorName = c.sourceConnectorName;
    this.horizontalMargin = c.horizontalMargin;
    this.verticalMargin = c.verticalMargin;
    this.evenHeights = c.evenHeights;
    this.headerOnEveryPage = c.headerOnEveryPage;
    this.showingHorizontalSubtotals = c.showingHorizontalSubtotals;
    this.showingVerticalSubtotals = c.showingVerticalSubtotals;
  }

  public LeanCrosstabComponent clone() {
    return new LeanCrosstabComponent( this );
  }


  @Override public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                           LeanLayoutResults results ) throws LeanException {

    LeanConnector connector = dataContext.getConnector( sourceConnectorName );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector '" + sourceConnectorName + "'" );
    }

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


    // Now all the rows have been pivoted, we can render the data...
    // The vertical dimension columns are on the left.
    // The horizontal dimension values are the next columns.
    // We need every horizontal dimension value combined with every other with every fact
    //

    // Keep the calculated details around for later during layout and rendering.
    //
    CrosstabDetails details = new CrosstabDetails();

    // To calculate the width and height of the text in the given font we need a GC
    //
    SVGGraphics2D gc = HopSvgGraphics2D.newDocument();
    enableFont( gc, lookupDefaultFont( renderContext ) );

    // Things to remember for every rendered row...
    //
    if ( horizontalDimensions.size() == 0 && verticalDimensions.size() == 0 ) {
      // No dimensions
      //
      details.sortedVerticalCombinations = new ArrayList<>();
      details.sortedHorizontalCombinations = Arrays.asList( Arrays.asList( "-" ) );
    } else if ( horizontalDimensions.size() == 0 ) {
      // Any keySet is fine, all facts have the same vertical dimensions
      //
      details.sortedVerticalCombinations = new ArrayList( pivotMapList.get( 0 ).keySet() );
      sortListOfListOfStrings( details.sortedVerticalCombinations );
      details.sortedHorizontalCombinations = new ArrayList<>();
    } else if ( verticalDimensions.size() == 0 ) {
      details.sortedVerticalCombinations = new ArrayList<>();
      details.sortedHorizontalCombinations = new ArrayList<>( pivotMapList.get( 0 ).keySet() );
      sortListOfListOfStrings( details.sortedHorizontalCombinations );
    } else {
      // Generic case with both horizontal and vertical dimensions
      //
      List<Set<String>> horizontalValues = new ArrayList<>();
      List<Set<String>> verticalValues = new ArrayList<>();
      calculateDistinctValues( horizontalValues, verticalValues );

      // Get all dimension combinations horizontally
      // Then sort this list of lists...
      //
      Set<List<String>> horizontalCombinations = new HashSet<>();
      getCombinations( horizontalValues, 0, horizontalCombinations, new ArrayList<>() );
      details.sortedHorizontalCombinations = sortCombinations( horizontalCombinations );

      // Get the vertical dimension combinations
      // Then sort this list of lists...
      //
      Set<List<String>> verticalCombinations = new HashSet<>();
      getCombinations( verticalValues, 0, verticalCombinations, new ArrayList<>() );
      details.sortedVerticalCombinations = sortCombinations( verticalCombinations );
    }

    // PREPROCESSING IS DONE HERE, CALCULATE THE GRID
    //

    // There are a number of rows above the grid:
    // One row for every horizontal dimension
    // Then one row with the vertical headers and the facts
    //
    enableFont( gc, lookupHorizontalDimensionsFont( renderContext ) );


    for ( int rowNr = 0; rowNr < horizontalDimensions.size(); rowNr++ ) {
      details.nrHeaderLines++;

      List<CellInfo> cellInfos = new ArrayList<>();

      // Add the blanks for the vertical dimensions...
      //
      for ( int dimNr = 0; dimNr < verticalDimensions.size(); dimNr++ ) {
        String columnName = " ";
        LeanTextGeometry geometry = calculateTextGeometry( gc, columnName );
        cellInfos.add( new CellInfo( geometry, columnName, new LeanColumn( "blank" ), LeanVerticalAlignment.TOP, LeanHorizontalAlignment.LEFT ) );
      }
      // Add the horizontal values for this row
      //
      for ( int colNr = 0; colNr < details.sortedHorizontalCombinations.size(); colNr++ ) {
        List<String> horizontalCombination = details.sortedHorizontalCombinations.get( colNr );
        String headerValue = horizontalCombination.get( rowNr );
        LeanTextGeometry geometry = calculateTextGeometry( gc, headerValue );
        LeanColumn column = horizontalDimensions.get( rowNr );
        for ( LeanFact fact : facts ) {
          cellInfos.add( new CellInfo( geometry, headerValue, column, column.getVerticalAlignment(), column.getHorizontalAlignment() ) );
        }
      }
      // Finally, see if we need to add columns for the horizontal aggregations
      //
      if ( showingVerticalTotals ) {
        // We need to show a grand total over the vertical dimensions...
        //
        for ( LeanFact fact : facts ) {
          String headerValue = " "; // Empty space
          LeanTextGeometry geometry = calculateTextGeometry( gc, headerValue );
          cellInfos.add( new CellInfo( geometry, headerValue, fact, fact.getHeaderVerticalAlignment(), fact.getHeaderHorizontalAlignment() ) );
        }
      }
      details.cellInfosList.add( cellInfos );
      details.headerRowFlags.add( true );
    }

    // Now add one row with the vertical dimension headers and facts
    // First the vertical dimension headers
    //
    // TODO: make this optional?
    {
      details.nrHeaderLines++;

      List<CellInfo> cellInfos = new ArrayList<>();

      for ( int dimNr = 0; dimNr < verticalDimensions.size(); dimNr++ ) {
        LeanColumn dimension = verticalDimensions.get( dimNr );
        String headerName = dimension.getHeaderValue();
        if ( headerName == null ) {
          headerName = dimension.getColumnName();
        }
        LeanTextGeometry geometry = calculateTextGeometry( gc, headerName );
        cellInfos.add( new CellInfo( geometry, headerName, dimension, dimension.getVerticalAlignment(), dimension.getHorizontalAlignment() ) );
      }
      // Now add the facts, loop over all the horizontal combinations.
      // If there are no horizontal dimensions, only execute once
      //
      for ( int colNr = 0; colNr < details.sortedHorizontalCombinations.size()
        || colNr == 0 && details.sortedHorizontalCombinations.size() == 0; colNr++ ) {
        for ( LeanFact fact : facts ) {
          // For facts we always need to display the header value
          //
          String headerValue = fact.getHeaderValue();
          if ( headerValue == null ) {
            headerValue = fact.getColumnName();
          }
          LeanTextGeometry geometry = calculateTextGeometry( gc, headerValue );
          cellInfos.add( new CellInfo( geometry, headerValue, fact, fact.getVerticalAlignment(), fact.getHorizontalAlignment() ) );
        }
      }
      if ( showingVerticalTotals ) {
        // We need to show a grand total over the vertical dimensions...
        //
        for ( LeanFact fact : facts ) {
          String headerValue = "Total"; // Empty space
          LeanTextGeometry geometry = calculateTextGeometry( gc, headerValue );
          cellInfos.add( new CellInfo( geometry, headerValue, fact, fact.getHeaderVerticalAlignment(), fact.getHeaderHorizontalAlignment() ) );
        }
      }
      details.cellInfosList.add( cellInfos );
      details.headerRowFlags.add( true );
    }

    // Now we can simply loop over the horizontal and vertical combinations...
    //
    if ( horizontalDimensions.size() == 0 && verticalDimensions.size() == 0 ) {
      // Only the facts please
      //
      List<String> keys = Arrays.asList( "-" );
      List<CellInfo> cellInfos = new ArrayList<>();

      List<String> verticalCombination = Arrays.asList( "-" );
      List<String> horizontalCombination = new ArrayList<>();

      addFacts( gc, keys, cellInfos );

      details.cellInfosList.add( cellInfos );
      details.headerRowFlags.add( false );

    } else {
      // The generic case.
      //
      for ( int rowNr = 0; rowNr < details.sortedVerticalCombinations.size() || rowNr == 0 && details.sortedVerticalCombinations.size() == 0; rowNr++ ) {

        List<String> verticalCombination;
        if ( details.sortedVerticalCombinations.size() > 0 ) {
          verticalCombination = details.sortedVerticalCombinations.get( rowNr );
        } else {
          verticalCombination = new ArrayList<>();
        }

        // This is the current row we're painting...
        //
        List<CellInfo> cellInfos = new ArrayList<>();

        // Now we can add the vertical dimensions without too much of an issue
        //
        enableFont( gc, lookupVerticalDimensionsFont( renderContext ) );
        for ( int i = 0; i < verticalCombination.size(); i++ ) {
          String verticalValue = verticalCombination.get( i );
          LeanColumn dimension = verticalDimensions.get( i );
          LeanTextGeometry geometry = calculateTextGeometry( gc, verticalValue );
          LeanColumn column = verticalDimensions.get( i );
          cellInfos.add( new CellInfo( geometry, verticalValue, column, column.getVerticalAlignment(), column.getHorizontalAlignment() ) );
        }

        // Loop to get the combinations...
        //
        enableFont( gc, lookupFactsFont( renderContext ) );
        for ( int colNr = 0; colNr < details.sortedHorizontalCombinations.size() || colNr == 0 && details.sortedHorizontalCombinations.size() == 0; colNr++ ) {
          List<String> horizontalCombination;
          if ( details.sortedHorizontalCombinations.size() > 0 ) {
            horizontalCombination = details.sortedHorizontalCombinations.get( colNr );
          } else {
            horizontalCombination = new ArrayList<>();
          }
          // Create a key : horizontal values, then vertical
          //
          List<String> keys = new ArrayList();
          keys.addAll( verticalCombination );
          keys.addAll( horizontalCombination );

          // Every combination is an extra column...
          // but we need a column for every fact
          //
          addFacts( gc, keys, cellInfos );
        }

        // Now add vertical aggregations
        //
        if ( showingVerticalTotals ) {
          // We need to show a grand total over the vertical dimensions...
          //
          addFacts( gc, verticalCombination, cellInfos );
        }


        // Here we processed all the facts for the given vertical and horizontal dimensions
        // We can add the rowStrings to the grid
        //
        details.cellInfosList.add( cellInfos );
        details.headerRowFlags.add( false );
      }

      if ( showingHorizontalTotals ) {
        // Add totals for all the horizontal combinations...
        //
        List<CellInfo> cellInfos = new ArrayList<>();

        // Put total in the first column, blanks in the rest
        //
        for ( int i = 0; i < verticalDimensions.size(); i++ ) {
          String text;
          if ( i == 0 ) {
            text = "Total"; // TODO Configure this
          } else {
            text = " ";
          }
          LeanColumn dimension = verticalDimensions.get( i );
          LeanTextGeometry geometry = calculateTextGeometry( gc, text );
          LeanColumn column = verticalDimensions.get( i );

          cellInfos.add( new CellInfo( geometry, text, dimension, dimension.getVerticalAlignment(), dimension.getHorizontalAlignment() ) );
        }

        // Now loop over all the horizontal combinations and add the fact aggregates
        //
        for ( int colNr = 0; colNr < details.sortedHorizontalCombinations.size() || colNr == 0 && details.sortedHorizontalCombinations.size() == 0; colNr++ ) {
          List<String> horizontalCombination;
          if ( details.sortedHorizontalCombinations.size() > 0 ) {
            horizontalCombination = details.sortedHorizontalCombinations.get( colNr );
          } else {
            horizontalCombination = new ArrayList<>();
          }
          // Create a key : horizontal values, then vertical
          //
          List<String> keys = new ArrayList();
          keys.addAll( horizontalCombination );

          // Every combination is an extra column...
          // but we need a column for every fact
          //
          addFacts( gc, keys, cellInfos );
        }
        if ( showingVerticalTotals ) {
          // Add the grand total
          //
          addFacts( gc, Arrays.asList( GRANT_TOTAL_STRING ), cellInfos );
        }

        // Add the new line to the list
        //
        details.cellInfosList.add( cellInfos );
        details.headerRowFlags.add( false );
      }
    }

    // POST PROCESSING
    //

    // Calculate global min and max Y offsets
    //
    for ( List<CellInfo> cellInfos : details.cellInfosList ) {
      for ( CellInfo cellInfo : cellInfos ) {
        if ( details.globalMaxYOffset < cellInfo.geometry.getOffsetY() ) {
          details.globalMaxYOffset = cellInfo.geometry.getOffsetY();
        }
        if ( details.globalMinYOffset > cellInfo.geometry.getOffsetY() ) {
          details.globalMinYOffset = cellInfo.geometry.getOffsetY();
        }
      }
    }

    // Now we calculate the maximum column widths so that we can render correctly from top to bottom
    //
    int nrRows = details.cellInfosList.size();
    int nrColumns = details.cellInfosList.get( 0 ).size();

    int globalMaxHeight = 0;

    if ( details.cellInfosList.size() > 0 ) {
      // Get the maximum width of every column
      // Get the maximum Y offset for every column
      //
      for ( int colNr = 0; colNr < nrColumns; colNr++ ) {
        details.maxWidths.add( 0 );
      }
      for ( int colNr = 0; colNr < nrColumns; colNr++ ) {
        for ( int rowNr = 0; rowNr < nrRows; rowNr++ ) {
          List<CellInfo> cellInfos = details.cellInfosList.get( rowNr );
          LeanTextGeometry geometry = cellInfos.get( colNr ).geometry;
          if ( details.maxWidths.get( colNr ) < geometry.getWidth() ) {
            details.maxWidths.set( colNr, geometry.getWidth() );
          }
        }
      }

      // Get the maximum height of every row
      //
      for ( int rowNr = 0; rowNr < nrRows; rowNr++ ) {
        int maxHeight = 0;
        for ( int colNr = 0; colNr < nrColumns; colNr++ ) {
          LeanTextGeometry geometry = details.cellInfosList.get( rowNr ).get( colNr ).geometry;
          if ( maxHeight < geometry.getHeight() ) {
            maxHeight = geometry.getHeight();
          }
        }
        details.maxHeights.add( maxHeight );
        if ( maxHeight > globalMaxHeight ) {
          globalMaxHeight = maxHeight;
        }
      }
    }

    // If evenHeights set highest row for all rows
    //
    if ( evenHeights ) {
      // If we have a header, leave that one alone.
      //
      for ( int i = 0; i < details.maxHeights.size(); i++ ) {
        details.maxHeights.set( i, globalMaxHeight );
      }
    }

    // Total width?
    //
    details.totalWidth = 0;
    for ( int width : details.maxWidths ) {
      details.totalWidth += width + 2 * horizontalMargin;
    }

    details.totalHeight = 0;
    for ( int height : details.maxHeights ) {
      details.totalHeight += height + 2 * verticalMargin;
    }

    // Save the details for later
    //
    results.addDataSet( component, DATA_CROSSTAB_DETAILS, details );
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
   * @throws LeanException
   */
  public LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    if ( component.isDynamic() ) {
      CrosstabDetails details = (CrosstabDetails) results.getDataSet( component, DATA_CROSSTAB_DETAILS );
      return new LeanSize( details.totalWidth, details.totalHeight );
    } else {
      return component.getSize();
    }
  }

  private void addFacts( SVGGraphics2D gc, List<String> keys, List<CellInfo> cellInfos ) throws LeanException {
    for ( int factNr = 0; factNr < pivotMapList.size(); factNr++ ) {
      LeanFact fact = facts.get( factNr );
      Map<List<String>, Object> pivotMap = pivotMapList.get( factNr );
      Map<List<String>, Long> countMap = countMapList.get( factNr );

      // Make a copy so we can change format masks
      // TODO: cache this clone somewhere for performance
      //
      IValueMeta valueMeta = inputRowMeta.getValueMeta( factIndexes.get( factNr ) ).clone();
      if ( fact.getFormatMask() != null ) {
        valueMeta.setConversionMask( fact.getFormatMask() );
      }

      Long count = countMap.get( keys );
      Object object = pivotMap.get( keys );
      String factString;
      try {
        switch ( fact.getAggregationMethod() ) {
          case SUM:
            if ( valueMeta.isNull( object ) ) {
              factString = " ";
            } else {
              factString = valueMeta.getString( object );
            }
            break;
          case AVERAGE:
            if ( valueMeta.isNull( object ) ) {
              factString = " ";
            } else {
              Double sum = (Double) object;
              sum /= count;
              factString = valueMeta.getString( sum );
            }
            break;
          case COUNT:
            if ( count == null ) {
              factString = " ";
            } else {
              ValueMetaInteger intValueMeta = new ValueMetaInteger( valueMeta.getName() );
              intValueMeta.setConversionMask( valueMeta.getConversionMask() );
              factString = intValueMeta.getString( count );
            }
            break;
          default:
            throw new LeanException( "Unsupported aggregation exception : " + fact.getAggregationMethod() );
        }
      } catch ( HopValueException e ) {
        factString = "!?";
      }

      LeanTextGeometry geometry = calculateTextGeometry( gc, factString );
      CellInfo cellInfo = new CellInfo( geometry, factString, fact, fact.getVerticalAlignment(), fact.getHorizontalAlignment() );
      cellInfos.add( cellInfo );
    }
  }

  private void processVertical( List<List<String>> sortedVerticalValues,
                                int columnNr, int verticalIndex,
                                List<List<String>> rowStringsList, List<String> currentRow ) {
  }

  @SuppressWarnings( "unchecked" )
  @Override public void doLayout( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                  LeanLayoutResults results ) throws LeanException {

    // Get the current page on which we're rendering...
    // Create a new one if we need to move on to a next page
    //
    LeanRenderPage renderPage = results.getCurrentRenderPage( page );

    // Calculate the expected geometry for this component
    //
    LeanGeometry expectedGeometry = getExpectedGeometry( presentation, page, component, dataContext, renderContext, results );

    // Get the details back
    //
    CrosstabDetails details = (CrosstabDetails) results.getDataSet( component, DATA_CROSSTAB_DETAILS );

    // Calculate the height until the end of the page...
    // How much more can we fit onto the page?
    //
    boolean addFragment = true;
    int partNumber = 1;

    int remainingHeight = presentation.getUsableHeight( page ) - expectedGeometry.getY();

    List<List<CellInfo>> cellInfosList = details.cellInfosList;
    List<Integer> maxWidths = details.maxWidths;
    List<Integer> maxHeights = details.maxHeights;
    IRowMeta rowMeta = inputRowMeta;

    // Loop over all the rows, see how many we can fit onto this page, then create another one.
    //
    int startLine = 0;
    int partHeight = 0;

    int rowNr = 0;
    for ( ; rowNr < maxHeights.size(); rowNr++ ) {
      int maxHeight = maxHeights.get( rowNr );
      int rowHeight = maxHeight + 2 * verticalMargin;
      partHeight += rowHeight;

      // Did we leave the page at the bottom?
      //
      if ( partHeight > remainingHeight ) {

        // Save previous until the previous row...
        //
        LeanGeometry partGeometry = expectedGeometry.clone();
        partHeight -= rowHeight;

        if ( headerOnEveryPage && partNumber > 1 ) {
          // The part is actually a bit taller...
          // Add the header lines * 2*margin per line
          //
          partHeight += details.nrHeaderLines * ( maxHeights.get( 0 ) + 2 * verticalMargin );
        }
        partGeometry.setHeight( partHeight );

        // Add this as a new component part
        //
        addPartLayoutResult( results, renderPage, page, component, partGeometry, partNumber, startLine, rowNr );

        // Create a new page
        //
        partNumber++;
        renderPage = results.addNewPage( page, renderPage.getPageNumber() + 1 );
        remainingHeight = presentation.getUsableHeight( page );

        if ( headerOnEveryPage ) {
          // Reserve room for a header on the new page...
          //
          remainingHeight -= maxHeights.get( 0 );
        }

        // keep track for the new part...
        //
        startLine = rowNr;
        partHeight = rowHeight;
        expectedGeometry.setY( 0 );
      }
    }

    // Let's not forget the top part on the last page...
    //
    if ( partHeight > 0 ) {
      LeanGeometry partGeometry = expectedGeometry.clone();
      // Only a part of the total height!
      //
      partGeometry.setHeight( partHeight );

      // Extra for the new table header?
      //
      if ( headerOnEveryPage && partNumber > 1 ) {
        // The part is actually a bit taller...
        //
        partGeometry.incHeight( details.nrHeaderLines * ( maxHeights.get( 0 ) + 2 * verticalMargin ) );
      }

      addPartLayoutResult( results, renderPage, page, component, partGeometry, partNumber, startLine, rowNr );
    }

  }

  private void addPartLayoutResult( LeanLayoutResults results, LeanRenderPage renderPage, LeanPage page, LeanComponent component, LeanGeometry partGeometry, int partNumber, int startLine,
                                    int endLine ) {
    LeanComponentLayoutResult result = new LeanComponentLayoutResult();
    result.setRenderPage( renderPage );
    result.setSourcePage( page );
    result.setComponent( component );
    result.setGeometry( partGeometry );
    result.setPartNumber( partNumber );
    result.getDataMap().put( DATA_START_ROW, startLine );
    result.getDataMap().put( DATA_END_ROW, endLine );

    // Store the geometry also in the results for layout purposes...
    //
    results.addComponentGeometry( component.getName(), partGeometry );

    renderPage.getLayoutResults().add( result );

    renderPage.addDrawnItem( component.getName(), partNumber, "ComponentPart", null, 0, 0, partGeometry );
  }

  @Override public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {

    LeanComponent component = layoutResult.getComponent();
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    CrosstabDetails details = (CrosstabDetails) results.getDataSet( component, DATA_CROSSTAB_DETAILS );

    SVGGraphics2D gc = layoutResult.getRenderPage().getGc();

    // Get sizes and string values from data set...
    //
    List<List<CellInfo>> cellInfosList = details.cellInfosList;
    List<Integer> maxWidths = details.maxWidths;
    List<Integer> maxHeights = details.maxHeights;
    int globalMaxYOffset = details.globalMaxYOffset;
    int globalMinYOffset = details.globalMinYOffset;

    int avgYOffset = ( globalMaxYOffset + globalMinYOffset ) / 2;

    int startRow = (int) layoutResult.getDataMap().get( DATA_START_ROW );
    int endRow = (int) layoutResult.getDataMap().get( DATA_END_ROW );

    // Now start drawing the table...
    //
    int y = componentGeometry.getY();
    int nrHeaderRows = horizontalDimensions.size() + 1;

    if ( headerOnEveryPage && startRow > 0 ) {
      // Render the header, data is on rows 0-dimensions.imageSize()+1
      // Plus one for the vertical dimension headers and the facts
      //
      int maxHeight = maxHeights.get( 0 );
      for ( int i = 0; i < nrHeaderRows; i++ ) {
        y = renderLine( gc, y, i, maxHeight, cellInfosList,
          componentGeometry, maxWidths, true, nrHeaderRows, avgYOffset, renderContext );
      }
    }

    for ( int rowNr = startRow; rowNr < endRow; rowNr++ ) {
      int maxHeight = maxHeights.get( rowNr );
      y = renderLine( gc, y, rowNr, maxHeight, cellInfosList,
        componentGeometry, maxWidths, rowNr < nrHeaderRows, nrHeaderRows, avgYOffset, renderContext );
    }

    drawBorder( gc, componentGeometry, renderContext );
  }

  private int renderLine( SVGGraphics2D gc, int y, int rowNr, int maxHeight, List<List<CellInfo>> cellInfosList,
                          LeanGeometry componentGeometry, List<Integer> maxWidths,
                          boolean headerRow, int nrHeaderRows, int yOffset, IRenderContext renderContext ) throws LeanException {

    List<CellInfo> cellInfos = cellInfosList.get( rowNr );

    int x = componentGeometry.getX();

    if ( maxWidths.size() != cellInfos.size() ) {
      throw new RuntimeException( "Grid calculation error!" );
    }

    // Grouping information
    //
    String groupText = null;
    int groupStartX = x;
    int groupWidth = 0;
    LeanColumn groupColumn = null;
    LeanTextGeometry groupTextGeometry = null;
    int groupColNr = 0;
    LeanHorizontalAlignment groupHorizontalAlignment = null;
    LeanVerticalAlignment groupVerticalAlignment = null;

    for ( int c = 0; c < cellInfos.size(); c++ ) {
      int maxWidth = maxWidths.get( c );
      LeanTextGeometry textGeometry = cellInfos.get( c ).geometry;
      LeanColumn leanColumn = cellInfos.get( c ).column;
      String text = cellInfos.get( c ).text;
      LeanHorizontalAlignment horizontalAlignment = cellInfos.get( c ).horizontalAlignment;
      LeanVerticalAlignment verticalAlignment = cellInfos.get( c ).verticalAlignment;

      if ( rowNr < nrHeaderRows || headerOnEveryPage && headerRow ) {
        // The header rows block...
        // Get the column from the headers...
        //
        enableFont( gc, lookupHorizontalDimensionsFont( renderContext ) );
        enableColor( gc, lookupHorizontalDimensionsColor( renderContext ) );
      } else {
        if ( c < verticalDimensions.size() ) {
          enableFont( gc, lookupVerticalDimensionsFont( renderContext ) );
          enableColor( gc, lookupVerticalDimensionsColor( renderContext ) );
        } else {
          enableFont( gc, lookupFactsFont( renderContext ) );
          enableColor( gc, lookupFactsColor( renderContext ) );
        }
      }

      // See if we need to group values together in the header
      //
      if ( rowNr < nrHeaderRows || headerOnEveryPage && headerRow ) {

        if ( text.equals( groupText ) ) {
          // Extend the active group
          //
          groupWidth += maxWidth + horizontalMargin * 2;

        } else {
          // Draw the previous group if there is one, start a new one
          //
          if ( groupText != null ) {
            renderLineCell( gc, groupStartX, y, rowNr, groupColNr, groupText,
              nrHeaderRows, groupWidth, maxHeight, yOffset, groupColumn, groupTextGeometry,
              groupHorizontalAlignment, groupVerticalAlignment, renderContext );
          }
          groupStartX = x;
          groupText = text;
          groupWidth = maxWidth;
          groupTextGeometry = textGeometry;
          groupColumn = leanColumn;
          groupColNr = c;
          groupHorizontalAlignment = horizontalAlignment;
          groupVerticalAlignment = verticalAlignment;
        }
      } else {
        renderLineCell( gc, x, y, rowNr, c, text, nrHeaderRows, maxWidth, maxHeight, yOffset,
          leanColumn, textGeometry, horizontalAlignment, verticalAlignment, renderContext );
      }
      x += maxWidth + horizontalMargin * 2;
    }
    // See if we need to draw the last group
    //
    if ( groupText != null ) {
      renderLineCell( gc, groupStartX, y, rowNr, groupColNr, groupText, nrHeaderRows, groupWidth, maxHeight, yOffset,
        groupColumn, groupTextGeometry, groupHorizontalAlignment, groupVerticalAlignment, renderContext );
    }
    y += maxHeight + verticalMargin * 2;

    return y;
  }

  private void renderLineCell( SVGGraphics2D gc, int x, int y, int rowNr, int c, String text, int nrHeaderRows, int maxWidth, int maxHeight, int yOffset, LeanColumn leanColumn,
                               LeanTextGeometry textGeometry, LeanHorizontalAlignment horizontalAlignment, LeanVerticalAlignment verticalAlignment, IRenderContext renderContext )
    throws LeanException {
    // Don't theme in the top left cell
    //
    boolean emptyCell = c < verticalDimensions.size() && rowNr < nrHeaderRows - 1;
    if ( !emptyCell ) {
      // Fill the background of the cell
      //
      if ( isBackground() ) {
        enableColor( gc, lookupBackgroundColor( renderContext ) );
        gc.fillRect( x, y, maxWidth + horizontalMargin * 2, maxHeight + verticalMargin * 2 );
      }
    }

    enableColor( gc, lookupDefaultColor( renderContext ) );

    int cellWidth = maxWidth + horizontalMargin * 2;
    int cellHeight = maxHeight + verticalMargin * 2;
    int positionX;
    int positionY;

    switch ( verticalAlignment ) {
      case TOP:
        positionY = y + textGeometry.getHeight() + verticalMargin;
        break;
      case BOTTOM:
        positionY = y + cellHeight - verticalMargin;
        break;
      case MIDDLE:
        positionY = y + cellHeight / 2 + textGeometry.getHeight() / 2;
        break;
      default:
        throw new LeanException( "Unsupported vertical alignment : " + verticalAlignment );
    }

    switch ( horizontalAlignment ) {
      case LEFT:
        positionX = x + textGeometry.getOffsetX() + horizontalMargin;
        break;
      case RIGHT:
        positionX = x + cellWidth - horizontalMargin - textGeometry.getWidth();
        break;
      case CENTER:
        positionX = x + ( cellWidth - textGeometry.getWidth() ) / 2;
        break;
      default:
        throw new LeanException( "Unsupported horizontal alignment : " + horizontalAlignment );
    }

    Stroke baseStroke = gc.getStroke();

    //gc.setStroke(new BasicStroke(0.1f));
    //gc.drawRect( positionX, positionY-textGeometry.getHeight(), textGeometry.getWidth(), textGeometry.getHeight() );
    //gc.setStroke( baseStroke );

    gc.drawString( text, positionX, positionY );


    // Don't draw a rectangle around the top left cell
    //
    if ( !emptyCell ) {
      // draw the rectangle
      //
      LeanColorRGB oldColor = enableColor( gc, lookupGridColor( renderContext ) );
      gc.setStroke( new BasicStroke( 0.5f ) );
      gc.drawRect( x, y, cellWidth, cellHeight );
      gc.setStroke( baseStroke );
      enableColor( gc, oldColor );
    }
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
   * Gets evenHeights
   *
   * @return value of evenHeights
   */
  public boolean isEvenHeights() {
    return evenHeights;
  }

  /**
   * @param evenHeights The evenHeights to set
   */
  public void setEvenHeights( boolean evenHeights ) {
    this.evenHeights = evenHeights;
  }

  /**
   * Gets headerOnEveryPage
   *
   * @return value of headerOnEveryPage
   */
  public boolean isHeaderOnEveryPage() {
    return headerOnEveryPage;
  }

  /**
   * @param headerOnEveryPage The headerOnEveryPage to set
   */
  public void setHeaderOnEveryPage( boolean headerOnEveryPage ) {
    this.headerOnEveryPage = headerOnEveryPage;
  }

  /**
   * Gets showingHorizontalSubtotals
   *
   * @return value of showingHorizontalSubtotals
   */
  public boolean isShowingHorizontalSubtotals() {
    return showingHorizontalSubtotals;
  }

  /**
   * @param showingHorizontalSubtotals The showingHorizontalSubtotals to set
   */
  public void setShowingHorizontalSubtotals( boolean showingHorizontalSubtotals ) {
    this.showingHorizontalSubtotals = showingHorizontalSubtotals;
  }

  /**
   * Gets showingVerticalSubtotals
   *
   * @return value of showingVerticalSubtotals
   */
  public boolean isShowingVerticalSubtotals() {
    return showingVerticalSubtotals;
  }

  /**
   * @param showingVerticalSubtotals The showingVerticalSubtotals to set
   */
  public void setShowingVerticalSubtotals( boolean showingVerticalSubtotals ) {
    this.showingVerticalSubtotals = showingVerticalSubtotals;
  }
}
