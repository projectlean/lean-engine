package org.lean.www.servlets;

import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.render.IRenderContext;
import org.lean.render.context.PresentationRenderContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PresentationCache {

  private static PresentationCache instance;

  private Map<LeanPresentation, LeanLayoutResults> presentationResultsMap;

  private PresentationCache() {
    presentationResultsMap = new HashMap<>();
  }

  /**
   * Gets instance
   *
   * @return value of instance
   */
  public static PresentationCache getInstance() {
    if (instance == null) {
      instance = new PresentationCache();
    }
    return instance;
  }

  public static LeanLayoutResults renderAndCache(
      LeanPresentation presentation, ILoggingObject parent, IHopMetadataProvider metadataProvider)
      throws LeanException {
    LeanLayoutResults results = getInstance().presentationResultsMap.get(presentation);
    if (results != null) {
      return results;
    }

    IRenderContext renderContext = new PresentationRenderContext(presentation);
    results = presentation.doLayout(parent, renderContext, metadataProvider, Collections.emptyList());
    presentation.render(results, metadataProvider);

    getInstance().presentationResultsMap.put(presentation, results);
    return results;
  }

  /**
   * Gets presentationResultsMap
   *
   * @return value of presentationResultsMap
   */
  public Map<LeanPresentation, LeanLayoutResults> getPresentationResultsMap() {
    return presentationResultsMap;
  }
}
