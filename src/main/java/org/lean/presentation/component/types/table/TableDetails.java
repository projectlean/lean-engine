package org.lean.presentation.component.types.table;

import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.core.row.IRowMeta;
import org.lean.core.LeanTextGeometry;

import java.util.ArrayList;
import java.util.List;

public class TableDetails {
  public List<RowMetaAndData> rows;
  public List<List<LeanTextGeometry>> columnSizesList;
  public List<List<String>> rowStringsList;
  public List<Integer> maxWidths;
  public List<Integer> maxHeights;
  public IRowMeta rowMeta;
  public int totalWidth;
  public int totalHeight;

  public TableDetails() {
    rows = new ArrayList<>();
    columnSizesList = new ArrayList<>();
    rowStringsList = new ArrayList<>();
    maxWidths = new ArrayList<>();
    maxHeights = new ArrayList<>();
    maxHeights = new ArrayList<>();
  }
}
