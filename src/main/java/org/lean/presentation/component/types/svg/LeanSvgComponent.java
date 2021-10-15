package org.lean.presentation.component.types.svg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang.StringUtils;
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
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;
import org.w3c.dom.Node;

@JsonDeserialize(as = LeanSvgComponent.class)
@LeanComponentPlugin(id = "LeanSvgComponent", name = "SVG", description = "An SVG component")
public class LeanSvgComponent extends LeanBaseComponent implements ILeanComponent {

  public static final String DATA_SVG_DETAILS = "SVG Details";

  @HopMetadataProperty private String filename;

  @HopMetadataProperty private ScaleType scaleType;

  public LeanSvgComponent() {
    super("LeanSvgComponent");
    scaleType = ScaleType.MIN;
  }

  public LeanSvgComponent(String filename, ScaleType scaleType) {
    this();
    this.filename = filename;
    this.scaleType = scaleType;
  }

  public LeanSvgComponent(LeanSvgComponent c) {
    super("LeanSvgComponent", c);
    this.filename = c.filename;
    this.scaleType = c.scaleType;
  }

  public LeanSvgComponent clone() {
    return new LeanSvgComponent(this);
  }

  @Override
  public void processSourceData(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {

    if (StringUtils.isEmpty(filename)) {
      throw new LeanException("No image file specified");
    }

    IVariables variables = dataContext.getVariables();

    SvgDetails details = new SvgDetails();

    // The real filename after variable substitution?
    //
    String realFilename = variables.resolve(filename);

    // Load the SVG XML document
    //
    try {
      SvgCacheEntry svgCacheEntry =
          SvgCache.loadSvg(new SvgFile(realFilename, getClass().getClassLoader()));
      details.imageGeometry =
          new LeanGeometry(
              svgCacheEntry.getX(),
              svgCacheEntry.getY(),
              (int) svgCacheEntry.getWidth(),
              (int) svgCacheEntry.getHeight());
      details.svgDocument = svgCacheEntry.getSvgDocument();
    } catch (Exception e) {
      throw new LeanException("Unable to load SVG file '" + realFilename + "'", e);
    }

    // Don't calculate this twice...
    //
    results.addDataSet(component, DATA_SVG_DETAILS, details);
  }

  @Override
  public LeanSize getExpectedSize(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {
    SvgDetails details = (SvgDetails) results.getDataSet(component, DATA_SVG_DETAILS);
    return new LeanSize(details.imageGeometry.getWidth(), details.imageGeometry.getHeight());
  }

  @Override
  public LeanGeometry getExpectedGeometry(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {

    SvgDetails details = (SvgDetails) results.getDataSet(component, DATA_SVG_DETAILS);

    // Calculate the boundaries of this image based on the layout
    //
    LeanGeometry geometry =
        super.getExpectedGeometry(
            presentation, page, component, dataContext, renderContext, results);

    // See if we need to scale the SVG to fit the target...
    //
    // Zoom in or out to make the image fit onto the parent page (it's the best use-case for now)
    //
    float xMagnification = (float) geometry.getWidth() / (float) details.imageGeometry.getWidth();
    float yMagnification = (float) geometry.getHeight() / (float) details.imageGeometry.getHeight();

    // Based on the scale type we calculate the magnifications...
    //
    switch (scaleType) {
      case NONE:
        xMagnification = 1.0f;
        yMagnification = 1.0f;
        break;
      case FILL:
        break;
      case FILL_HORIZONTAL:
        yMagnification = 1.0f;
        break;
      case FILL_VERTICAL:
        xMagnification = 1.0f;
        break;
      case MIN:
        float magnification = Math.min(xMagnification, yMagnification);
        xMagnification = magnification;
        yMagnification = magnification;
        break;
      case MAX:
        magnification = Math.max(xMagnification, yMagnification);
        xMagnification = magnification;
        yMagnification = magnification;
        break;
    }

    details.xMagnification = xMagnification;
    details.yMagnification = yMagnification;

    int width = Math.round(xMagnification * details.imageGeometry.getWidth());
    int xDifference = geometry.getWidth() - width;

    int height = Math.round(yMagnification * details.imageGeometry.getHeight());
    int yDifference = geometry.getHeight() - height;

    LeanLayout layout = component.getLayout();

    geometry.setWidth(width);
    geometry.setHeight(height);
    if (layout.hasRight()) {
      geometry.incX(xDifference);
    }
    if (layout.hasBottom()) {
      geometry.incY(yDifference);
    }

    // TODO: fix issue with calculating centered boundaries when magnification is involved

    // Update the stored geometry to make sure...
    //
    results.addComponentGeometry(component.getName(), geometry);

    return geometry;
  }

  @Override
  public void doLayout(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {
    super.doLayout(presentation, page, component, dataContext, renderContext, results);
  }

  public void render(
      LeanComponentLayoutResult layoutResult,
      LeanLayoutResults results,
      IRenderContext renderContext,
      LeanPosition offSet)
      throws LeanException {

    LeanRenderPage renderPage = layoutResult.getRenderPage();
    LeanGeometry componentGeometry = layoutResult.getGeometry();
    LeanComponent component = layoutResult.getComponent();

    HopSvgGraphics2D gc = renderPage.getGc();

    // Draw background for the full imageSize of the component area
    //
    setBackgroundBorderFont(gc, componentGeometry, renderContext);

    // Remember the details
    //
    SvgDetails details = (SvgDetails) results.getDataSet(component, DATA_SVG_DETAILS);
    Node imageSvgNode = details.svgDocument.getRootElement();

    // Embed the SVG into the presentation
    //
    gc.embedSvg(
        imageSvgNode,
        filename,
        componentGeometry.getX(),
        componentGeometry.getY(),
        details.imageGeometry.getWidth(),
        details.imageGeometry.getHeight(),
        details.xMagnification,
        details.yMagnification,
        0d);
  }

  /**
   * Gets filename
   *
   * @return value of filename
   */
  public String getFilename() {
    return filename;
  }

  /** @param filename The filename to set */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * Gets scaleType
   *
   * @return value of scaleType
   */
  public ScaleType getScaleType() {
    return scaleType;
  }

  /** @param scaleType The scaleType to set */
  public void setScaleType(ScaleType scaleType) {
    this.scaleType = scaleType;
  }
}
