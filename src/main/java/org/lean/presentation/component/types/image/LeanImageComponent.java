package org.lean.presentation.component.types.image;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
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
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URL;

@JsonDeserialize( as = LeanImageComponent.class )
public class LeanImageComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_IMAGE_DETAILS = "Image Details";

  @HopMetadataProperty
  private String filename;

  @HopMetadataProperty
  private String scalePercent;

  public LeanImageComponent() {
    super( "LeanSvgComponent" );
  }

  public LeanImageComponent( String filename ) {
    this();
    this.filename = filename;
  }

  public LeanImageComponent( LeanImageComponent c ) {
    super( "LeanSvgComponent", c );
    this.filename = c.filename;
    this.scalePercent = c.scalePercent;
  }

  public LeanImageComponent clone() {
    return new LeanImageComponent( this );
  }

  @Override public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext,
                                           LeanLayoutResults results ) throws LeanException {

    if ( StringUtils.isEmpty( filename ) ) {
      throw new LeanException( "No image file specified" );
    }

    ImageDetails details = new ImageDetails();

    // Get the width and height of the image
    //
    try {
      URL resource = this.getClass().getClassLoader().getResource( filename );
      details.image = ImageIO.read( resource.openStream() );
    } catch ( IOException e ) {
      throw new LeanException( "Unable to load image file '" + filename + "'", e );
    }

    if ( details.image == null ) {
      // Probably unsupported image type
      //
      throw new LeanException( "Unable to load file '" + filename + "' (Unsupported type?)" );
    }

    details.scaleFactor = (double) Const.toDouble( scalePercent, 100.0 ) / 100;

    details.originalSize = new LeanSize( details.image.getWidth(), details.image.getHeight() );
    details.imageSize = new LeanSize( (int) ( details.image.getWidth() * details.scaleFactor ), (int) ( details.image.getHeight() * details.scaleFactor ) );

    // Don't calculate this twice...
    //
    results.addDataSet( component, DATA_IMAGE_DETAILS, details );
  }

  public LeanSize getExpectedSize( LeanPresentation leanPresentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    ImageDetails details = (ImageDetails) results.getDataSet( component, DATA_IMAGE_DETAILS );
    return details.imageSize;
  }

  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {

    LeanGeometry componentGeometry = layoutResult.getGeometry();
    LeanComponent component = layoutResult.getComponent();

    SVGGraphics2D gc = layoutResult.getRenderPage().getGc();

    // Draw background for the full imageSize of the component area
    //
    setBackgroundBorderFont( gc, componentGeometry, renderContext );

    // Remember the details
    //
    ImageDetails details = (ImageDetails) results.getDataSet( component, DATA_IMAGE_DETAILS );

    // This allow us to make the image smaller or larger
    //
    AffineTransform oldTransform = gc.getTransform();
    gc.scale( details.scaleFactor, details.scaleFactor );

    // Don't scale the location...
    int x = (int) ( componentGeometry.getX() / details.scaleFactor );
    int y = (int) ( componentGeometry.getY() / details.scaleFactor );
    gc.drawImage( details.image, x, y, null );

    // Set the drawing scale back to normal
    //
    gc.setTransform( oldTransform );

    if ( isBorder() ) {
      enableColor( gc, lookupBorderColor( renderContext ) );
      gc.drawRect( componentGeometry.getX(), componentGeometry.getY(), details.imageSize.getWidth(), details.imageSize.getHeight() );
    }
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
