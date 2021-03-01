package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;

public class LabelPresentationUtil extends BasePresentationUtil {

  public LabelPresentationUtil( IHopMetadataProvider metadataProvider, IVariables variables ) {
    super( metadataProvider, variables );
  }

  public LeanPresentation createLabelPresentation( int nr ) throws LeanException {
    LeanPresentation presentation = createBasePresentation(
      "Label (" + nr + ")",
      "Label " + nr + " description",
      1,
      "A single label top/left of the page",
      false
    );

    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel( "<_ö gpĨ\"dsfsdf\nsdfljsldfsldjf\n   ewioruwero>" );
    label.setDefaultFont( new LeanFont("Courier", "40", true, true) );
    label.setHorizontalAlignment( LeanHorizontalAlignment.LEFT );
    label.setVerticalAlignment( LeanVerticalAlignment.TOP );
    label.setBorder( true );
    label.setDefaultColor( new LeanColorRGB( 0, 140, 194 ) );
    label.setBorderColor( new LeanColorRGB( 80, 80, 80 ) );
    label.setBackGroundColor( new LeanColorRGB( 200, 200, 200 ) );

    LeanComponent label1 = new LeanComponent("Label1", label);
    label1.setLayout( new LeanLayout( 25, 5) );
    label1.setSize( null ); // Calculate dynamically...

    presentation.getPages().get(0).getComponents().add(label1);

    return presentation;
  }
}
