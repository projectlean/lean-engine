package org.lean.presentation.layout;

import org.lean.core.LeanAttachment;

public class LeanLayoutBuilder {
  private LeanLayout layout;

  public LeanLayoutBuilder() {
    layout = new LeanLayout(null, null, null, null);
  }

  public LeanLayout build() {
    return layout;
  }

  public LeanLayoutBuilder all() {
    return left().top().right().bottom();
  }

  public LeanLayoutBuilder all(int margin) {
    return left(margin).top(margin).right(-margin).bottom(-margin);
  }

  /**
   * Specify a position at the top of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder top() {
    layout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP));
    return this;
  }

  /**
   * Specify a position at the top of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder top(int offset) {
    return top(0, offset);
  }

  /**
   * Specify a position at the top of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder top(int percentage, int offset) {
    layout.setTop(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.TOP));
    return this;
  }

  /**
   * Specify a position at the left of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder left() {
    layout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT));
    return this;
  }

  /**
   * Specify a position at the left of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder left(int offset) {
    return left(0, offset);
  }

  /**
   * Specify a left boundary in percentage to the left of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder left(int percentage, int offset) {
    layout.setLeft(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.LEFT));
    return this;
  }

  /**
   * Specify a boundary at the right of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder right() {
    layout.setRight(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.RIGHT));
    return this;
  }

  /**
   * Specify a boundary at the right of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder right(int offset) {
    return right(0, offset);
  }

  /**
   * Specify a right boundary in percentage to the right of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder right(int percentage, int offset) {
    layout.setRight(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.RIGHT));
    return this;
  }

  /**
   * Specify a boundary at the bottom of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder bottom() {
    layout.setBottom(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.BOTTOM));
    return this;
  }

  /**
   * Specify a boundary at the bottom of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder bottom(int offset) {
    return bottom(0, offset);
  }

  /**
   * Specify a bottom boundary in percentage to the bottom of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder bottom(int percentage, int offset) {
    layout.setBottom(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.BOTTOM));
    return this;
  }

  /**
   * Specify a bottom boundary relative to the top of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder bottomFromTop(int percentage, int offset) {
    layout.setBottom(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.TOP));
    return this;
  }

  /**
   * Specify a top boundary relative to the bottom of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder topFromBottom(int percentage, int offset) {
    layout.setTop(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.BOTTOM));
    return this;
  }

  /**
   * Specify a position at the top of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder topFromBottom(String referenceComponent, int percentage, int offset) {
    layout.setTop(new LeanAttachment(referenceComponent, percentage, offset, LeanAttachment.Alignment.BOTTOM));
    return this;
  }

  /**
   * Specify a top using the top of the referenced component
   *
   * @return the builder
   */
  public LeanLayoutBuilder topFromTop(String referenceComponent, int percentage, int offset) {
    layout.setTop(new LeanAttachment(referenceComponent, percentage, offset, LeanAttachment.Alignment.TOP));
    return this;
  }

  /**
   * Specify a bottom using the top of the referenced component
   *
   * @return the builder
   */
  public LeanLayoutBuilder bottomFromTop(String referenceComponent, int percentage, int offset) {
    layout.setBottom(new LeanAttachment(referenceComponent, percentage, offset, LeanAttachment.Alignment.TOP));
    return this;
  }


  /**
   * Specify a position right below another component with a vertical offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder below(String referenceComponent, int verticalOffset) {
    layout.setLeft(new LeanAttachment(referenceComponent, 0, 0, LeanAttachment.Alignment.LEFT));
    layout.setTop(new LeanAttachment(referenceComponent, 0, verticalOffset, LeanAttachment.Alignment.BOTTOM));
    return this;
  }

  /**
   * Specify a left boundary relative to the right of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder leftFromRight(int percentage, int offset) {
    layout.setLeft(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.RIGHT));
    return this;
  }

  /**
   * Specify a left boundary relative to the center of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder leftCenter() {
    return leftCenter(0,0);
  }

  /**
   * Specify a left boundary relative to the center of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder leftCenter( int percentage, int offset) {
    layout.setLeft(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.CENTER));
    return this;
  }

  /**
   * Specify a top boundary relative to the center of the parent
   *
   * @return the builder
   */
  public LeanLayoutBuilder topFromCenter() {
    return topCenter(0,0);
  }

  /**
   * Specify a top boundary relative to the center of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder topCenter(int percentage, int offset) {
    layout.setTop(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.CENTER));
    return this;
  }

  /**
   * Specify a right boundary relative to the left of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder rightFromLeft(int percentage, int offset) {
    layout.setRight(new LeanAttachment(null, percentage, offset, LeanAttachment.Alignment.LEFT));
    return this;
  }

  /**
   * Specify a right boundary relative to the left of the parent with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder rightFromLeft(String referenceComponent, int percentage, int offset) {
    layout.setRight(new LeanAttachment(referenceComponent, percentage, offset, LeanAttachment.Alignment.LEFT));
    return this;
  }


  /**
   * Specify a left boundary relative to the right of the reference component with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder leftFromRight(String componentName, int percentage, int offset) {
    layout.setLeft(new LeanAttachment(componentName, percentage, offset, LeanAttachment.Alignment.RIGHT));
    return this;
  }

  /**
   * Specify a right boundary relative to the right of the reference component with an offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder rightFromRight(String componentName, int percentage, int offset) {
    layout.setRight(new LeanAttachment(componentName, percentage, offset, LeanAttachment.Alignment.RIGHT));
    return this;
  }

  /**
   * Specify a right boundary relative to the center of the reference component
   *
   * @return the builder
   */
  public LeanLayoutBuilder rightFromCenter(String componentName, int percentage, int offset) {
    layout.setRight(new LeanAttachment(componentName, percentage, offset, LeanAttachment.Alignment.CENTER));
    return this;
  }


  /**
   * Specify a position to the right of another component with a horizontal offset
   *
   * @return the builder
   */
  public LeanLayoutBuilder beside(String referenceComponent, int horizontalOffset) {
    layout.setLeft(new LeanAttachment(referenceComponent, 0, horizontalOffset, LeanAttachment.Alignment.RIGHT));
    layout.setTop(new LeanAttachment(referenceComponent, 0, 0, LeanAttachment.Alignment.TOP));
    return this;
  }

  public LeanLayoutBuilder left( LeanAttachment attachment ) {
    layout.setLeft( attachment );
    return this;
  }

  public LeanLayoutBuilder top( LeanAttachment attachment ) {
    layout.setTop( attachment );
    return this;
  }

  public LeanLayoutBuilder right( LeanAttachment attachment ) {
    layout.setRight( attachment );
    return this;
  }

  public LeanLayoutBuilder bottom( LeanAttachment attachment ) {
    layout.setBottom( attachment );
    return this;
  }
}
