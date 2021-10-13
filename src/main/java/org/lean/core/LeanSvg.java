package org.lean.core;

/** Represents a piece of SVG on a certain location in another document */
public class LeanSvg {

  private String svgXml;

  private LeanGeometry geometry;

  public LeanSvg() {}

  public LeanSvg(String svgXml, LeanGeometry geometry) {
    this.svgXml = svgXml;
    this.geometry = geometry;
  }

  /**
   * Gets svgXml
   *
   * @return value of svgXml
   */
  public String getSvgXml() {
    return svgXml;
  }

  /** @param svgXml The svgXml to set */
  public void setSvgXml(String svgXml) {
    this.svgXml = svgXml;
  }

  /**
   * Gets geometry
   *
   * @return value of geometry
   */
  public LeanGeometry getGeometry() {
    return geometry;
  }

  /** @param geometry The geometry to set */
  public void setGeometry(LeanGeometry geometry) {
    this.geometry = geometry;
  }
}
