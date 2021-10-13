package org.lean.core;

public class LeanTextGeometry {

  private int width;

  private int height;

  private int offsetX;

  private int offsetY;

  /** This constructor is needed for serialization purposes, keep it public */
  public LeanTextGeometry() {}

  /**
   * @param width
   * @param height
   * @param offsetX
   * @param offsetY
   */
  public LeanTextGeometry(int width, int height, int offsetX, int offsetY) {
    super();
    this.width = width;
    this.height = height;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  /** @return the width */
  public int getWidth() {
    return width;
  }

  /** @param width the width to set */
  public void setWidth(int width) {
    this.width = width;
  }

  /** @return the height */
  public int getHeight() {
    return height;
  }

  /** @param height the height to set */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Gets offsetX
   *
   * @return value of offsetX
   */
  public int getOffsetX() {
    return offsetX;
  }

  /** @param offsetX The offsetX to set */
  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }

  /**
   * Gets offsetY
   *
   * @return value of offsetY
   */
  public int getOffsetY() {
    return offsetY;
  }

  /** @param offsetY The offsetY to set */
  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }
}
