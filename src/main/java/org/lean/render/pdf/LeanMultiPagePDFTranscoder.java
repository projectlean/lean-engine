package org.lean.render.pdf;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.fop.Version;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.svg.AbstractFOPTranscoder;
import org.apache.fop.svg.PDFBridgeContext;
import org.apache.fop.svg.PDFDocumentGraphics2DConfigurator;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.fop.svg.font.FOPFontFamilyResolverImpl;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGLength;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LeanMultiPagePDFTranscoder extends AbstractFOPTranscoder {

  /** Graphics2D instance that is used to paint to */
  protected LeanPDFDocumentGraphics2D graphics;

  /**
   * Constructs a new {@link PDFTranscoder}.
   */
  public LeanMultiPagePDFTranscoder() {
    super();
    this.handler = new LeanFOPErrorHandler();
  }

  /**
   * {@inheritDoc}
   */
  protected FOPTranscoderUserAgent createUserAgent() {
    return new AbstractFOPTranscoder.FOPTranscoderUserAgent() {
      // The PDF stuff wants everything at 72dpi
      public float getPixelUnitToMillimeter() {
        return super.getPixelUnitToMillimeter();
        //return 25.4f / 72; //72dpi = 0.352778f;
      }
    };
  }

  public void nextPage(int width, int height) throws IOException {
    graphics.nextPage(width, height);
    graphics.startPage();
  }

  public void finish() throws IOException {
    this.graphics.finish();
  }

  /**
   * Transcodes the specified Document as an image in the specified output.
   *
   * @param document the document to transcode
   * @param uri the uri of the document or null if any
   * @param output the ouput where to transcode
   * @exception TranscoderException if an error occured while transcoding
   */
  protected void transcode(Document document, String uri,
                           TranscoderOutput output)
    throws TranscoderException {

    graphics = new LeanPDFDocumentGraphics2D(isTextStroked());
    graphics.getPDFDocument().getInfo().setProducer("Apache FOP Version "
      + Version.getVersion()
      + ": PDF Transcoder for Batik");
    if (hints.containsKey(KEY_DEVICE_RESOLUTION)) {
      graphics.setDeviceDPI(getDeviceResolution());
    }

    setupImageInfrastructure(uri);

    try {
      Configuration effCfg = getEffectiveConfiguration();

      if (effCfg != null) {
        PDFDocumentGraphics2DConfigurator configurator
          = new PDFDocumentGraphics2DConfigurator();
        boolean useComplexScriptFeatures = false; //TODO - FIX ME
        configurator.configure(graphics, effCfg, useComplexScriptFeatures);
      } else {
        graphics.setupDefaultFontInfo();
      }
      ((FOPTranscoderUserAgent) userAgent).setFontFamilyResolver(
        new FOPFontFamilyResolverImpl(graphics.getFontInfo()));
    } catch (Exception e) {
      throw new TranscoderException(
        "Error while setting up PDFDocumentGraphics2D", e);
    }

    super.transcode(document, uri, output);

    if (getLogger().isTraceEnabled()) {
      getLogger().trace("document size: " + width + " x " + height);
    }

    // prepare the image to be painted
    org.apache.batik.bridge.UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx,
      document.getDocumentElement());
    float widthInPt = org.apache.batik.bridge.UnitProcessor.userSpaceToSVG(width, SVGLength.SVG_LENGTHTYPE_PT,
      org.apache.batik.bridge.UnitProcessor.HORIZONTAL_LENGTH, uctx);
    int w = (int)(widthInPt + 0.5);
    float heightInPt = org.apache.batik.bridge.UnitProcessor.userSpaceToSVG(height, SVGLength.SVG_LENGTHTYPE_PT,
      UnitProcessor.HORIZONTAL_LENGTH, uctx);
    int h = (int)(heightInPt + 0.5);
    if (getLogger().isTraceEnabled()) {
      getLogger().trace("document size: " + w + "pt x " + h + "pt");
    }

    // prepare the image to be painted
    //int w = (int)(width + 0.5);
    //int h = (int)(height + 0.5);

    try {
      OutputStream out = output.getOutputStream();
      if (!(out instanceof BufferedOutputStream)) {
        out = new BufferedOutputStream(out);
      }
      graphics.setupDocument(out, w, h);
      graphics.setSVGDimension(width, height);

      if (hints.containsKey(ImageTranscoder.KEY_BACKGROUND_COLOR)) {
        graphics.setBackgroundColor(
          (Color)hints.get(ImageTranscoder.KEY_BACKGROUND_COLOR));
      }
      graphics.setGraphicContext(
        new org.apache.xmlgraphics.java2d.GraphicContext());
      graphics.preparePainting();

      graphics.transform(curTxf);
      graphics.setRenderingHint(
        RenderingHintsKeyExt.KEY_TRANSCODING,
        RenderingHintsKeyExt.VALUE_TRANSCODING_VECTOR);

      this.root.paint(graphics);

      // graphics.finish();
    } catch (IOException ex) {
      throw new TranscoderException(ex);
    }
  }
  /** {@inheritDoc} */
  protected BridgeContext createBridgeContext() {
    //For compatibility with Batik 1.6
    return createBridgeContext("1.x");
  }

  /** {@inheritDoc} */
  public BridgeContext createBridgeContext(String version) {
    FontInfo fontInfo = graphics.getFontInfo();
    if (isTextStroked()) {
      fontInfo = null;
    }
    BridgeContext ctx = new PDFBridgeContext(userAgent, fontInfo,
      getImageManager(), getImageSessionContext());
    return ctx;
  }

}
