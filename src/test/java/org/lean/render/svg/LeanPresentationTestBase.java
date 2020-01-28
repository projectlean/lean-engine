package org.lean.render.svg;

import org.lean.core.LeanEnvironment;
import org.lean.core.log.DurationRequest;
import org.lean.core.log.LeanMetricsUtil;
import org.lean.core.metastore.LeanMetaStoreUtil;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.datacontext.PresentationDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.render.IRenderContext;
import org.lean.render.context.PresentationRenderContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.apache.hop.core.logging.LogChannelInterface;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.logging.LoggingObjectInterface;
import org.apache.hop.metastore.api.IMetaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;


@Ignore
public class LeanPresentationTestBase {

  protected IMetaStore metaStore;
  protected LoggingObjectInterface parent;
  protected String folderName;

  @Before
  public void setUp() throws Exception {

    // Create a metastore
    //
    metaStore = LeanMetaStoreUtil.createTestMetaStore( "Test" );
    LeanEnvironment.init( metaStore );

    parent = new LoggingObject( "Presentation unit test" );

    // Create SVG output folder if it doesn't exist
    //
    folderName = System.getProperty( "java.io.tmpdir" ) + "/Lean/";
    File folder = new File( folderName );
    if ( !folder.exists() ) {
      folder.mkdirs();
    }
  }

  @After
  public void tearDown() throws Exception {
    LeanMetaStoreUtil.cleanupTestMetaStore( metaStore );
  }

  @Ignore
  public List<DurationRequest> getStandardDurationRequests() {
    List<DurationRequest> requests = new ArrayList<>();

    requests.add( new DurationRequest( LeanMetricsUtil.PRESENTATION_START_LAYOUT, LeanMetricsUtil.PRESENTATION_FINISH_LAYOUT, "Layout time was" ) );
    requests.add( new DurationRequest( LeanMetricsUtil.PRESENTATION_START_RENDER, LeanMetricsUtil.PRESENTATION_FINISH_RENDER, "Rendering time was" ) );
    requests.add( new DurationRequest( LeanMetricsUtil.PRESENTATION_START_LAYOUT, LeanMetricsUtil.PRESENTATION_FINISH_RENDER, "Total time was" ) );

    return requests;
  }

  @Ignore
  protected void testRendering( LeanPresentation presentation, String filename ) throws Exception {
    testRendering( presentation, filename, getStandardDurationRequests() );
  }

    @Ignore
  protected void testRendering( LeanPresentation presentation, String filename, List<DurationRequest> durationRequests ) throws Exception {

    IRenderContext renderContext = new PresentationRenderContext( presentation );
    IDataContext dataContext = new PresentationDataContext( presentation, metaStore );

    LeanLayoutResults results = presentation.doLayout( parent, renderContext, metaStore );
    presentation.render( results, metaStore );

    LogChannelInterface log = results.getLog();

    for ( DurationRequest durationRequest : durationRequests ) {
      long duration = LeanMetricsUtil.getLastDuration( log, durationRequest.getStartId(), durationRequest.getFinishId() );
      log.logBasic( durationRequest.getMessage() + " " + duration + " ms" );
    }

    results.saveSvgPages( folderName, filename, true, true, true );

    LeanRenderPage leanRenderPage = results.getRenderPages().get( 0 );
    String xml = leanRenderPage.getSvgXml();
    assertNotNull( xml );
  }
}