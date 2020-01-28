package org.lean.presentation.component.types.group;

import org.lean.core.LeanAttachment;
import org.lean.core.LeanColumn;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanSize;
import org.lean.core.LeanSortMethod;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.types.chain.LeanChainConnector;
import org.lean.presentation.connector.types.distinct.LeanDistinctConnector;
import org.lean.presentation.connector.types.selection.LeanSelectionConnector;
import org.lean.presentation.connector.types.sort.LeanSortConnector;
import org.lean.presentation.datacontext.ChainDataContext;
import org.lean.presentation.datacontext.GroupDataContext;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a groupComponent which repeats the given groupComponent for every row in the given connector.
 * Optionally you can select columns, perform sort/distinct
 * <p>
 * First we get the rows for the groups:
 *
 * @see ILeanComponent#processSourceData(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Then we calculate the expected size of the composite.
 * Obviously, this size is dynamic so it's hard to know unless we calculate all the sizes of the groupComponent given the input data.
 * So we simply read all the data in memory for now and calculate the sizes of all the expected sizes of all the group elements.
 * That's what we'll return.
 * @see LeanGroupComponent#getExpectedSize(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Now we need to spread the groups over the pages.  This happens in:
 * <p>
 * @see ILeanComponent#doLayout(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Finally, we render all what we've calculated looping once again over the groups.
 * @see ILeanComponent#render(LeanComponentLayoutResult, LeanLayoutResults, IRenderContext)
 *
 */
