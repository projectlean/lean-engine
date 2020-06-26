package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.util.TablePresentationUtil;
import org.junit.Test;

public class LeanPresentationTableTest extends LeanPresentationTestBase {
  
  @Test
  public void testTableRender() throws Exception {

    LeanPresentation presentation = new TablePresentationUtil( metadataProvider ).createTablePresentation( 2000 );
    testRendering( presentation, "table_test");
  }

}