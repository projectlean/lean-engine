package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.TablePresentationUtil;
import org.junit.Test;

public class LeanPresentationTableChainTest extends LeanPresentationTestBase {
  
  @Test
  public void testTableChainRender() throws Exception {

    LeanPresentation presentation = new TablePresentationUtil( metadataProvider, variables ).createTableChainPresentation( 2100 );
    testRendering( presentation, "table_chain_test");
  }

}