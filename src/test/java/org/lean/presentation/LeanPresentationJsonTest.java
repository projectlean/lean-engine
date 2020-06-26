package org.lean.presentation;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lean.core.LeanEnvironment;
import org.lean.util.BasePresentationUtil;
import org.lean.util.TestUtil;

public class LeanPresentationJsonTest {

  private IHopMetadataProvider metadataProvider;

  @Before
  public void setUp() throws Exception {
    metadataProvider = new MemoryMetadataProvider();
    LeanEnvironment.init();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testJson() throws Exception {

    LeanPresentation[] presentations = new BasePresentationUtil( metadataProvider ).getAvailablePresentations();

    for ( LeanPresentation presentation : presentations ) {
      String jsonString = presentation.toJsonString();
      LeanPresentation verify = LeanPresentation.fromJsonString( jsonString );
      TestUtil.assertEqualPresentations( presentation, verify );
    }
  }

  @Test
  public void testMetaStore() throws Exception {

    IHopMetadataSerializer<LeanPresentation> presentationSerializer = metadataProvider.getSerializer( LeanPresentation.class );

    LeanPresentation[] presentations = new BasePresentationUtil( metadataProvider ).getAvailablePresentations();

    for ( LeanPresentation presentation : presentations ) {

      presentationSerializer.save( presentation );
      LeanPresentation verify = presentationSerializer.load( presentation.getName() );

      TestUtil.assertEqualPresentations( presentation, verify );
    }
  }
}