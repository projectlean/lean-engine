package org.lean.presentation.theme;

import org.lean.core.Constants;
import org.lean.core.metastore.MetaStoreFactory;
import org.lean.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.stores.memory.MemoryMetaStore;

public class LeanThemeTest {

  public static final String THEME_NAME = "Theme1";
  private IMetaStore metaStore;

  @Before
  public void before() throws Exception {

    metaStore = new MemoryMetaStore();
  }

  @Test
  public void testThemeSaveLoad() throws Exception {
    MetaStoreFactory<LeanTheme> factory = new MetaStoreFactory<>( LeanTheme.class, metaStore, Constants.NAMESPACE );

    LeanTheme theme = createTheme( THEME_NAME );
    factory.saveElement( theme );

    // Load it back...
    //
    LeanTheme verify = factory.loadElement( THEME_NAME );

    TestUtil.assertEqualThemes(theme, verify);

  }

  @After
  public void after() throws Exception {
  }



  public static final LeanTheme createTheme(String name) {
    LeanTheme theme = LeanTheme.getDefault();
    theme.setName( name );
    return theme;
  }
}
