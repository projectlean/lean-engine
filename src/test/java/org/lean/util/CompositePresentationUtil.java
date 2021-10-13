package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.composite.LeanCompositeComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

public class CompositePresentationUtil extends BasePresentationUtil {

  public static final String COMPONENT_NAME_COMPOSITE1 = "Composite1";
  public static final String COMPONENT_NAME_LABEL1 = "Label1";
  public static final String COMPONENT_NAME_LABEL2 = "Label2";

  public CompositePresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public LeanPresentation createSimpleCompositePresentation(int nr) throws Exception {

    LeanPresentation presentation =
        createBasePresentation(
            "Composite simple (" + nr + ")",
            "Composite simple " + nr + " description",
            100,
            "Simple composite with 2 labels, 2nd label below 1st, right aligned");

    LeanPage pageOne = presentation.getPages().get(0);

    LeanCompositeComponent compositeComponent = new LeanCompositeComponent();

    // Add 2 labels to the composite
    //
    {
      LeanLabelComponent label1Component = new LeanLabelComponent();
      label1Component.setLabel("One 1 One 1 One 1 One 1 One 1 One 1 One 1 One 1 One 1");
      label1Component.setDefaultFont(new LeanFont("Hack", "24", false, false));
      label1Component.setHorizontalAlignment(LeanHorizontalAlignment.CENTER);
      label1Component.setVerticalAlignment(LeanVerticalAlignment.TOP);
      label1Component.setBorder(true);
      LeanComponent label1 = new LeanComponent(COMPONENT_NAME_LABEL1, label1Component);
      LeanLayout label1Layout = new LeanLayout();
      // null below means: relative to parent (page or composite)
      //
      label1Layout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT));
      label1Layout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP));
      label1.setLayout(label1Layout);

      compositeComponent.getChildren().add(label1);
    }

    {
      LeanLabelComponent label2Component = new LeanLabelComponent();
      label2Component.setLabel("Two 2 Two 2 Two 2");
      label2Component.setDefaultFont(new LeanFont("Hack", "18", false, false));
      label2Component.setHorizontalAlignment(LeanHorizontalAlignment.RIGHT);
      label2Component.setVerticalAlignment(LeanVerticalAlignment.TOP);
      label2Component.setBorder(true);
      LeanComponent label2 = new LeanComponent(COMPONENT_NAME_LABEL2, label2Component);
      LeanLayout label2Layout = new LeanLayout();
      // null below means: relative to parent (page or composite)
      //
      label2Layout.setRight(
          new LeanAttachment(COMPONENT_NAME_LABEL1, 0, 0, LeanAttachment.Alignment.RIGHT));
      label2Layout.setTop(
          new LeanAttachment(COMPONENT_NAME_LABEL1, 0, 5, LeanAttachment.Alignment.BOTTOM));
      label2.setLayout(label2Layout);

      compositeComponent.getChildren().add(label2);
    }

    LeanComponent composite = new LeanComponent(COMPONENT_NAME_COMPOSITE1, compositeComponent);
    LeanLayout compositeLayout = new LeanLayout();
    compositeLayout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT));
    compositeLayout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP));
    composite.setLayout(compositeLayout);

    pageOne.getComponents().add(composite);

    return presentation;
  }
}
