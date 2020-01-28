package org.lean.render;

import org.lean.core.LeanRenderType;

/**
 * This contains all sorts of rendering outputs
 *
 */
public interface IRenderOutput {

  /**
   * The render type of the output
   * @return
   */
  public LeanRenderType getRenderType();


  /*
   * Apply the render context (sizes, default colors, ...)
   *
   * @param renderType
   * @param page the page to initialize
   * @param renderContext the render context to apply
   *
  void applyRenderContext( LeanRenderType renderType, LeanPage page, IRenderContext renderContext ) throws LeanException;
  */
}
