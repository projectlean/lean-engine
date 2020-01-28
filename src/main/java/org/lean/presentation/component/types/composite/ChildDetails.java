package org.lean.presentation.component.types.composite;

import org.lean.core.LeanSize;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.layout.LeanLayoutResults;

public class ChildDetails {
  public LeanSize childExpectedSize;
  public LeanComponent childComponent;

  public ChildDetails() {
  }

  public ChildDetails( LeanLayoutResults childLayoutResults, LeanSize childExpectedSize, LeanComponent childComponent ) {
    this.childExpectedSize = childExpectedSize;
    this.childComponent = childComponent;
  }
}
