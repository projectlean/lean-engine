package org.lean.presentation.theme;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lean.util.TestUtil;

public class LeanThemeTest {

  public static final String THEME_NAME = "Theme1";
  private IHopMetadataProvider metadataProvider;

  public static final LeanTheme createTheme(String name) {
    LeanTheme theme = LeanTheme.getDefault();
    theme.setName(name);
    return theme;
  }

  @Before
  public void before() throws Exception {
    metadataProvider = new MemoryMetadataProvider();
  }

  @Test
  public void testThemeSaveLoad() throws Exception {

    IHopMetadataSerializer<LeanTheme> themeSerializer =
        metadataProvider.getSerializer(LeanTheme.class);

    LeanTheme theme = createTheme(THEME_NAME);
    themeSerializer.save(theme);

    // Load it back...
    //
    LeanTheme verify = themeSerializer.load(THEME_NAME);

    TestUtil.assertEqualThemes(theme, verify);
  }

  @After
  public void after() throws Exception {}
}
