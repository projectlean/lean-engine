package org.lean.util;

import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.types.sampledata.LeanSampleDataConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;

public class BasePresentationUtil {

  public static final String HEADER_MESSAGE_LABEL = "HeaderLabel";
  public static String CONNECTOR_SAMPLE_ROWS = "Sample rows";

  public static LeanPresentation[] getAvailablePresentations() throws Exception {
    int nr = 1;
    return new LeanPresentation[] {
      LabelPresentationUtil.createLabelPresentation( 500 * nr++ ),
      LineChartPresentationUtil.createLineChartPresentation( 500 * nr++ ),
      LineChartPresentationUtil.createLineChartSeriesPresentation( 500 * nr++ ),
      LineChartPresentationUtil.createLineChartNoLabelsPresentation( 500 * nr++ ),
      ComboPresentationUtil.createComboPresentation( 500 * nr++ ),
      CompositePresentationUtil.createSimpleCompositePresentation( 500 * nr++ ),
      CrosstabPresentationUtil.createCrosstabPresentation( 500 * nr++ ),
      CrosstabPresentationUtil.createCrosstabPresentationOnlyVerticalDimensions( 500 * nr++ ),
      CrosstabPresentationUtil.createCrosstabPresentationOnlyHorizontalDimensions( 500 * nr++ ),
      CrosstabPresentationUtil.createCrosstabPresentationOnlyFacts( 500 * nr++ ),
      GroupCompositePresentationUtil.createGroupCompositePresentation( 500 * nr++ ),
      GroupPresentationUtil.createSimpleGroupedLabelPresentation( 500 * nr++ ),
    };
  }


  protected static LeanPresentation createBasePresentation( String name, String description, int rowCount, String headerMessage ) {
    return createBasePresentation( name, description, rowCount, headerMessage, false );
  }


  protected static LeanPresentation createBasePresentation( String name, String description, int rowCount, String headerMessage, boolean portrait ) {
    LeanPresentation presentation = new LeanPresentation();
    presentation.setName( name );
    presentation.setDescription( description );

    // Add a default theme scheme.
    // It has information about background colors, chart colors and so on.
    //
    LeanTheme theme = LeanTheme.getDefault();
    presentation.getThemes().add( theme );

    // Setting a default theme allows all components in the presentation to use it
    //
    presentation.setDefaultThemeName( theme.getName() );

    addHeaderFooter( presentation, headerMessage, portrait );

    // Create a one-page document
    //
    LeanPage pageOne = LeanPage.getA4( 1, portrait );
    presentation.getPages().add( pageOne );

    // Get a bunch of rows in the output.
    //
    ILeanConnector sampleRowsConnector = new LeanSampleDataConnector( rowCount );
    LeanConnector sampleRows = new LeanConnector( CONNECTOR_SAMPLE_ROWS, sampleRowsConnector );
    presentation.getConnectors().add( sampleRows );

    return presentation;
  }

  protected static void addHeaderFooter( LeanPresentation presentation, String headerMessage, boolean portrait ) {
    // Add a header with a logo at the top of the page
    //
    LeanPage header = LeanPage.getHeaderFooter( portrait, 50 );
    header.getComponents().add( createHeaderImageComponent() );
    header.getComponents().add( createHeaderLabelComponent( headerMessage ) );
    header.getComponents().add( createPresentationNameLabelComponent() );
    presentation.setHeader( header );

    // Add a footer with a single label at the bottom of the page.
    //
    LeanPage footer = LeanPage.getHeaderFooter( portrait, 25 );
    footer.getComponents().add( createPageNumberLabelComponent() );
    footer.getComponents().add( createSysdateLabelComponent() );
    presentation.setFooter( footer );
  }

  /*
  protected static LeanComponent createHeaderImageComponent() {
    LeanSvgComponent leanImage = new LeanSvgComponent( "lean-logo.png" );
    leanImage.setBorder( false );
    leanImage.setScalePercent( "5" );
    LeanComponent imageComponent = new LeanComponent( "Logo", leanImage );
    imageComponent.setSize( null );
    LeanLayout imageLayout = new LeanLayout();
    imageLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) ); // Top of the page
    imageLayout.setRight( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.RIGHT ) ); // Right of the page
    imageComponent.setLayout( imageLayout );

    return imageComponent;
  } */

  protected static LeanComponent createHeaderImageComponent() {
    LeanLabelComponent leanLabel = new LeanLabelComponent();
    leanLabel.setLabel( "Lean" );
    leanLabel.setBorder( false );
    leanLabel.setDefaultColor( new LeanColorRGB( 255, 0, 0 ) );
    LeanComponent imageComponent = new LeanComponent( "Logo", leanLabel );
    imageComponent.setSize( null );
    LeanLayout imageLayout = new LeanLayout();
    imageLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) ); // Top of the page
    imageLayout.setRight( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.RIGHT ) ); // Right of the page
    imageComponent.setLayout( imageLayout );

    return imageComponent;
  }

  protected static LeanComponent createHeaderLabelComponent( String headerMessage ) {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel( headerMessage );
    label.setBorder( false );
    LeanComponent labelComponent = new LeanComponent( HEADER_MESSAGE_LABEL, label );
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.CENTER ) );
    labelLayout.setTop( new LeanAttachment( "Logo", 0, 0, LeanAttachment.Alignment.CENTER ) );
    labelComponent.setLayout( labelLayout );
    return labelComponent;
  }

  protected static LeanComponent createPresentationNameLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel( "${PRESENTATION_NAME}" );
    label.setBorder( false );
    LeanComponent labelComponent = new LeanComponent( "PresentationName", label );
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    labelLayout.setTop( new LeanAttachment( "Logo", 0, 0, LeanAttachment.Alignment.CENTER ) );
    labelComponent.setLayout( labelLayout );
    return labelComponent;
  }


  protected static LeanComponent createPageNumberLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel( "Page #${PAGE_NUMBER}" );
    label.setBorder( false );
    LeanComponent labelComponent = new LeanComponent( "FooterLabel", label );
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    labelLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    labelComponent.setLayout( labelLayout );
    return labelComponent;
  }

  protected static LeanComponent createSysdateLabelComponent() {
    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel( "${SYSTEM_DATE}" );
    label.setBorder( false );
    LeanComponent labelComponent = new LeanComponent( "SystemDate", label );
    LeanLayout labelLayout = new LeanLayout();
    labelLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    labelLayout.setRight( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.RIGHT ) );
    labelComponent.setLayout( labelLayout );
    return labelComponent;
  }
}
