package org.lean.presentation.component.types.crosstab;

import org.apache.hop.core.row.IValueMeta;
import org.lean.core.LeanColumn;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanTextGeometry;
import org.lean.core.LeanVerticalAlignment;

public class CellInfo {
  public LeanTextGeometry geometry;
  public String text;
  public LeanColumn column;
  public LeanVerticalAlignment verticalAlignment;
  public LeanHorizontalAlignment horizontalAlignment;
  public IValueMeta valueMeta;
  public Object valueData;

  public CellInfo() {}

  public CellInfo(
      LeanTextGeometry geometry,
      String text,
      LeanColumn column,
      LeanVerticalAlignment verticalAlignment,
      LeanHorizontalAlignment horizontalAlignment) {
    this.geometry = geometry;
    this.text = text;
    this.column = column;
    this.verticalAlignment = verticalAlignment;
    this.horizontalAlignment = horizontalAlignment;
  }
}
