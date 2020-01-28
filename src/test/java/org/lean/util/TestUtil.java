package org.lean.util;

import org.lean.core.LeanColorRGB;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;

public class TestUtil {

  public static void assertEqualPresentations( LeanPresentation presentation, LeanPresentation verify ) {
    assertEquals( presentation.getDescription(), verify.getDescription() );
    assertEquals( presentation.getPages().size(), verify.getPages().size() );
    for ( int p = 0; p < presentation.getPages().size(); p++ ) {
      LeanPage originPage = presentation.getPages().get( p );
      LeanPage verifyPage = verify.getPages().get( p );
      assertEqualsPages( originPage, verifyPage );
    }
    assertEquals( presentation.getConnectors().size(), verify.getConnectors().size() );
    for ( int c = 0; c < presentation.getConnectors().size(); c++ ) {
      LeanConnector originConnector = presentation.getConnectors().get( c );
      LeanConnector verifyConnector = verify.getConnectors().get( c );

      assertEqualConnectors( originConnector, verifyConnector );
    }

    assertEquals( presentation.getThemes().size(), verify.getThemes().size() );
    for ( int t = 0; t < presentation.getThemes().size(); t++ ) {
      LeanTheme originTheme = presentation.getThemes().get( t );
      LeanTheme verifyTheme = verify.getThemes().get( t );

      assertEqualThemes( originTheme, verifyTheme );
    }

  }

  public static void assertEqualConnectors( LeanConnector originConnector, LeanConnector verifyConnector ) {
    assertEquals( originConnector.getName(), verifyConnector.getName() );
    ILeanConnector originIConnector = originConnector.getConnector();
    ILeanConnector verifyIConnector = verifyConnector.getConnector();
    assertEquals( originIConnector.getPluginId(), verifyIConnector.getPluginId() );
    assertEquals( originIConnector.getSourceConnectorName(), verifyIConnector.getSourceConnectorName() );
  }

  public static void assertEqualsPages( LeanPage originPage, LeanPage verifyPage ) {
    assertEquals( originPage.getComponents().size(), verifyPage.getComponents().size() );

    for ( int c = 0; c < originPage.getComponents().size(); c++ ) {
      LeanComponent originComponent = originPage.getComponents().get( c );
      LeanComponent verifyComponent = verifyPage.getComponents().get( c );

      assertEqualsComponents( originComponent, verifyComponent );
    }
  }

  public static void assertEqualsComponents( LeanComponent originComponent, LeanComponent verifyComponent ) {
    ILeanComponent originIComponent = originComponent.getComponent();
    ILeanComponent verifyIComponent = verifyComponent.getComponent();

    assertEquals( originComponent.getName(), verifyComponent.getName() );
    Assert.assertEquals( originComponent.getSize(), verifyComponent.getSize() );
    assertEquals( originIComponent.getPluginId(), verifyIComponent.getPluginId() );
    assertEquals( originIComponent.getSourceConnectorName(), verifyIComponent.getSourceConnectorName() );
    assertEquals( originIComponent.getDefaultColor(), verifyIComponent.getDefaultColor() );
    assertEquals( originIComponent.getBackGroundColor(), verifyIComponent.getBackGroundColor() );
    assertEquals( originIComponent.getBorderColor(), verifyIComponent.getBorderColor() );
    Assert.assertEquals( originIComponent.getDefaultFont(), verifyIComponent.getDefaultFont() );
  }


  public static void assertEqualThemes( LeanTheme originTheme, LeanTheme verifyTheme ) {
    assertEquals( originTheme.getName(), verifyTheme.getName() );

    assertEquals( originTheme.getColors().size(), verifyTheme.getColors().size() );
    for (int c=0;c<originTheme.getColors().size();c++) {
      LeanColorRGB originColor = originTheme.getColors().get( c );
      LeanColorRGB verifyColor = verifyTheme.getColors().get( c );
      assertEquals(originColor, verifyColor);
    }

    assertEquals(originTheme.getDefaultColor(), verifyTheme.getDefaultColor());
    Assert.assertEquals(originTheme.getDefaultFont(), verifyTheme.getDefaultFont());
    assertEquals(originTheme.getBackgroundColor(), verifyTheme.getBackgroundColor());
    assertEquals(originTheme.getBorderColor(), verifyTheme.getBorderColor());


  }
}
