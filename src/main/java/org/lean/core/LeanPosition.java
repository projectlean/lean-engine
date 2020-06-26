package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

public class LeanPosition {

  @HopMetadataProperty
  private int x;
  @HopMetadataProperty
  private int y;

  /**
   * This constructor is needed for serialization purposes, keep it public
   */
  public LeanPosition() {
  }

  /**
   * @param x
   * @param y
   */
  public LeanPosition( int x, int y ) {
    this();
    this.x = x;
    this.y = y;
  }

  @Override public String toString() {
    return "LeanPosition(" + x + "," + y + ")";
  }

  /**
   * Gets x
   *
   * @return value of x
   */
  public int getX() {
    return x;
  }

  /**
   * @param x The x to set
   */
  public void setX( int x ) {
    this.x = x;
  }

  /**
   * Gets y
   *
   * @return value of y
   */
  public int getY() {
    return y;
  }

  /**
   * @param y The y to set
   */
  public void setY( int y ) {
    this.y = y;
  }
}
