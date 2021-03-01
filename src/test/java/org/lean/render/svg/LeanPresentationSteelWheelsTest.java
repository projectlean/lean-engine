package org.lean.render.svg;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.lean.core.Constants;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.theme.LeanTheme;
import org.lean.util.H2DatabaseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.file.Path;

public class LeanPresentationSteelWheelsTest extends LeanPresentationTestBase {


  @Test
  public void testSteelWheelsJsonPresentationsRender() throws Exception {

    // populate steelwheels database and save connection in metastore
    //
    H2DatabaseUtil.createSteelWheelsDatabase( metadataProvider, variables );

    // Load all files in resources/presentations/*.json
    //
    File dir = new File( "src/test/resources/presentations/" );
    File[] files = dir.listFiles( new FilenameFilter() {
      @Override
      public boolean accept( File dir, String name ) {
        return name.endsWith( ".json" );
      }
    } );

    for ( File file : files ) {
      FileInputStream inputStream = new FileInputStream( file );
      try {
        String jsonString = IOUtils.toString( inputStream );
        LeanPresentation presentation = LeanPresentation.fromJsonString( jsonString );
        Path path = file.toPath();
        String baseFilename = path.getFileName().toString().replace( ".json", "" );

        // Add a default theme to make sure we're not dealing with L&F shenanigans
        //
        if ( StringUtils.isEmpty( presentation.getDefaultThemeName() ) ) {
          presentation.getThemes().add( LeanTheme.getDefault() );
          presentation.setDefaultThemeName( Constants.DEFAULT_THEME_NAME );
        }

        testRendering( presentation, baseFilename );

      } catch ( Exception e ) {
        IOUtils.closeQuietly( inputStream );
        throw e;
      }

    }
  }

}