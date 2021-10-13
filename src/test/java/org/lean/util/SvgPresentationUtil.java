package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.component.types.svg.LeanSvgComponent;
import org.lean.presentation.component.types.svg.ScaleType;
import org.lean.presentation.layout.LeanLayoutBuilder;
import org.lean.presentation.page.LeanPage;

public class SvgPresentationUtil extends BasePresentationUtil {

  public SvgPresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public LeanPresentation createSvgPresentation(int nr) throws Exception {
    // Landscape A4 presentation
    //
    LeanPresentation presentation =
        createBasePresentation(
            "SVG (" + nr + ")",
            "SVG " + nr + " description",
            1,
            "A few SVG images on specific locations on the page",
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
      LeanLabelComponent label = new LeanLabelComponent();
      label.setLabel("SVG layout test");
      LeanComponent labelComponent = new LeanComponent("label-top-center", label);
      labelComponent.setLayout(new LeanLayoutBuilder().top().left(50, 0).build());
      pageOne.getComponents().add(labelComponent);
    }

    // Scale label to 50 high to the top right
    //
    {
      LeanSvgComponent svg = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
      svg.setBorder(true);
      LeanComponent svgComponent = new LeanComponent("logo-top-right-100", svg);
      svgComponent.setLayout(new LeanLayoutBuilder().top().right().bottomFromTop(0, 50).build());
      pageOne.getComponents().add(svgComponent);
    }

    // Logo at the top left...
    //
    {
      LeanSvgComponent svg = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
      svg.setBorder( true );
      LeanComponent svgComponent = new LeanComponent("logo-top-left", svg);
      svgComponent.setLayout(new LeanLayoutBuilder().top().left().build());
      pageOne.getComponents().add(svgComponent);
    }

    // Logo at the top right...
    //
    {
      LeanSvgComponent svg = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
      svg.setBorder( true );
      LeanComponent svgComponent = new LeanComponent("logo-top-right", svg);
      svgComponent.setLayout(new LeanLayoutBuilder().topFromBottom("logo-top-right-100", 0, 5).right(0,0).build());
      pageOne.getComponents().add(svgComponent);
    }

    // Logo at the bottom left...
    //
    {
      LeanSvgComponent svg = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
      LeanComponent svgComponent = new LeanComponent("logo-bottom-left", svg);
      svgComponent.setLayout(new LeanLayoutBuilder().bottom().left().build());
      pageOne.getComponents().add(svgComponent);
    }

    // Logo at the bottom right...
    //
    {
      LeanSvgComponent svg = new LeanSvgComponent("lean-logo.svg", ScaleType.MIN);
      LeanComponent svgComponent = new LeanComponent("logo-bottom-right", svg);
      svgComponent.setLayout(new LeanLayoutBuilder().bottom().right().build());
      pageOne.getComponents().add(svgComponent);
    }

    return presentation;
  }
}
