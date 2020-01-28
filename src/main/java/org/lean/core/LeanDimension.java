package org.lean.core;

public class LeanDimension extends LeanColumn {

  public LeanDimension() {
    super();
  }

  public LeanDimension( String columnName ) {
    super( columnName );
  }

  public LeanDimension( String columnName, String headerValue, LeanHorizontalAlignment horizontalAlignment, LeanVerticalAlignment verticalAlignment) {
    super( columnName, headerValue, horizontalAlignment, verticalAlignment );

  }

  public LeanDimension( LeanDimension d ) {
    super(d);
  }
}
