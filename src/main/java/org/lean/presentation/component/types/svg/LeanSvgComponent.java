package org.lean.presentation.component.types.svg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.core.svg.SvgCache;
import org.apache.hop.core.svg.SvgCacheEntry;
import org.apache.hop.core.svg.SvgFile;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;
import org.w3c.dom.Node;

@JsonDeserialize( as = LeanSvgComponent.class )
@LeanComponentPlugin(
  id= "LeanSvgComponent",
  name="SVG",
  description = "An SVG component"
)
public class LeanSvgComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_SVG_DETAILS = "SVG Details";

  @HopMetadataProperty
  private String filename;

  @HopMetadataProperty
  private String scalePercent;

  public LeanSvgComponent() {
    super( "LeanSvgComponent" );
  }

  public LeanSvgComponent( String filename ) {
    this();
    this.filename = filename;
  }

  public LeanSvgComponent( LeanSvgComponent c ) {
    super( "LeanSvgComponent", c );
    this.filename = c.filename;
    this.scalePercent = c.scalePercent;
  }

  public LeanSvgComponent clone() {
    return new LeanSvgComponent( this );
  }

  @Override public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                           LeanLayoutResults results ) throws LeanException {

    if ( StringUtils.isEmpty( filename ) ) {
      throw new LeanException( "No image file specified" );
    }

    IVariables variables = dataContext.getVariableSpace();

    SvgDetails details = new SvgDetails();

    // The real filename after variable substitution?
    //
    String realFilename = variables.resolve( filename );

    // Load the SVG XML document
    //
    try {
      SvgCacheEntry svgCacheEntry = SvgCache.loadSvg( new SvgFile( realFilename, getClass().getClassLoader() ) );
      details.originalSize = new LeanSize((int)svgCacheEntry.getWidth(), (int)svgCacheEntry.getHeight());
      details.svgDocument = svgCacheEntry.getSvgDocument();
    } catch ( Exception e ) {
      throw new LeanException( "Unable to load SVG file '" + realFilename + "'", e );
    }

    details.scaleFactor = Const.toDouble( variables.resolve(scalePercent), 100.0 ) / 100;

    details.imageSize = new LeanSize(
      (int) ( details.originalSize.getWidth() * details.scaleFactor ),
      (int) ( details.originalSize.getHeight() * details.scaleFactor )
    );

    // Don't calculate this twice...
    //
    results.addDataSet( component, DATA_SVG_DETAILS, details );
  }

  public LeanSize getExpectedSize( LeanPresentation leanPresentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {

    SvgDetails details = (SvgDetails) results.getDataSet( component, DATA_SVG_DETAILS );
    return details.imageSize;
  }

  @Override
  public LeanGeometry getExpectedGeometry( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    return super.getExpectedGeometry( presentation, page, component, dataContext, renderContext, results );
  }

  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext, LeanPosition offSet ) throws LeanException {

    LeanRenderPage renderPage = layoutResult.getRenderPage();
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    LeanComponent component = layoutResult.getComponent();

    HopSvgGraphics2D gc = layoutResult.getRenderPage().getGc();

    // Draw background for the full imageSize of the component area
    //
    setBackgroundBorderFont( gc, componentGeometry, renderContext );

    // Remember the details
    //
    SvgDetails details = (SvgDetails) results.getDataSet( component, DATA_SVG_DETAILS );
    Node imageSvgNode = details.svgDocument.getRootElement();

    // Embed the SVG into the presentation
    //
    gc.embedSvg(
      imageSvgNode,
      filename,
      componentGeometry.getX(),
      componentGeometry.getY(),
      details.imageSize.getWidth(),
      details.imageSize.getHeight(),
      (float) details.scaleFactor,
      (float) details.scaleFactor,
      0d
    );

    if ( isBorder() ) {
      enableColor( gc, lookupBorderColor( renderContext ) );
      gc.drawRect( componentGeometry.getX(), componentGeometry.getY(), details.imageSize.getWidth(), details.imageSize.getHeight() );
    }

    // addd drawnItem for this
    //
    // renderPage.addComponentDrawnItem( component, componentGeometry );
  }


  /**
   * Gets filename
   *
   * @return value of filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename The filename to set
   */
  public void setFilename( String filename ) {
    this.filename = filename;
  }

  /**
   * Gets scalePercent
   *
   * @return value of scalePercent
   */
  public String getScalePercent() {
    return scalePercent;
  }

  /**
   * @param scalePercent The scalePercent to set
   */
  public void setScalePercent( String scalePercent ) {
    this.scalePercent = scalePercent;
  }
}
