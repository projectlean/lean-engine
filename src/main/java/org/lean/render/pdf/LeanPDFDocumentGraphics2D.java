package org.lean.render.pdf;

import org.apache.fop.svg.PDFDocumentGraphics2D;

import java.io.IOException;

public class LeanPDFDocumentGraphics2D extends PDFDocumentGraphics2D {
  public LeanPDFDocumentGraphics2D( boolean textAsShapes ) {
    super( textAsShapes );
  }

  public void preparePainting() {
    super.preparePainting();
  }

  public void closePage() {
    super.closePage();
  }

  public void startPage() throws IOException {
    super.startPage();
  }

}
