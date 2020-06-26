package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.awt.*;
import java.util.Objects;

public class LeanGeometry implements Cloneable {

  @HopMetadataProperty
  private int x;
  @HopMetadataProperty
  private int y;
  @HopMetadataProperty
  private int width;
  @HopMetadataProperty
  private int height;

  /**
   * This constructor is needed for serialization purposes, keep it public
   */
  public LeanGeometry() {
  }

  /**
   * @param x
   * @param y
   * @param width
   * @param height
   */
  public LeanGeometry( int x, int y, int width, int height ) {
    super();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    LeanGeometry geometry = (LeanGeometry) o;
    return x == geometry.x &&
      y == geometry.y &&
      width == geometry.width &&
      height == geometry.height;
  }

  @Override public int hashCode() {
    return Objects.hash( x, y, width, height );
  }

  @Override public String toString() {
    return "LeanGeometry("+x+","+y+":"+width+"x"+height+")";
  }

  public boolean contains( int px, int py ) {
    return new Rectangle(x, y, width, height).contains( px, py );
  }

  /**
   * Add the geometry to the existing one.
   * Find the maximum surface area.
   *
   * @param g
   */
  public void maxSurface( LeanGeometry g ) {
    x=Math.min(x, g.x);
    y=Math.min(y, g.y);
    width = Math.max(width, g.x+g.width);
    height = Math.max(height, g.y+g.height);
  }

  /**
   * Of this geometry or g, keep the lowest
   * @param g
   */
  public void lowest( LeanGeometry g ) {
    if (y+height<g.y+g.height) {
      x=g.x;
      y=g.y;
      width = g.width;
      height= g.height;
    }
  }

  @Override
  public LeanGeometry clone() {
    return new LeanGeometry( x, y, width, height );
  }

  public void incHeight( int inc) {
    height+=inc;
  }

  public void incWidth( int inc) {
    width+=inc;
  }

  public void incX( int inc ) {
    x+=inc;
  }

  public void incY( int inc ) {
    y+=inc;
  }


  /**
   * @return the x
   */
  public int getX() {
    return x;
  }

  /**
   * @param x the x to set
   */
  public void setX( int x ) {
    this.x = x;
  }

  /**
   * @return the y
   */
  public int getY() {
    return y;
  }

  /**
   * @param y the y to set
   */
  public void setY( int y ) {
    this.y = y;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth( int width ) {
    this.width = width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height the height to set
   */
  public void setHeight( int height ) {
    this.height = height;
  }
}
