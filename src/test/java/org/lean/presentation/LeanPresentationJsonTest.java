package org.lean.presentation;

import org.lean.core.Constants;
import org.lean.core.LeanEnvironment;
import org.lean.core.metastore.LeanMetaStoreUtil;
import org.lean.core.metastore.MetaStoreFactory;
import org.lean.util.BasePresentationUtil;
import org.lean.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.hop.metastore.api.IMetaStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LeanPresentationJsonTest {

  private IMetaStore metaStore;

  @Before
  public void setUp() throws Exception {
    metaStore = LeanMetaStoreUtil.createTestMetaStore( "Test" );
    LeanEnvironment.init( metaStore );
  }

  @After
  public void tearDown() throws Exception {
    LeanMetaStoreUtil.cleanupTestMetaStore( metaStore );
  }

  @Test
  public void testJson() throws Exception {

    LeanPresentation[] presentations = BasePresentationUtil.getAvailablePresentations();

    for (LeanPresentation presentation : presentations) {
      String jsonString = presentation.toJsonString();
      LeanPresentation verify = LeanPresentation.fromJsonString( jsonString );
      TestUtil.assertEqualPresentations( presentation, verify );
    }
  }

  @Test
  public void testMetaStore() throws Exception {

    MetaStoreFactory<LeanPresentation> factory = new MetaStoreFactory<>( LeanPresentation.class, metaStore, Constants.NAMESPACE );
    LeanPresentation[] presentations = BasePresentationUtil.getAvailablePresentations();

    for (LeanPresentation presentation : presentations) {

      factory.saveElement( presentation );
      LeanPresentation verify = factory.loadElement( presentation.getName() );

      TestUtil.assertEqualPresentations( presentation, verify );
    }
  }
}