package org.lean.presentation.component.types.svg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.batik.svggen.DOMGroupManager.DRAW;

@JsonDeserialize( as = LeanSvgComponent.class )
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

    SvgDetails details = new SvgDetails();

    // Load the SVG XML document
    //
    try {
      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory( parser );
      try {
        InputStream svgStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( filename );

        if ( svgStream == null ) {
          throw new LeanException( "Unable to find file '" + filename + "'" );
        }
        details.svgDocument = factory.createSVGDocument( filename, svgStream );
      } catch ( IOException e ) {
        System.out.println( e.getMessage() );
      }
    } catch ( Exception e ) {
      throw new LeanException( "Unable to load SVG file '" + filename + "'", e );
    }

    Element elSVG = details.svgDocument.getRootElement();
    String widthString = elSVG.getAttribute( "width" );
    String heightString = elSVG.getAttribute( "height" );
    int width = Const.toInt( widthString.replace( "px", "" ), -1 );
    int height = Const.toInt( heightString.replace( "px", "" ), -1 );
    if ( width < 0 || height < 0 ) {
      throw new LeanException( "Unable to find valid width or height in SVG document " + filename );
    }

    details.scaleFactor = (double) Const.toDouble( scalePercent, 100.0 ) / 100;

    details.originalSize = new LeanSize( width, height );
    details.imageSize = new LeanSize( (int) ( width * details.scaleFactor ), (int) ( height * details.scaleFactor ) );

    // Don't calculate this twice...
    //
    results.addDataSet( component, DATA_SVG_DETAILS, details );
  }

  public LeanSize getExpectedSize( LeanPresentation leanPresentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    SvgDetails details = (SvgDetails) results.getDataSet( component, DATA_SVG_DETAILS );
    return details.imageSize;
  }

  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {

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
      (float)details.scaleFactor,
      (float)details.scaleFactor,
      0d
    );

    if ( isBorder() ) {
      enableColor( gc, lookupBorderColor( renderContext ) );
      gc.drawRect( componentGeometry.getX(), componentGeometry.getY(), details.imageSize.getWidth(), details.imageSize.getHeight() );
    }

    // addd drawnItem for this
    //
    renderPage.addComponentDrawnItem( component, componentGeometry );
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
