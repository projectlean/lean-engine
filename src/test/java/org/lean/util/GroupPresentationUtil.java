package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanSortMethod;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.group.LeanGroupComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

import java.util.Collections;

public class GroupPresentationUtil extends BasePresentationUtil {


  public static final String COMPONENT_NAME_LABEL = "Label1";

  public GroupPresentationUtil( IHopMetadataProvider metadataProvider, IVariables variables ) {
    super( metadataProvider, variables );
  }

  public LeanPresentation createSimpleGroupedLabelPresentation( int nr ) throws Exception {

    LeanPresentation presentation = createBasePresentation(
      "Group label (" + nr + ")",
      "Group label " + nr + " description",
      100,
      "A group repeating labels with country names"
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    // The Label to repeat in the group component
    //
    LeanLabelComponent labelComponent = new LeanLabelComponent();
    labelComponent.setLabel( "Country: ${country}" );
    labelComponent.setDefaultFont( new LeanFont( "Courier", "48", false, false ) );
    labelComponent.setHorizontalAlignment( LeanHorizontalAlignment.CENTER );
    labelComponent.setVerticalAlignment( LeanVerticalAlignment.TOP );
    labelComponent.setBorder( true );
    labelComponent.setDefaultColor( new LeanColorRGB( 0, 140, 194 ) );
    labelComponent.setBorderColor( new LeanColorRGB( 240, 240, 240 ) );
    labelComponent.setBackGroundColor( new LeanColorRGB( 200, 200, 200 ) );

    LeanComponent label = new LeanComponent( COMPONENT_NAME_LABEL, labelComponent );
    LeanLayout labelLayout = new LeanLayout();
    // null below means: relative to parent (page or composite)
    //
    labelLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    labelLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    label.setLayout( labelLayout );
    // Dynamic size
    label.setSize( null );


    // Read the distinct countries from connector called BasePresentationUtil.CONNECTOR_SAMPLE_ROWS
    //
    LeanGroupComponent groupComponent = new LeanGroupComponent(
      CONNECTOR_SAMPLE_ROWS,
      Collections.singletonList( new LeanColumn( "country", "Country", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP ) ),
      Collections.singletonList( new LeanSortMethod( LeanSortMethod.Type.NATIVE_VALUE, true ) ),
      true, // distinct values from all rows
      label, // The component to repeat
      5 // Margin
    );
    LeanComponent leanGroupComponent = new LeanComponent( "Group", groupComponent );
    LeanLayout groupLayout = new LeanLayout();
    groupLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    groupLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    leanGroupComponent.setLayout( groupLayout );
    leanGroupComponent.setSize( null );

    pageOne.getComponents().add( leanGroupComponent );


    return presentation;
  }


}
