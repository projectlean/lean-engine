package org.lean.util;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.AggregationMethod;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanSortMethod;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.composite.LeanCompositeComponent;
import org.lean.presentation.component.types.crosstab.LeanCrosstabComponent;
import org.lean.presentation.component.types.group.LeanGroupComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

import java.util.Arrays;
import java.util.Collections;

public class GroupCompositePresentationUtil extends BasePresentationUtil {

  public static final String COMPONENT_NAME_COMPOSITE1 = "Composite1";
  public static final String COMPONENT_NAME_LABEL1 = "Label1";
  public static final String COMPONENT_NAME_CROSSTAB1 = "Crosstab1";

  public GroupCompositePresentationUtil( IHopMetadataProvider metadataProvider ) {
    super( metadataProvider );
  }

  public LeanPresentation createGroupCompositePresentation( int nr ) throws Exception {

    LeanPresentation presentation = createBasePresentation(
      "Group composite (" + nr + ")",
      "Group composite " + nr + " description",
      2000,
      "A group repeating a composite with a label and a crosstab, data filtering"
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    LeanCompositeComponent compositeComponent = new LeanCompositeComponent();

    {
      // The Label to repeat in the group component, top of the composite
      //
      LeanLabelComponent labelComponent = new LeanLabelComponent();
      labelComponent.setLabel( "Country: ${country}" );
      labelComponent.setDefaultFont( new LeanFont( "Courier", "48", false, false ) );
      labelComponent.setHorizontalAlignment( LeanHorizontalAlignment.CENTER );
      labelComponent.setVerticalAlignment( LeanVerticalAlignment.TOP );
      labelComponent.setBorder( true );
      labelComponent.setDefaultColor( new LeanColorRGB( 0, 140, 194 ) );
      labelComponent.setBorderColor( new LeanColorRGB( 240, 240, 240) );
      labelComponent.setBackGroundColor( new LeanColorRGB( 200, 200, 200 ) );


      LeanComponent label = new LeanComponent( COMPONENT_NAME_LABEL1, labelComponent );
      LeanLayout labelLayout = new LeanLayout();
      // null below means: relative to parent (page or composite)
      //
      labelLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
      labelLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
      label.setLayout( labelLayout );
      // Dynamic size
      label.setSize( null );
      compositeComponent.getChildren().add(label);
    }

    {
      // Add a crosstab below the label
      //
      LeanCrosstabComponent ctc = new LeanCrosstabComponent( CONNECTOR_SAMPLE_ROWS );
      ctc.setHorizontalDimensions( Arrays.asList(
        new LeanDimension( "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE ),
        new LeanDimension( "important", "?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE )
      ) );
      ctc.setVerticalDimensions( Arrays.asList(
        new LeanDimension( "name", "Customer", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE )
      ) );
      LeanFact sumFact = new LeanFact( "random", "Sum", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE, AggregationMethod.SUM, "0.000" );
      sumFact.setHorizontalAggregation( true );
      sumFact.setHorizontalAggregationHeader( "Total Sum" );
      sumFact.setHeaderHorizontalAlignment( LeanHorizontalAlignment.CENTER );
      sumFact.setHeaderVerticalAlignment( LeanVerticalAlignment.MIDDLE );
      LeanFact countFact = new LeanFact( "name", "Count", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE, AggregationMethod.COUNT, "0" );
      countFact.setHorizontalAggregation( true );
      countFact.setHorizontalAggregationHeader( "Total Count" );
      countFact.setHeaderHorizontalAlignment( LeanHorizontalAlignment.CENTER );
      countFact.setHeaderVerticalAlignment( LeanVerticalAlignment.MIDDLE );
      ctc.setFacts( Arrays.asList( sumFact, countFact ) );
      ctc.setBackground( false );
      ctc.setBorder( false );
      ctc.setHorizontalMargin( 3 );
      ctc.setVerticalMargin( 2 );
      ctc.setEvenHeights( true );
      ctc.setHeaderOnEveryPage( true );
      ctc.setShowingVerticalTotals( true );
      ctc.setShowingHorizontalTotals( true );

      LeanComponent ct = new LeanComponent( COMPONENT_NAME_CROSSTAB1, ctc );
      LeanLayout layout = new LeanLayout();
      layout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
      layout.setTop( new LeanAttachment( COMPONENT_NAME_LABEL1, 0, 10, LeanAttachment.Alignment.BOTTOM ) );

      ct.setLayout( layout );
      ct.setSize( null );

      compositeComponent.getChildren().add( ct );
    }

    LeanComponent composite = new LeanComponent( COMPONENT_NAME_COMPOSITE1, compositeComponent );
    LeanLayout compositeLayout = new LeanLayout();
    compositeLayout.setLeft( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    compositeLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    composite.setLayout( compositeLayout );


    // Create a group and throw the composite in there.
    //
    LeanGroupComponent groupComponent = new LeanGroupComponent(
      CONNECTOR_SAMPLE_ROWS,
      Collections.singletonList(new LeanColumn( "country", "Country", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP )),
      Collections.singletonList(new LeanSortMethod( LeanSortMethod.Type.NATIVE_VALUE, true )),
      true, // distinct values from all rows
      composite, // The component to repeat
      10 // Margin
    );
    LeanComponent leanGroupComponent = new LeanComponent( "Group", groupComponent );
    LeanLayout groupLayout = new LeanLayout(  );
    groupLayout.setLeft( new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT ) );
    groupLayout.setTop( new LeanAttachment( null, 0, 0, LeanAttachment.Alignment.TOP ) );
    leanGroupComponent.setLayout( groupLayout );
    leanGroupComponent.setSize( null );

    pageOne.getComponents().add(leanGroupComponent);

    return presentation;
  }


}
