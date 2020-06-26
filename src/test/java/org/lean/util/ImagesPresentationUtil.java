package org.lean.util;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.component.types.svg.LeanSvgComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

public class ImagesPresentationUtil extends BasePresentationUtil {


  public ImagesPresentationUtil( IHopMetadataProvider metadataProvider ) {
    super( metadataProvider );
  }

  public LeanPresentation createImagesPresentation( int nr ) throws Exception {
    // Landscape A4 presentation
    //
    LeanPresentation presentation = createBasePresentation(
      "Images (" + nr + ")",
      "Images " + nr + " description",
      10,
      "A few static SVG images with labels",
      false
    );

    // Remove the header and footer, not needed here...
    //
    presentation.setHeader( null );
    presentation.setFooter( null );

    LeanPage pageOne = presentation.getPages().get( 0 );
    pageOne.setWidth( 800 );
    pageOne.setHeight( 250 );

    // Add the tap image...
    //
    LeanSvgComponent hand = new LeanSvgComponent( "pointing_hand_cursor_vector.svg" );
    hand.setScalePercent( "50" );
    LeanComponent handComponent = new LeanComponent( "Hand", hand );
    LeanLayout handLayout = new LeanLayout();
    handLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    handLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.CENTER ) );
    handComponent.setLayout( handLayout );
    pageOne.getComponents().add( handComponent );


    // Tap or click anywhere
    //
    LeanLabelComponent tap = new LeanLabelComponent();
    tap.setLabel( "Tap or click anywhere!" );
    tap.setHorizontalAlignment( LeanHorizontalAlignment.CENTER );
    tap.setDefaultFont( new LeanFont( "Arial", "40", true, false ) );

    LeanComponent tapComponent = new LeanComponent( "Tap", tap );
    LeanLayout tapLayout = new LeanLayout(
      new LeanAttachment( "Hand", 0, 0, LeanAttachment.Alignment.RIGHT ), // LEFT
      null,
      new LeanAttachment( "Hand", 0, 0, LeanAttachment.Alignment.CENTER ),
      null
    );
    tapComponent.setLayout( tapLayout );
    pageOne.getComponents().add( tapComponent );

    return presentation;
  }


}
