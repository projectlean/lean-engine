package org.lean.presentation.layout;

import org.lean.core.LeanAttachment;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

/**
 * In case a position is not relative it means absolute vs the top and left margins of the page.
 * In that situation, you simply set or get the (x,y) position and you're done.
 * <p>
 * In case the position is relative versus another component, you need to provide a bunch of details for the x and y coordinates.
 * <ul>
 * <li>The component name x relative to</li>
 * <li>The left, right, top or bottom of the referenced component</li>
 * <li>whether or not to place the position in the centre or middle.</li>
 * </ul>
 */
public class LeanLayout {

  @MetaStoreAttribute
  private LeanAttachment left;

  @MetaStoreAttribute
  private LeanAttachment right;

  @MetaStoreAttribute
  private LeanAttachment top;

  @MetaStoreAttribute
  private LeanAttachment bottom;

  public LeanLayout() {
  }

  /**
   * Position the component relative to the page, with an offset of (x,y)
   * @param x
   * @param y
   */
  public LeanLayout(int x, int y) {
    left = new LeanAttachment( 0, x );
    top = new LeanAttachment( 0, y );
  }

  public LeanLayout( LeanAttachment left, LeanAttachment right, LeanAttachment top, LeanAttachment bottom ) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  public LeanLayout( LeanLayout layout ) {
    if (layout.left==null) {
      this.left = null;
    } else {
      this.left = new LeanAttachment( layout.left );
    }
    if (layout.right==null) {
      this.right= null;
    } else {
      this.right = new LeanAttachment( layout.right);
    }
    if (layout.top==null) {
      this.top= null;
    } else {
      this.top = new LeanAttachment( layout.top);
    }
    if (layout.bottom==null) {
      this.bottom = null;
    } else {
      this.bottom = new LeanAttachment( layout.bottom);
    }
  }

  public static LeanLayout topLeftPage() {
    LeanLayout layout = new LeanLayout();
    layout.left = new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT  );
    layout.top = new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP );
    return layout;
  }


  /**
   * Replace all left,top, right, bottom references with a new name
   *
   * @param oldName
   * @param newName
   */
  public void replaceReferences( String oldName, String newName ) {
    for (LeanAttachment attachment : new LeanAttachment[] { left, top, right, bottom}) {
      if (attachment!=null && oldName.equals(attachment.getComponentName())) {
        attachment.setComponentName( newName );
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

  /**
   * @param left The left to set
   */
  public void setLeft( LeanAttachment left ) {
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

  /**
   * @param right The right to set
   */
  public void setRight( LeanAttachment right ) {
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

  /**
   * @param top The top to set
   */
  public void setTop( LeanAttachment top ) {
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

  /**
   * @param bottom The bottom to set
   */
  public void setBottom( LeanAttachment bottom ) {
    this.bottom = bottom;
  }
}
