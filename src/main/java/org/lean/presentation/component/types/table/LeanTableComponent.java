package org.lean.presentation.component.types.table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanFont;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanSize;
import org.lean.core.LeanTextGeometry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize( as = LeanTableComponent.class )
@LeanComponentPlugin(
  id= "LeanTableComponent",
  name="Table",
  description = "A table component"
)
public class LeanTableComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_TABLE_DETAILS = "table_details";
  private static final String DATA_START_ROW = "DATA_START_ROW";
  private static final String DATA_END_ROW = "DATA_END_ROW";

  @HopMetadataProperty
  private List<LeanColumn> columnSelection;

  @HopMetadataProperty
  private LeanColorRGB gridColor;

  @HopMetadataProperty
  private int horizontalMargin;

  @HopMetadataProperty
  private int verticalMargin;

  @HopMetadataProperty
  private boolean evenHeights;

  @HopMetadataProperty
  private boolean headerOnEveryPage;

  @HopMetadataProperty
  private boolean header;

  @HopMetadataProperty
  private LeanFont headerFont;

  @HopMetadataProperty
  private String gridLineWidth;

  public LeanTableComponent() {
    super( "LeanTableComponent" );
    columnSelection = new ArrayList<>();
  }

  public LeanTableComponent( String connectorName, List<LeanColumn> columnSelection ) {
    this();
    super.sourceConnectorName = connectorName;
    this.columnSelection = columnSelection;
  }

  public LeanTableComponent( LeanTableComponent c ) {
    super( "LeanTableComponent", c );
    this.columnSelection = new ArrayList<>();
    for ( LeanColumn lc : c.columnSelection ) {
      this.columnSelection.add( new LeanColumn( lc ) );
    }
    this.gridColor = c.gridColor == null ? null : new LeanColorRGB( c.gridColor );
    this.horizontalMargin = c.horizontalMargin;
    this.verticalMargin = c.verticalMargin;
    this.evenHeights = c.evenHeights;
    this.headerOnEveryPage = c.headerOnEveryPage;
    this.header = c.header;
    this.headerFont = c.headerFont == null ? null : new LeanFont( c.headerFont );
  }

  public LeanTableComponent clone() {
    return new LeanTableComponent( this );
  }


  @Override public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                           LeanLayoutResults results ) throws LeanException {
    TableDetails details = new TableDetails();

    LeanConnector connector = dataContext.getConnector( sourceConnectorName );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector '" + sourceConnectorName + "'" );
    }

    // Get the rows
    //
    details.rows = connector.retrieveRows( dataContext );

    // Calculate the width and height of the text in the given font
    //
    SVGGraphics2D gc = HopSvgGraphics2D.newDocument();

    // Get sizes and string values
    //
    details.columnSizesList = new ArrayList<>();
    details.rowStringsList = new ArrayList<>();
    details.maxWidths = new ArrayList<>();
    details.maxHeights = new ArrayList<>();

    details.rowMeta = getRowsAndFieldInformation( gc, details.rows, details.columnSizesList, details.rowStringsList, details.maxWidths, details.maxHeights, renderContext );

    // Total width?
    //
    for ( int width : details.maxWidths ) {
      details.totalWidth += width + 2 * horizontalMargin;
    }

    int totalHeight = 0;
    for ( int height : details.maxHeights ) {
      details.totalHeight += height + 2 * verticalMargin;
    }

    // Before we go, store all the information we already collected so we don't have to calculate it all again...
    //
    results.addDataSet( component, DATA_TABLE_DETAILS, details );
  }

  /**
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

      // Get data information back
      //
      TableDetails details = (TableDetails) results.getDataSet( component, DATA_TABLE_DETAILS );

      // Retain the location, adjust the width and Height
      //
      return new LeanSize( details.totalWidth, details.totalHeight );

    } else {

      return component.getSize();
    }
  }

  @Override public void doLayout( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                  LeanLayoutResults results ) throws LeanException {

    // Get data information back
    //
    TableDetails details = (TableDetails) results.getDataSet( component, DATA_TABLE_DETAILS );

    // In case we have no specified columns, we take all the input data...
    //
    if ( columnSelection.size() == 0 ) {
      LeanConnector connector = dataContext.getConnector( sourceConnectorName );
      if ( connector == null ) {
        throw new LeanException( "Unable to find connector '" + sourceConnectorName + "'" );
      }
      IRowMeta inputFields = connector.getConnector().describeOutput( dataContext );
      for ( IValueMeta inputField : inputFields.getValueMetaList() ) {
        LeanColumn column = new LeanColumn( inputField.getName() );
        columnSelection.add( column );
      }
    }

    // Get the current page on which we're rendering...
    // Create a new one if we need to move on to a next page
    //
    LeanRenderPage renderPage = results.getCurrentRenderPage( page );

    // Calculate the expected geometry for this component
    //
    LeanGeometry expectedGeometry = getExpectedGeometry( presentation, page, component, dataContext, renderContext, results );

    // Calculate the height until the end of the page...
    // How much more can we fit onto the page?
    //
    boolean addFragment = true;
    int partNumber = 1;

    int remainingHeight = presentation.getUsableHeight( page ) - expectedGeometry.getY();


    List<List<LeanTextGeometry>> columnSizesList = details.columnSizesList;
    List<List<String>> rowStringsList = details.rowStringsList;
    List<Integer> maxWidths = details.maxWidths;
    List<Integer> maxHeights = details.maxHeights;
    IRowMeta rowMeta = details.rowMeta;

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

        if ( header && headerOnEveryPage && partNumber > 1 ) {
          // The part is actually a bit taller...
          //
          partHeight += maxHeights.get( 0 ) + 2 * verticalMargin;
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

        if ( header && headerOnEveryPage ) {
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

    // Let's not forget the dangling part on the last page...
    //
    if ( partHeight > 0 ) {
      LeanGeometry partGeometry = expectedGeometry.clone();
      if ( header && headerOnEveryPage && partNumber > 1 ) {
        // The part is actually a bit taller...
        //
        partHeight += maxHeights.get( 0 ) + 2 * verticalMargin;
      }
      partGeometry.setHeight( partHeight );

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
    TableDetails details = (TableDetails) results.getDataSet( component, DATA_TABLE_DETAILS );

    SVGGraphics2D gc = layoutResult.getRenderPage().getGc();

    setBackgroundBorderFont( gc, componentGeometry, renderContext );

    // Get sizes and string values from data set...
    //
    List<List<LeanTextGeometry>> columnSizesList = details.columnSizesList;
    List<List<String>> rowStringsList = details.rowStringsList;
    List<Integer> maxWidths = details.maxWidths;
    List<Integer> maxHeights = details.maxHeights;

    int startRow = (int) layoutResult.getDataMap().get( DATA_START_ROW );
    int endRow = (int) layoutResult.getDataMap().get( DATA_END_ROW );

    // Now start drawing the table...
    //
    int y = componentGeometry.getY();

    if ( header && headerOnEveryPage && startRow > 0 ) {
      // Render the header, data is on row 0
      //
      int maxHeight = maxHeights.get( 0 );
      y = renderLine( gc, y, 0, maxHeight, rowStringsList, columnSizesList, componentGeometry, maxWidths, true, renderContext );
    }

    for ( int rowNr = startRow; rowNr < endRow; rowNr++ ) {
      int maxHeight = maxHeights.get( rowNr );
      y = renderLine( gc, y, rowNr, maxHeight, rowStringsList, columnSizesList, componentGeometry, maxWidths, rowNr == 0, renderContext );
    }

    drawBorder( gc, componentGeometry, renderContext );
  }

  private int renderLine( SVGGraphics2D gc, int y, int rowNr, int maxHeight, List<List<String>> rowStringsList, List<List<LeanTextGeometry>> columnSizesList,
                          LeanGeometry componentGeometry, List<Integer> maxWidths, boolean firstRow, IRenderContext renderContext ) throws LeanException {
    List<LeanTextGeometry> columnSizes = columnSizesList.get( rowNr );
    List<String> rowStrings = rowStringsList.get( rowNr );

    int x = componentGeometry.getX();

    for ( int c = 0; c < columnSizes.size(); c++ ) {
      LeanColumn leanColumn = columnSelection.get( c );
      int maxWidth = maxWidths.get( c );
      LeanTextGeometry textGeometry = columnSizes.get( c );
      String text = rowStrings.get( c );

      enableColor( gc, lookupDefaultColor( renderContext ) );
      if ( header && ( rowNr == 0 || headerOnEveryPage && firstRow ) ) {
        enableFont( gc, headerFont );
      } else {
        enableFont( gc, lookupDefaultFont( renderContext ) );
      }

      switch ( leanColumn.getHorizontalAlignment() ) {
        case LEFT:
          gc.drawString( text, x + textGeometry.getOffsetX() + horizontalMargin, y + textGeometry.getOffsetY() + verticalMargin );
          break;

        case RIGHT:
          gc.drawString( text, x + maxWidth + horizontalMargin - textGeometry.getWidth(), y + textGeometry.getOffsetY() + verticalMargin );
          break;
        case CENTER:
          gc.drawString( text, x + ( ( maxWidth + horizontalMargin * 2 ) - textGeometry.getWidth() ) / 2, y + textGeometry.getOffsetY() + verticalMargin );
          break;
      }

      enableColor( gc, lookupGridColor( renderContext ) );
      Stroke oldStroke = gc.getStroke();
      if ( StringUtils.isNotEmpty( gridLineWidth ) ) {
        gc.setStroke( new BasicStroke( Float.valueOf( gridLineWidth ) ) );
      }
      gc.drawRect( x, y, maxWidth + horizontalMargin * 2, maxHeight + verticalMargin * 2 );
      if ( StringUtils.isNotEmpty( gridLineWidth ) ) {
        gc.setStroke( oldStroke );
      }

      x += maxWidth + horizontalMargin * 2;
    }
    y += maxHeight + verticalMargin * 2;

    return y;
  }

  public LeanColumn findColumn( String columnName ) {
    for ( LeanColumn column : columnSelection ) {
      if ( column.getColumnName().equalsIgnoreCase( columnName ) ) {
        return column;
      }
    }
    return null;
  }

  private IRowMeta getRowsAndFieldInformation( SVGGraphics2D gc, List<RowMetaAndData> rows, List<List<LeanTextGeometry>> columnSizesList, List<List<String>> rowStringsList,
                                               List<Integer> maxWidths, List<Integer> maxHeights, IRenderContext renderContext ) throws LeanException {
    // No rows: all done
    //
    if ( rows.size() == 0 ) {
      return null;
    }

    IRowMeta rowMetaInput = rows.get( 0 ).getRowMeta();
    IRowMeta rowMeta = new RowMeta();
    int columnIndexes[] = new int[ columnSelection.size() ];

    for ( int i = 0; i < columnSelection.size(); i++ ) {
      LeanColumn leanColumn = columnSelection.get( i );
      int valueMetaIndex = rowMetaInput.indexOfValue( leanColumn.getColumnName() );
      if ( valueMetaIndex >= 0 ) {
        IValueMeta valueMeta = rowMetaInput.getValueMeta( valueMetaIndex ).clone();
        columnIndexes[ i ] = valueMetaIndex;
        rowMeta.addValueMeta( valueMeta );
      } else {
        throw new LeanException( "Unable to find column '" + leanColumn.getColumnName() + "' in the connector input" );
      }
    }

    for ( int i = 0; i < columnIndexes.length; i++ ) {
      IValueMeta valueMeta = rowMetaInput.getValueMeta( columnIndexes[ i ] );
      LeanColumn leanColumn = columnSelection.get( i );
      if ( StringUtils.isNotEmpty( leanColumn.getFormatMask() ) ) {
        valueMeta.setConversionMask( leanColumn.getFormatMask() );
      }
    }

    // Set length min values
    //
    for ( int i = 0; i < columnSelection.size(); i++ ) {
      maxWidths.add( 0 );
    }

    // Calculate header sizes...
    //
    if ( header ) {
      // One header font for all values
      //
      enableFont( gc, headerFont );
      List<LeanTextGeometry> columnSizes = new ArrayList<>();
      List<String> rowStrings = new ArrayList<>();
      int maxHeight = 0;
      for ( int i = 0; i < columnSelection.size(); i++ ) {
        LeanColumn leanColumn = columnSelection.get( i );
        IValueMeta valueMeta = rowMeta.getValueMeta( columnIndexes[ i ] );

        String text;
        if ( StringUtils.isNotEmpty( leanColumn.getHeaderValue() ) ) {
          text = leanColumn.getHeaderValue();
        } else {
          text = leanColumn.getColumnName();
        }

        // We print the name in the header...
        //
        rowStrings.add( text );

        LeanTextGeometry textGeometry = calculateTextGeometry( gc, text );

        columnSizes.add( textGeometry );

        maxWidths.set( i, textGeometry.getWidth() );
        if ( textGeometry.getHeight() > maxHeight ) {
          maxHeight = textGeometry.getHeight();
        }
      }
      columnSizesList.add( columnSizes );
      maxHeights.add( maxHeight );
      rowStringsList.add( rowStrings );
    }

    // First determine field string sizes...
    //
    int globalMaxHeight = 0;
    for ( RowMetaAndData row : rows ) {
      List<LeanTextGeometry> columnSizes = new ArrayList<>();
      List<String> rowStrings = new ArrayList<>();
      int maxHeight = 0;
      for ( int i = 0; i < columnIndexes.length; i++ ) {
        LeanColumn leanColumn = columnSelection.get( i );
        IValueMeta valueMeta = rowMeta.getValueMeta( i );

        String text;
        try {
          text = valueMeta.getString( row.getData()[ columnIndexes[ i ] ] );
        } catch ( HopValueException e ) {
          text = e.getMessage();
        }
        rowStrings.add( text );

        enableFont( gc, lookupDefaultFont( renderContext ) );
        LeanTextGeometry textGeometry = calculateTextGeometry( gc, text );

        columnSizes.add( textGeometry );

        if ( textGeometry.getWidth() > maxWidths.get( i ) ) {
          maxWidths.set( i, textGeometry.getWidth() );
        }
        if ( textGeometry.getHeight() > maxHeight ) {
          maxHeight = textGeometry.getHeight();
        }
      }
      columnSizesList.add( columnSizes );
      maxHeights.add( maxHeight );
      rowStringsList.add( rowStrings );
      if ( maxHeight > globalMaxHeight ) {
        globalMaxHeight = maxHeight;
      }
    }

    // The text geometry can be different for each string on a line.
    // Therefor we need to calculate and get the maximum offsets
    //
    for ( List<LeanTextGeometry> columnSizes : columnSizesList ) {
      int maxOffSetY = 0;
      for ( LeanTextGeometry columnSize : columnSizes ) {
        if ( columnSize.getOffsetY() > maxOffSetY ) {
          maxOffSetY = columnSize.getOffsetY();
        }
      }
      for ( LeanTextGeometry columnSize : columnSizes ) {
        columnSize.setOffsetY( maxOffSetY );
      }
    }


    // If evenHeights set highest row for all rows
    //
    if ( evenHeights ) {
      // If we have a header, leave that one alone.
      //
      for ( int i = 0; i < maxHeights.size(); i++ ) {
        maxHeights.set( i, globalMaxHeight );
      }
    }
    return rowMeta;
  }

  protected LeanColorRGB lookupGridColor( IRenderContext renderContext ) throws LeanException {
    if ( gridColor != null ) {
      return gridColor;
    }
    LeanColorRGB color = null;
    LeanTheme theme = renderContext.lookupTheme( themeName );
    if ( theme != null ) {
      return theme.lookupGridColor();
    }
    if ( getDefaultColor() != null ) {
      return getDefaultColor();
    }
    throw new LeanException( "No grid color nor default color defined (no theme used or found)" );
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
   * Gets gridColor
   *
   * @return value of gridColor
   */
  public LeanColorRGB getGridColor() {
    return gridColor;
  }

  /**
   * @param gridColor The gridColor to set
   */
  public void setGridColor( LeanColorRGB gridColor ) {
    this.gridColor = gridColor;
  }

  /**
   * Gets headerFont
   *
   * @return value of headerFont
   */
  public LeanFont getHeaderFont() {
    return headerFont;
  }

  /**
   * @param headerFont The headerFont to set
   */
  public void setHeaderFont( LeanFont headerFont ) {
    this.headerFont = headerFont;
  }

  /**
   * Gets header
   *
   * @return value of header
   */
  public boolean isHeader() {
    return header;
  }

  /**
   * @param header The header to set
   */
  public void setHeader( boolean header ) {
    this.header = header;
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
   * Gets columnSelection
   *
   * @return value of columnSelection
   */
  public List<LeanColumn> getColumnSelection() {
    return columnSelection;
  }

  /**
   * @param columnSelection The columnSelection to set
   */
  public void setColumnSelection( List<LeanColumn> columnSelection ) {
    this.columnSelection = columnSelection;
  }

  /**
   * Gets gridLineWidth
   *
   * @return value of gridLineWidth
   */
  public String getGridLineWidth() {
    return gridLineWidth;
  }

  /**
   * @param gridLineWidth The gridLineWidth to set
   */
  public void setGridLineWidth( String gridLineWidth ) {
    this.gridLineWidth = gridLineWidth;
  }
}
