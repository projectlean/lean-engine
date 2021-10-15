package org.lean.presentation.component.listeners;

import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

public interface IDoLayoutListener {
  /**
   * The doLayout() method of a component is about to be called. Before that happens we give the
   * chance to call this method through the listener(s) in the component.
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param renderContext
   * @param results
   * @throws LeanException
   */
  void beforeDoLayout(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;
}
