package org.lean.presentation.component.types.composite;

import org.lean.core.LeanSize;

import java.util.ArrayList;
import java.util.List;

public class CompositeDetails {

  public List<ChildDetails> childDetails;
  public LeanSize size;

  public CompositeDetails() {
    this.childDetails = new ArrayList<>(  );
  }
}
