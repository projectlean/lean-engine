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
import org.lean.presentation.layout.LeanLayoutBuilder;
import org.lean.presentation.page.LeanPage;

public class LabelPresentationUtil extends BasePresentationUtil {

  public LabelPresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public LeanPresentation createLabelPresentation(int nr) throws LeanException {
    LeanPresentation presentation =
        createBasePresentation(
            "Label (" + nr + ")",
            "Label " + nr + " description",
            1,
            "A single label top/left of the page",
            false);

    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel("<_ö gpĨ\"dsfsdf\nsdfljsldfsldjf\n   ewioruwero>");
    label.setDefaultFont(new LeanFont("Courier", "40", true, true));
    label.setHorizontalAlignment(LeanHorizontalAlignment.LEFT);
    label.setVerticalAlignment(LeanVerticalAlignment.TOP);
    label.setBorder(true);
    label.setDefaultColor(new LeanColorRGB(0, 140, 194));
    label.setBorderColor(new LeanColorRGB(80, 80, 80));
    label.setBackGroundColor(new LeanColorRGB(200, 200, 200));

    LeanComponent label1 = new LeanComponent("Label1", label);
    label1.setLayout(new LeanLayout(25, 5));

    presentation.getPages().get(0).getComponents().add(label1);

    return presentation;
  }

  public LeanPresentation createLabelsPresentation(int nr) throws LeanException {
    LeanPresentation presentation =
        createBasePresentation(
            "Labels (" + nr + ")",
            "Labels " + nr + " description",
            1,
            "Labels placed all over the page",
            false);

    // Remove the header and footer, not needed here...
    //
    presentation.setHeader(null);
    presentation.setFooter(null);

    LeanPage pageOne = presentation.getPages().get(0);
    pageOne.setTopMargin(25);
    pageOne.setLeftMargin(25);
    pageOne.setBottomMargin(25);
    pageOne.setRightMargin(25);

    // Label at the top center...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("top-center");
      LeanComponent labelComponent = new LeanComponent("label-top-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top().left(50, 0).build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the bottom center...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("bottom-center");
      LeanComponent labelComponent = new LeanComponent("label-bottom-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().bottom().left(50, 0).build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the left center...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("left-center");
      LeanComponent labelComponent = new LeanComponent("label-left-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top(50, 0).left().build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the right center...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("right-center");
      LeanComponent labelComponent = new LeanComponent("label-right-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top(50, 0).right().build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the center...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("center");
      LeanComponent labelComponent = new LeanComponent("label-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().left(50, 0).top(50, 0).build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the top left...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("top-left");
      LeanComponent labelComponent = new LeanComponent("label-top-left", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top().left().build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the top right...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("top-right");
      LeanComponent labelComponent = new LeanComponent("label-top-right", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top().right().build());
      pageOne.getComponents().add(labelComponent);
    }

    // Label at the bottom left...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("bottom-left");
      LeanComponent labelComponent = new LeanComponent("label-bottom-left", label);
      labelComponent.setLayout(new LeanLayoutBuilder().bottom().left().build());
      pageOne.getComponents().add(labelComponent);
    }

    // Logo at the bottom right...
    //
    {
      LeanLabelComponent label = new LeanLabelComponent("bottom-right");
      LeanComponent labelComponent = new LeanComponent("label-bottom-right", label);
      labelComponent.setLayout(new LeanLayoutBuilder().bottom().right().build());
      pageOne.getComponents().add(labelComponent);
    }

    return presentation;
  }
}
