package org.lean.util;

import org.apache.hop.core.database.DatabaseMetaPlugin;
import org.apache.hop.core.database.DatabasePluginType;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.databases.h2.H2DatabaseMeta;
import org.apache.hop.databases.mysql.MySqlDatabaseMeta;
import org.apache.hop.databases.oracle.OracleDatabaseMeta;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanAttachment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.component.types.svg.LeanSvgComponent;
import org.lean.presentation.component.types.svg.ScaleType;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.types.sampledata.LeanSampleDataConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutBuilder;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;

public class BasePresentationUtil {

  public static final String HEADER_MESSAGE_LABEL = "HeaderLabel";
  public static String CONNECTOR_SAMPLE_ROWS = "Sample rows";
  protected IHopMetadataProvider metadataProvider;
  protected IVariables variables;

  public BasePresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    this.metadataProvider = metadataProvider;
    this.variables = variables;
  }

  public static void registerTestPlugins() throws HopPluginException {
    PluginRegistry.getInstance()
        .registerPluginClass(
            H2DatabaseMeta.class.getName(), DatabasePluginType.class, DatabaseMetaPlugin.class);
    PluginRegistry.getInstance()
        .registerPluginClass(
            OracleDatabaseMeta.class.getName(), DatabasePluginType.class, DatabaseMetaPlugin.class);
    PluginRegistry.getInstance()
        .registerPluginClass(
            MySqlDatabaseMeta.class.getName(), DatabasePluginType.class, DatabaseMetaPlugin.class);
  }

  protected static LeanPresentation createBasePresentation(
      String name, String description, int rowCount, String headerMessage) {
    return createBasePresentation(name, description, rowCount, headerMessage, false);
  }

  protected static LeanPresentation createBasePresentation(
      String name, String description, int rowCount, String headerMessage, boolean portrait) {
    LeanPresentation presentation = new LeanPresentation();
    presentation.setName(name);
    presentation.setDescription(description);

    // Add a default theme scheme.
    // It has information about background colors, chart colors and so on.
    //
    LeanTheme theme = LeanTheme.getDefault();
    presentation.getThemes().add(theme);

    // Setting a default theme allows all components in the presentation to use it
    //
    presentation.setDefaultThemeName(theme.getName());

    addHeaderFooter(presentation, headerMessage, portrait);

    // Create a one-page document
    //
    LeanPage pageOne = LeanPage.getA4(1, portrait);
    presentation.getPages().add(pageOne);

    // Get a bunch of rows in the output.
    //
    ILeanConnector sampleRowsConnector = new LeanSampleDataConnector(rowCount);
    LeanConnector sampleRows = new LeanConnector(CONNECTOR_SAMPLE_ROWS, sampleRowsConnector);
    presentation.getConnectors().add(sampleRows);

    return presentation;
  }

  protected static void addHeaderFooter(
      LeanPresentation presentation, String headerMessage, boolean portrait) {
    // Add a header with a logo at the top of the page
    //
    LeanPage header = LeanPage.getHeaderFooter(true, portrait, 50);
    header.getComponents().add(createHeaderLabelComponent(headerMessage));
    header.getComponents().add(createPresentationNameLabelComponent());
    header.getComponents().add(createHeaderImageComponent());
    presentation.setHeader(header);

    // Add a footer with a single label at the bottom of the page.
    //
    LeanPage footer = LeanPage.getHeaderFooter(false, portrait, 25);
    footer.getComponents().add(createPageNumberLabelComponent());
    footer.getComponents().add(createSysdateLabelComponent());
    presentation.setFooter(footer);
  }

  protected static LeanComponent createHeaderImageComponent() {
    LeanSvgComponent leanLabel = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
    leanLabel.setBorder(true);
    LeanComponent imageComponent = new LeanComponent("Logo", leanLabel);
    imageComponent.setLayout(new LeanLayoutBuilder().top().right().bottom().build());
    return imageComponent;
  }

  protected static LeanComponent createHeaderLabelComponent(String headerMessage) {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel(headerMessage);
    label.setBorder(false);
    LeanComponent labelComponent = new LeanComponent(HEADER_MESSAGE_LABEL, label);
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.CENTER));
    labelLayout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.CENTER));
    labelComponent.setLayout(labelLayout);
    return labelComponent;
  }

  protected static LeanComponent createPresentationNameLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel("${PRESENTATION_NAME}");
    label.setBorder(false);
    LeanComponent labelComponent = new LeanComponent("PresentationName", label);
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT));
    labelLayout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.CENTER));
    labelComponent.setLayout(labelLayout);
    return labelComponent;
  }

  protected static LeanComponent createPageNumberLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel("Page #${PAGE_NUMBER}");
    label.setBorder(false);
    LeanComponent labelComponent = new LeanComponent("FooterLabel", label);
    labelComponent.setLayout(new LeanLayoutBuilder().left().bottom().build());
    return labelComponent;
  }

  protected static LeanComponent createSysdateLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel("${SYSTEM_DATE}");
    label.setBorder(false);
    LeanComponent labelComponent = new LeanComponent("SystemDate", label);
    labelComponent.setLayout(new LeanLayoutBuilder().right().bottom().build());
    return labelComponent;
  }

  public LeanPresentation[] getAvailablePresentations() throws Exception {
    int nr = 1;
    return new LeanPresentation[] {
      new BarChartPresentationUtil(metadataProvider, variables)
          .createBarChartPresentation(100 * nr),
      new BarChartPresentationUtil(metadataProvider, variables)
          .createStackedBarChartPresentation(100 * nr),
      new LabelPresentationUtil(metadataProvider, variables).createLabelPresentation(100 * nr++),
      new LineChartPresentationUtil(metadataProvider, variables)
          .createLineChartPresentation(100 * nr++),
      new LineChartPresentationUtil(metadataProvider, variables)
          .createLineChartSeriesPresentation(100 * nr++),
      new LineChartPresentationUtil(metadataProvider, variables)
          .createLineChartNoLabelsPresentation(100 * nr++),
      new ComboPresentationUtil(metadataProvider, variables).createComboPresentation(100 * nr++),
      new CompositePresentationUtil(metadataProvider, variables)
          .createSimpleCompositePresentation(100 * nr++),
      new CrosstabPresentationUtil(metadataProvider, variables)
          .createCrosstabPresentation(100 * nr++),
      new CrosstabPresentationUtil(metadataProvider, variables)
          .createCrosstabPresentationOnlyVerticalDimensions(100 * nr++),
      new CrosstabPresentationUtil(metadataProvider, variables)
          .createCrosstabPresentationOnlyHorizontalDimensions(100 * nr++),
      new CrosstabPresentationUtil(metadataProvider, variables)
          .createCrosstabPresentationOnlyFacts(100 * nr++),
      new GroupCompositePresentationUtil(metadataProvider, variables)
          .createGroupCompositePresentation(100 * nr++),
      new GroupPresentationUtil(metadataProvider, variables)
          .createSimpleGroupedLabelPresentation(100 * nr++),
    };
  }

  /**
   * Gets metadataProvider
   *
   * @return value of metadataProvider
   */
  public IHopMetadataProvider getMetadataProvider() {
    return metadataProvider;
  }

  /** @param metadataProvider The metadataProvider to set */
  public void setMetadataProvider(IHopMetadataProvider metadataProvider) {
    this.metadataProvider = metadataProvider;
  }
}
