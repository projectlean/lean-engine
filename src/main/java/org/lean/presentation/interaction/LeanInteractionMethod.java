package org.lean.presentation.interaction;

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.Objects;

/** A Lean interaction method describes the way a user can interact with any part of a presentation. */
public class LeanInteractionMethod {

  @HopMetadataProperty private boolean mouseClick;

  @HopMetadataProperty private boolean mouseDoubleClick;

  public LeanInteractionMethod() {
  }

  public LeanInteractionMethod( boolean mouseClick, boolean mouseDoubleClick) {
    this.mouseClick = mouseClick;
    this.mouseDoubleClick = mouseDoubleClick;
  }

  public LeanInteractionMethod( LeanInteractionMethod method ) {
    this.mouseClick = method.mouseClick;
    this.mouseDoubleClick = method.mouseDoubleClick;
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    LeanInteractionMethod that = (LeanInteractionMethod) o;
    return mouseClick == that.mouseClick && mouseDoubleClick == that.mouseDoubleClick;
  }

  @Override public int hashCode() {
    return Objects.hash( mouseClick, mouseDoubleClick );
  }

  /**
   * Gets mouseClick
   *
   * @return value of mouseClick
   */
  public boolean isMouseClick() {
    return mouseClick;
  }

  /** @param mouseClick The mouseClick to set */
  public void setMouseClick(boolean mouseClick) {
    this.mouseClick = mouseClick;
  }

  /**
   * Gets mouseDoubleClick
   *
   * @return value of mouseDoubleClick
   */
  public boolean isMouseDoubleClick() {
    return mouseDoubleClick;
  }

  /** @param mouseDoubleClick The mouseDoubleClick to set */
  public void setMouseDoubleClick(boolean mouseDoubleClick) {
    this.mouseDoubleClick = mouseDoubleClick;
  }
}
