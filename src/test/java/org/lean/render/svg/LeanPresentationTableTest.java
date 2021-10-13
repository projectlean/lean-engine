package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.util.TablePresentationUtil;

public class LeanPresentationTableTest extends LeanPresentationTestBase {

  @Test
  public void testTableRender() throws Exception {

    LeanPresentation presentation =
        new TablePresentationUtil(metadataProvider, variables).createTablePresentation(2000);
    testRendering(presentation, "table_test");
  }
}
