package org.lean.presentation.component.types.svg;

import org.lean.core.LeanSize;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import java.awt.image.BufferedImage;

public class SvgDetails {
  public double scaleFactor;
  public LeanSize imageSize;
  public LeanSize originalSize;
  public SVGDocument svgDocument;

  public SvgDetails() {
  }
}
