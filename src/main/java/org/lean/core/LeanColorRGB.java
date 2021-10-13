package org.lean.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.hop.metadata.api.HopMetadataProperty;

import java.awt.*;

public class LeanColorRGB {

  public static final LeanColorRGB BLACK = new LeanColorRGB("#000000");
  public static final LeanColorRGB WHITE = new LeanColorRGB("#ffffff");

  @HopMetadataProperty private int r;

  @HopMetadataProperty private int g;

  @HopMetadataProperty private int b;

  public LeanColorRGB() {
    r = 0;
    g = 140;
    b = 194;
  }

  public LeanColorRGB(int r, int g, int b) {
    this();
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public LeanColorRGB(LeanColorRGB c) {
    this(c.r, c.g, c.b);
  }

  /**
   * Decode standard hex values like "#FFCCEE"
   *
   * @param hexValue The hex value to convert to RGB
   */
  public LeanColorRGB(String hexValue) {
    Color color = Color.decode(hexValue);
    this.r = color.getRed();
    this.g = color.getGreen();
    this.b = color.getBlue();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LeanColorRGB)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    LeanColorRGB color = (LeanColorRGB) obj;
    return r == color.r && g == color.g && b == color.b;
  }

  @Override
  public String toString() {
    return "Color(" + r + "," + g + "," + b + ")";
  }

  public int getR() {
    return r;
  }

  public void setR(int r) {
    this.r = r;
  }

  public int getG() {
    return g;
  }

  public void setG(int g) {
    this.g = g;
  }

  public int getB() {
    return b;
  }

  public void setB(int b) {
    this.b = b;
  }

  @JsonIgnore
  public String getHexColor() {
    Color color = new Color(getR(), getG(), getB());
    String hex = Integer.toHexString(color.getRGB() & 0xffffff);
    if (hex.length() < 6) {
      hex = "0" + hex;
    }
    hex = "#" + hex;
    return hex;
  }
}
