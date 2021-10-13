package org.lean.presentation.component.types.group;

import org.apache.hop.core.RowMetaAndData;
import org.lean.core.LeanSize;

import java.util.ArrayList;
import java.util.List;

public class GroupDetails {

  public List<RowMetaAndData> rows;
  public List<GroupRowDetails> rowDetails;
  public LeanSize size;

  public GroupDetails() {
    this.rows = new ArrayList<>();
    this.rowDetails = new ArrayList<>();
  }
}
