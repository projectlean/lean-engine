package org.lean.presentation.component.types.composite;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a composite component which groups a bunch of composites
 * The size is the maximum reach of all the components in the composite
 * The composite will have its own data context
 * <p>
 * First we get the rows for all the components in the group:
 * <p>
 *
 * @see ILeanComponent#processSourceData(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Then we calculate the expected size of the composite.
 * Obviously, this size is dynamic so it's hard to know unless we calculate all the sizes of the components given the data context of the composite.
 * <p>
 * @see LeanCompositeComponent#getExpectedSize(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Now we render all the components in the composite
 * <p>
 * @see ILeanComponent#doLayout(LeanPresentation, LeanPage, LeanComponent, IDataContext, IRenderContext, LeanLayoutResults)
 * <p>
 * Finally, have all the child composites render themselves
 * <p>
 * @see ILeanComponent#render(LeanComponentLayoutResult, LeanLayoutResults, IRenderContext)
 */
@JsonDeserialize( as = LeanCompositeComponent.class )
@LeanComponentPlugin(
  id="LeanCompositeComponent",
  name="Composite",
  description="In this component you can place other components"
)
public class LeanCompositeComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_COMPOSITE_DETAILS = "DATA_COMPOSITE_DETAILS";

  @HopMetadataProperty
  private List<LeanComponent> children;

  public LeanCompositeComponent() {
    super( "LeanCompositeComponent" );
    children = new ArrayList<>();
  }

  public LeanCompositeComponent( List<LeanComponent> children ) {
    this();
    this.children = children;
  }

  public LeanCompositeComponent( LeanCompositeComponent c ) {
    super( "LeanCompositeComponent", c );
    this.children = new ArrayList<>();
    for ( LeanComponent child : c.children ) {
      this.children.add( new LeanComponent( child ) );
    }
  }

  public LeanCompositeComponent clone() {
    return new LeanCompositeComponent( this );
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

    CompositeDetails details = new CompositeDetails();

    // Calculate total size, do call to processRowData of children
    //
    LeanSize size = new LeanSize( 0, 0 );

    for ( LeanComponent child : children ) {

      ChildDetails childDetails = new ChildDetails();
      details.childDetails.add( childDetails );

      ILeanComponent childIComponent = child.getComponent();

      // Make a copy of the child component to make sure we don't mess up the original metadata when we calculate
      // relative position versus the composite borders.
      // With this new unique name it's safe to use the composite Layout results objects
      //
      String childComponentName = component.getName() + "-child(" + child.getName() + ")";

      // Create a new component to render
      //
      LeanComponent childComponent = new LeanComponent( child );
      childComponent.setName( childComponentName );
      childDetails.childComponent = childComponent;

      // Top references to page means: references to top of composite
      // Bottom references to page means: references to bottom composite which isn't know until after rendering:
      //   We consider this to be nonsensical right now and throw an error
      // Left references to page means: references to left of composite
      // Right references to page (null) means: right of the page
      //

      // Copy layout from parent
      //
      LeanLayout childLayout = new LeanLayout( child.getLayout() );
      childComponent.setLayout( childLayout );

      // Adjust layout: position from page (null) to parent row component
      //
      if ( childLayout.getBottom() != null ) {
        throw new LeanException( "The bottom of a composite can't be referenced since its size is dynamic and unknown upfront." );
      }
      LeanLayout componentLayout = component.getLayout();

      // Is the child referencing the top of the page?
      // Reference the top of the composite instead
      //
      LeanAttachment componentTop = componentLayout.getTop();
      if ( componentTop != null ) {
        LeanAttachment childTop = childLayout.getTop();
        if ( childTop != null && childTop.getComponentName() == null ) {
          childLayout.setTop( new LeanAttachment( componentTop.getComponentName(), childTop.getPercentage(), childTop.getOffset(), componentTop.getAlignment() ) );
        }
      }
      // Is the child referencing the left of the page?
      // Reference the left side of the composite instead
      //
      LeanAttachment componentLeft = componentLayout.getLeft();
      if ( componentLeft != null ) {
        LeanAttachment childLeft = childLayout.getLeft();
        if ( childLeft != null && childLeft.getComponentName() == null ) {
          childLayout.setLeft( new LeanAttachment( componentLeft.getComponentName(), childLeft.getPercentage(), childLeft.getOffset(), componentLeft.getAlignment() ) );
        }
      }
      // Is the child referencing the right of the page?
      // Reference the right side of the composite instead
      //
      LeanAttachment componentRight = componentLayout.getRight();
      if ( componentRight != null ) {
        LeanAttachment childRight = childLayout.getRight();
        if ( childRight != null && childRight.getComponentName() == null ) {
          childLayout.setRight( new LeanAttachment( componentRight.getComponentName(), childRight.getPercentage(), childRight.getOffset(), componentRight.getAlignment() ) );
        }
      }

      // Read the data for the component (Table, Crosstab, Image, ...)
      // This is stored in childLayoutResults
      //
      childIComponent.processSourceData( presentation, page, childComponent, dataContext, renderContext, results );

      // Calculate the expected size.
      // This pre-calculates all sorts of things about the component (table & crosstab cells, heights, widths, ...)
      //
      LeanSize childExpectedSize = childIComponent.getExpectedSize( presentation, page, childComponent, dataContext, renderContext, results );

      // Save all these learned size in the details (mostly for debugging).
      //
      childDetails.childExpectedSize = childExpectedSize;

    }

    // We can't simply change the name.  Make sure all references in the other children are updated!
    //
    for ( int x = 0; x < children.size(); x++ ) {
      String oldName = children.get( x ).getName();
      String newName = details.childDetails.get( x ).childComponent.getName();

      for ( int i = 0; i < details.childDetails.size(); i++ ) {
        LeanComponent childCopy = details.childDetails.get( i ).childComponent;
        childCopy.getLayout().replaceReferences( oldName, newName );
      }
    }

    // We can't calculate the size until we layout the composite children
    //
    details.size = new LeanSize( LeanSize.UNKNOWN_SIZE );

    // Cache it
    //
    results.addDataSet( component, DATA_COMPOSITE_DETAILS, details );
  }

  @Override
  public LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {

    // Unless we have a fixed size, we can't know the size until after layout
    //
    if ( component.isDynamic() ) {
      return new LeanSize( LeanSize.UNKNOWN_SIZE );
    } else {
      return component.getSize();
    }
  }

  @Override
  public void doLayout( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {

    // Get these results back
    //
    CompositeDetails details = (CompositeDetails) results.getDataSet( component, DATA_COMPOSITE_DETAILS );

    LeanGeometry compositeGeometry = new LeanGeometry( 0, 0, 0, 0 );

    int previousPageNr = results.getCurrentRenderPage( page ).getPageNumber();

    // Here we can simply do the layout of every child
    //
    for ( int i = 0; i < details.childDetails.size(); i++ ) {
      ChildDetails childDetails = details.childDetails.get( i );

      LeanComponent childComponent = childDetails.childComponent;
      ILeanComponent childIComponent = childComponent.getComponent();
      childIComponent.doLayout( presentation, page, childComponent, dataContext, renderContext, results );

      // If we passed onto a new page, we need to keep the lowest on that page
      // So we start again at the top...
      //
      int pageNr = results.getCurrentRenderPage( page ).getPageNumber();
      if ( pageNr != previousPageNr ) {
        compositeGeometry = new LeanGeometry( 0, 0, 0, 0 );
      }

      // Grab the geometry from the results, we need it to calculate the total surface of the composite.
      //
      LeanGeometry childGeometry = results.findGeometry( childComponent.getName() );

      // Compute the lowest surface area of this composite
      //
      compositeGeometry.lowest( childGeometry );

      previousPageNr = pageNr;
    }

    // Save the total composite geometry also in the results
    //
    results.addComponentGeometry( component.getName(), compositeGeometry );
    details.size = new LeanSize( compositeGeometry.getWidth(), compositeGeometry.getHeight() );
  }


  @Override
  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {

    LeanComponent component = layoutResult.getComponent();
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    CompositeDetails details = (CompositeDetails) results.getDataSet( component, DATA_COMPOSITE_DETAILS );

    for ( int i = 0; i < children.size(); i++ ) {
      LeanComponent child = children.get( i );
      ILeanComponent childIComponent = child.getComponent();
      ChildDetails childDetails = details.childDetails.get( i );

      LeanGeometry childGeometry = results.findGeometry( childDetails.childComponent.getName() );

      LeanComponentLayoutResult childComponentLayoutResult = new LeanComponentLayoutResult( layoutResult );
      childComponentLayoutResult.setComponent( child );
      childComponentLayoutResult.setGeometry( childGeometry );

      childIComponent.render( childComponentLayoutResult, results, renderContext );
    }
  }


  /**
   * Gets children
   *
   * @return value of children
   */
  public List<LeanComponent> getChildren() {
    return children;
  }

  /**
   * @param children The children to set
   */
  public void setChildren( List<LeanComponent> children ) {
    this.children = children;
  }
}
