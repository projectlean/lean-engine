package org.lean.presentation.layout;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanAttachment;
import org.lean.core.exception.LeanException;
import org.lean.presentation.component.LeanComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * In case a position is not relative it means absolute vs the top and left margins of the page. In
 * that situation, you simply set or get the (x,y) position and you're done.
 *
 * <p>In case the position is relative versus another component, you need to provide a bunch of
 * details for the x and y coordinates.
 *
 * <ul>
 *   <li>The component name x relative to
 *   <li>The left, right, top or bottom of the referenced component
 *   <li>whether or not to place the position in the centre or middle.
 * </ul>
 */
public class LeanLayout {

  @HopMetadataProperty private LeanAttachment left;

  @HopMetadataProperty private LeanAttachment right;

  @HopMetadataProperty private LeanAttachment top;

  @HopMetadataProperty private LeanAttachment bottom;

  public LeanLayout() {}

  /**
   * Position the component relative to the page, with an offset of (x,y)
   *
   * @param x
   * @param y
   */
  public LeanLayout(int x, int y) {
    left = new LeanAttachment(0, x);
    top = new LeanAttachment(0, y);
  }

  public LeanLayout(
      LeanAttachment left, LeanAttachment right, LeanAttachment top, LeanAttachment bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  public LeanLayout(LeanLayout layout) {
    if (layout.left == null) {
      this.left = null;
    } else {
      this.left = new LeanAttachment(layout.left);
    }
    if (layout.right == null) {
      this.right = null;
    } else {
      this.right = new LeanAttachment(layout.right);
    }
    if (layout.top == null) {
      this.top = null;
    } else {
      this.top = new LeanAttachment(layout.top);
    }
    if (layout.bottom == null) {
      this.bottom = null;
    } else {
      this.bottom = new LeanAttachment(layout.bottom);
    }
  }

  public static LeanLayout topLeftPage() {
    LeanLayout layout = new LeanLayout();
    layout.left = new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT);
    layout.top = new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP);
    return layout;
  }

  /**
   * Place directly below the referenced component
   *
   * @param otherComponent The name of the component to reference
   * @param spanPageWidth span the width of the page
   * @return The requested layout
   */
  public static LeanLayout under(String otherComponent, boolean spanPageWidth) {
    LeanLayout layout = new LeanLayout();
    layout.left = new LeanAttachment(otherComponent, 0, 0, LeanAttachment.Alignment.LEFT);
    layout.top = new LeanAttachment(otherComponent, 0, 0, LeanAttachment.Alignment.BOTTOM);
    if (spanPageWidth) {
      layout.right = new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.RIGHT);
    }
    return layout;
  }

  /**
   * Place to the right of the referenced component at the same height
   *
   * @param otherComponent The name of the component to reference
   * @return The requested layout
   */
  public static LeanLayout right(String otherComponent, boolean spanPageWidth) {
    LeanLayout layout = new LeanLayout();
    layout.left = new LeanAttachment(otherComponent, 0, 0, LeanAttachment.Alignment.RIGHT);
    layout.top = new LeanAttachment(otherComponent, 0, 0, LeanAttachment.Alignment.TOP);
    if (spanPageWidth) {
      layout.right = new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.RIGHT);
    }
    return layout;
  }

  /**
   * Replace all left,top, right, bottom references with a new name
   *
   * @param oldName
   * @param newName
   */
  public void replaceReferences(String oldName, String newName) {
    for (LeanAttachment attachment : new LeanAttachment[] {left, top, right, bottom}) {
      if (attachment != null && oldName.equals(attachment.getComponentName())) {
        attachment.setComponentName(newName);
      }
    }
  }

  public Set<String> getReferencedLayoutComponentNames() {
    Set<String> names = new HashSet<>();
    for (LeanAttachment attachment : new LeanAttachment[] {left, top, right, bottom}) {
      if (attachment != null && StringUtils.isNotEmpty( attachment.getComponentName() ) ) {
        names.add(attachment.getComponentName() );
      }
    }
    return names;
  }

  public boolean hasLeft() {
    return left!=null;
  }

  public boolean hasTop() {
    return top!=null;
  }

  public boolean hasRight() {
    return right!=null;
  }

  public boolean hasBottom() {
    return bottom!=null;
  }

  public int numberOfAnchors() {
    int anchors = 0;
    if (hasLeft()) {
      anchors++;
    }
    if (hasRight()) {
      anchors++;
    }
    if (hasTop()) {
      anchors++;
    }
    if (hasBottom()) {
      anchors++;
    }
    return anchors;
  }

  public void validate( LeanComponent component) throws LeanException {
    if (hasLeft()) {
      switch (left.getAlignment()) {
        case TOP:
        case BOTTOM:
          throw new LeanException(
              "Setting a TOP or BOTTOM alignment makes no sense for left attachments on component "
                  + component.getName());
      }
    }
    if (hasTop()) {
      switch (top.getAlignment()) {
        case LEFT:
        case RIGHT:
          throw new LeanException(
            "Setting a LEFT or RIGHT alignment makes no sense for top attachments on component "
              + component.getName());
      }
    }
    if (hasRight()) {
      switch (right.getAlignment()) {
        case TOP:
        case BOTTOM:
          throw new LeanException(
            "Setting a TOP or BOTTOM alignment makes no sense for right attachments on component "
              + component.getName());
      }
    }
    if (hasBottom()) {
      switch (bottom.getAlignment()) {
        case LEFT:
        case RIGHT:
          throw new LeanException(
            "Setting a LEFT or RIGHT alignment makes no sense for bottom attachments on component "
              + component.getName());
      }
    }
  }

  /**
   * Gets left
   *
   * @return value of left
   */
  public LeanAttachment getLeft() {
    return left;
  }

  /** @param left The left to set */
  public void setLeft(LeanAttachment left) {
    this.left = left;
  }

  /**
   * Gets right
   *
   * @return value of right
   */
  public LeanAttachment getRight() {
    return right;
  }

  /** @param right The right to set */
  public void setRight(LeanAttachment right) {
    this.right = right;
  }

  /**
   * Gets top
   *
   * @return value of top
   */
  public LeanAttachment getTop() {
    return top;
  }

  /** @param top The top to set */
  public void setTop(LeanAttachment top) {
    this.top = top;
  }

  /**
   * Gets bottom
   *
   * @return value of bottom
   */
  public LeanAttachment getBottom() {
    return bottom;
  }

  /** @param bottom The bottom to set */
  public void setBottom(LeanAttachment bottom) {
    this.bottom = bottom;
  }
}
