package org.lean.render.context;

import org.lean.core.Constants;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanEnvironment;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.theme.LeanTheme;
import org.junit.Before;
import org.junit.Test;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.stores.memory.MemoryMetaStore;

import static org.junit.Assert.*;

public class PresentationRenderContextTest {

  @Before
  public void setUp() throws LeanException {
    IMetaStore metaStore = new MemoryMetaStore();
    LeanEnvironment.init( metaStore );

  }

  @Test
  public void getStableColor() {

    LeanPresentation presentation = new LeanPresentation();
    presentation.getThemes().add( LeanTheme.getDefault() );
    presentation.setDefaultThemeName( Constants.DEFAULT_THEME_NAME );

    PresentationRenderContext renderContext = new PresentationRenderContext( presentation );

    LeanColorRGB a1 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "A" );
    LeanColorRGB a2 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "A" );
    assertEquals( a1, a2 );

    LeanColorRGB b1 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "B" );
    LeanColorRGB b2 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "B" );
    assertEquals( b1, b2 );
    assertNotSame( a1, b1 );

    LeanColorRGB c1 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "C" );
    LeanColorRGB c2 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "C" );
    assertEquals( c1, c2 );
    assertNotSame( a1, c1 );
    assertNotSame( b1, c1 );

    LeanColorRGB a3 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "A" );
    assertEquals( a1, a3 );
    LeanColorRGB b3 = renderContext.getStableColor( Constants.DEFAULT_THEME_NAME, "B" );
    assertEquals( b1, b3 );

  }
}