@JsonDeserialize( as = LeanGroupComponent.class )
public class LeanGroupComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_GROUP_DETAILS = "DATA_GROUP_DETAILS";

  @MetaStoreAttribute
  private List<LeanColumn> columnSelection;

  @MetaStoreAttribute
  private List<LeanSortMethod> columnSorts;

  @MetaStoreAttribute
  private boolean distinctSelection;

  @MetaStoreAttribute
  private LeanComponent groupComponent;

  @MetaStoreAttribute
  private int verticalMargin;

  public LeanGroupComponent() {
    super( "LeanGroupComponent");

    columnSelection = new ArrayList<>();
    columnSorts = new ArrayList<>();
  }

  public LeanGroupComponent( String connectorName, List<LeanColumn> columnSelection, List<LeanSortMethod> columnSorts, boolean distinctSelection,
                             LeanComponent groupComponent, int verticalMargin ) {
    this();
    this.sourceConnectorName = connectorName;
    this.columnSelection = columnSelection;
    this.columnSorts = columnSorts;
    this.distinctSelection = distinctSelection;
    this.groupComponent = groupComponent;
    this.verticalMargin = verticalMargin;
  }

  public LeanGroupComponent( LeanGroupComponent c ) {
    super( "LeanGroupComponent", c );
    this.sourceConnectorName = c.sourceConnectorName;
    this.columnSelection = new ArrayList<>(  );
    for (LeanColumn column : c.columnSelection) {
      this.columnSelection.add(new LeanColumn( column ));
    }
    this.columnSorts = new ArrayList<>(  );
    for (LeanSortMethod m : c.columnSorts) {
      this.columnSorts.add(new LeanSortMethod( m ));
    }
    this.distinctSelection = c.distinctSelection;
    this.groupComponent = c.groupComponent == null ? null : new LeanComponent( c.groupComponent );
    this.verticalMargin = c.verticalMargin;
  }

  public LeanGroupComponent clone() {
    return new LeanGroupComponent(this);
  }


  /**
   * This is the first thing that happens: figure out over what values we need to group over.
   * <p>
   * Connector name, column selection and column sort describes the values over which we need to group
   * We optionally calculate distinct values for the rows.
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param renderContext
   * @param results
   * @throws LeanException
   */
  @Override
  public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                 LeanLayoutResults results ) throws LeanException {

    GroupDetails details = new GroupDetails();

    LeanConnector connector = dataContext.getConnector( sourceConnectorName );
    if ( connector == null ) {
      throw new LeanException( "Unable to find connector '" + sourceConnectorName + "'" );
    }

    List<ILeanConnector> connectors = new ArrayList<>();

    // Select the columns from the data source
    // Sort the columns
    // Get distinct values (optional)
    //
    connectors.add( new LeanSelectionConnector( columnSelection ) );
    if ( !columnSorts.isEmpty() ) {
      connectors.add( new LeanSortConnector( columnSelection, columnSorts ) );
    }
    if ( distinctSelection ) {
      connectors.add( new LeanDistinctConnector() );
    }

    // Chain the operations
    //
    LeanChainConnector chain = new LeanChainConnector( sourceConnectorName, connectors );
    ChainDataContext chainContext = chain.createChainContext( dataContext );
    LeanConnector lastConnector = chainContext.getLastConnector();

    // Get the rows from source and selected, sorted, distinct
    //
    synchronized ( lastConnector.getConnector() ) {
      details.rows = lastConnector.retrieveRows( chainContext );
    }

    // Calculate total size, do call to processRowData of child
    //
    LeanSize size = new LeanSize( 0, 0 );

    // The last component to layout below
    // Null means: first component, keep layout of this component
    //
    LeanComponent lastComponent = null;

    for ( int rowNr=0;rowNr<details.rows.size();rowNr++) {

      RowMetaAndData groupRow = details.rows.get( rowNr );
      GroupRowDetails rowDetails = new GroupRowDetails();
      details.rowDetails.add(rowDetails);

      // Make a copy of the current component to store separately in the
      // component geometry map in results.
      //
      String rowComponentName = component.getName() + "-group#" + ( rowNr + 1 )+":"+groupComponent.getName();

      // Create a new component to render
      //
      LeanComponent rowComponent = new LeanComponent( groupComponent );
      rowComponent.setName( rowComponentName );
      ILeanComponent groupIComponent = rowComponent.getComponent(); // also copied

      // Copy layout from parent
      //
      rowComponent.setLayout( new LeanLayout( component.getLayout() ) );

      // Adjust layout: position from parent to previous row component
      //
      if (lastComponent!=null) {
        // This component needs to position below the previous one.
        //
        rowComponent.getLayout().setTop(new LeanAttachment( lastComponent.getName(), 0, 0, LeanAttachment.Alignment.BOTTOM ) );
      }

      // Create a new data context which will filter the data sources...
      //
      IDataContext groupRowDataContext = new GroupDataContext( dataContext, groupRow );

      // Read the data for the component (Table, Crosstab, Image, ...)
      // This is stored in groupResults
      //
      groupIComponent.processSourceData( presentation, page, rowComponent, groupRowDataContext, renderContext, results );

      // Calculate the expected size.
      // This pre-calculates all sorts of things about the component (table & crosstab cells, heights, widths, ...)
      //
      LeanSize groupRowExpectedSize = groupIComponent.getExpectedSize( presentation, page, rowComponent, groupRowDataContext, renderContext, results );

      // Add the expected size to the total size
      //
      // For the width we need the maximum
      //
      if ( size.getWidth() < groupRowExpectedSize.getWidth() ) {
        size.setWidth( groupRowExpectedSize.getWidth() );
      }

      // For the height we add up everything...
      //
      size.setHeight( size.getHeight() + groupRowExpectedSize.getHeight() + verticalMargin );

      // Save all these learned facts in the details.
      //
      rowDetails.groupRowDataContext = groupRowDataContext;
      rowDetails.groupExpectedRowSize = groupRowExpectedSize;
      rowDetails.groupRowComponent = rowComponent;

      lastComponent = rowComponent;

    }
    details.size = size;

    // Cache it
    //
    results.addDataSet( component, DATA_GROUP_DETAILS, details );
  }

  @Override
  public LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results ) throws LeanException {

    if ( component.isDynamic() ) {
      GroupDetails details = (GroupDetails) results.getDataSet( component, DATA_GROUP_DETAILS );
      return details.size;
    } else {
      return component.getSize();
    }
  }

  @Override
  public void doLayout( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results ) throws LeanException {

    // This stores results in the details, including the total size
    //
    LeanGeometry geometry = getExpectedGeometry( presentation, page, component, dataContext, renderContext, results );

    // Get these results back
    //
    GroupDetails details = (GroupDetails) results.getDataSet( component, DATA_GROUP_DETAILS );

    // Call doLayout for every group row
    //
    for ( int rowNr = 0; rowNr<details.rowDetails.size(); rowNr++) {
      GroupRowDetails groupRowDetails = details.rowDetails.get(rowNr);
      LeanComponent rowComponent = groupRowDetails.groupRowComponent;
      ILeanComponent groupIComponent = rowComponent.getComponent();
      groupIComponent.doLayout( presentation, page, rowComponent, groupRowDetails.groupRowDataContext, renderContext, results );

      // Lookup the geometry of the last row component. This is the geometry of the last part every time
      // We'll use that as the geometry of the group as a whole (the last part of the group on the last page)
      //
      LeanGeometry rowComponentGeometry = results.findGeometry( rowComponent.getName() );

      // Make the geometry higher to the tune of the vertical margin
      //
      rowComponentGeometry.incHeight(verticalMargin);

      // Now store this under the name of the group
      // It will allow other components to position against this
      //
      results.addComponentGeometry( component.getName(), rowComponentGeometry );
    }
  }


  @Override
  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {

    LeanComponent component = layoutResult.getComponent();
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    GroupDetails details = (GroupDetails) results.getDataSet( component, DATA_GROUP_DETAILS );

    // We're not rendering anything here, we let the copies of our group component do that.
    // Start drawing the list of component copies ...
    //
    // Here's where we start on the current page...
    //
    int x = componentGeometry.getX();
    int y = componentGeometry.getY();

    // Loop over the group row details
    //
    for ( GroupRowDetails groupRowDetails : details.rowDetails ) {
      LeanSize groupRowSize = groupRowDetails.groupExpectedRowSize;

      // The Layout Results we need to re-create for the group component...
      //
      // LeanComponentLayoutResult groupLayoutResult = new LeanComponentLayoutResult(layoutResult);
      // groupLayoutResult.setComponent( groupComponent );
      layoutResult.setComponent( groupComponent );
      layoutResult.getGeometry().setX( x );
      layoutResult.getGeometry().setY( y );
      layoutResult.getGeometry().setWidth( details.size.getWidth() );
      layoutResult.getGeometry().setHeight( groupRowSize.getHeight() );
      y += groupRowSize.getHeight() + verticalMargin;

      // Render the group component on the parent
      //
      groupComponent.getComponent().render( layoutResult, results, renderContext );
    }


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
   * Gets columnSorts
   *
   * @return value of columnSorts
   */
  public List<LeanSortMethod> getColumnSorts() {
    return columnSorts;
  }

  /**
   * @param columnSorts The columnSorts to set
   */
  public void setColumnSorts( List<LeanSortMethod> columnSorts ) {
    this.columnSorts = columnSorts;
  }

  /**
   * Gets distinctSelection
   *
   * @return value of distinctSelection
   */
  public boolean isDistinctSelection() {
    return distinctSelection;
  }

  /**
   * @param distinctSelection The distinctSelection to set
   */
  public void setDistinctSelection( boolean distinctSelection ) {
    this.distinctSelection = distinctSelection;
  }

  /**
   * Gets groupComponent
   *
   * @return value of groupComponent
   */
  public LeanComponent getGroupComponent() {
    return groupComponent;
  }

  /**
   * @param groupComponent The groupComponent to set
   */
  public void setGroupComponent( LeanComponent groupComponent ) {
    this.groupComponent = groupComponent;
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
}
