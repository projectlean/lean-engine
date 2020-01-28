package org.lean.core.draw;

import org.lean.core.LeanColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawnContext {

  private List<LeanColumn> dimensions;
  private String value;

  public DrawnContext() {
    this.dimensions = new ArrayList<>(  );
  }

  public DrawnContext( String value ) {
    this();
    this.value = value;
  }

  public DrawnContext( List<LeanColumn> dimensions, String value ) {
    this.dimensions = dimensions;
    this.value = value;
  }

  public DrawnContext(String value, LeanColumn...dimensions) {
    this(value);
    this.dimensions.addAll( Arrays.asList(dimensions) );
  }
}
