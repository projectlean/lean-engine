package org.lean.render.svg;

import org.apache.hop.core.database.DatabaseMetaPlugin;
import org.apache.hop.core.database.DatabasePluginType;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.databases.h2.H2DatabaseMeta;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.lean.core.LeanEnvironment;
import org.lean.core.log.DurationRequest;
import org.lean.core.log.LeanMetricsUtil;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.datacontext.PresentationDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.render.IRenderContext;
import org.lean.render.context.PresentationRenderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;


@Ignore
public class LeanPresentationTestBase {

  protected IHopMetadataProvider metadataProvider;
  protected ILoggingObject parent;
  protected String folderName;

  @Before
  public void setUp() throws Exception {

    // Create a metastore
    //
    metadataProvider = new MemoryMetadataProvider();
    LeanEnvironment.init();

    // PluginRegistry.getInstance().registerPluginClass( H2DatabaseMeta.class.getName(), DatabasePluginType.class, DatabaseMetaPlugin.class );


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
    IDataContext dataContext = new PresentationDataContext( presentation, metadataProvider );

    LeanLayoutResults results = presentation.doLayout( parent, renderContext, metadataProvider );
    presentation.render( results, metadataProvider );

    ILogChannel log = results.getLog();

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