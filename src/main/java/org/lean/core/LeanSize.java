package org.lean.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

public class LeanSize {

  public static final LeanSize UNKNOWN_SIZE = new LeanSize(-1, -1);

  @MetaStoreAttribute
  private int width;
  @MetaStoreAttribute
  private int height;

  /**
   * This constructor is needed for serialization purposes, keep it public
   */
  public LeanSize() {
  }

  /**
   * @param width
   * @param height
   */
  public LeanSize( int width, int height ) {
    this();
    this.width = width;
    this.height = height;
  }

  public LeanSize( LeanSize size ) {
    this();
    this.width = size.width;
    this.height = size.height;
  }

  @Override public String toString() {
    return "LeanSize("+width+"x"+height+")";
  }

  @Override public boolean equals( Object obj ) {
    if (!(obj instanceof LeanSize)) {
      return false;
    }
    if (obj==this) {
      return true;
    }
    LeanSize size = (LeanSize) obj;
    return width==size.width && height==size.height;
  }

  @JsonIgnore
  public boolean isDefined() {
    return width>0 && height>0;
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
