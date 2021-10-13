package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.TablePresentationUtil;

public class LeanPresentationTableChainTest extends LeanPresentationTestBase {

  @Test
  public void testTableChainRender() throws Exception {

    LeanPresentation presentation =
        new TablePresentationUtil(metadataProvider, variables).createTableChainPresentation(2100);
    testRendering(presentation, "table_chain_test");
  }
}
