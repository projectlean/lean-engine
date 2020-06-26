package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.ArrayList;
import java.util.List;

public class LeanSortMethod {

  public enum Type {
    NATIVE_VALUE,
    STRING_ALPHA,
    STRING_NUMERIC,
    STRING_CUSTOM;
  }

  @HopMetadataProperty
  private Type type;

  @HopMetadataProperty
  private boolean ascending;

  @HopMetadataProperty
  private List<String> customOrder;

  public LeanSortMethod() {
    type = Type.NATIVE_VALUE;
    ascending = true;
    customOrder = new ArrayList<>();
  }

  public LeanSortMethod( Type type, boolean ascending ) {
    this();
    this.type = type;
    this.ascending = ascending;
  }

  public LeanSortMethod( Type type, boolean ascending, List<String> customOrder ) {
    this.type = type;
    this.ascending = ascending;
    this.customOrder = customOrder;
  }

  public LeanSortMethod( LeanSortMethod m ) {
    this();
    this.type = m.type;
    this.ascending = m.ascending;
    for ( String s : m.customOrder ) {
      this.customOrder.add( s );
    }
  }

  /**
   * Gets type
   *
   * @return value of type
   */
  public Type getType() {
    return type;
  }

  /**
   * @param type The type to set
   */
  public void setType( Type type ) {
    this.type = type;
  }

  /**
   * Gets ascending
   *
   * @return value of ascending
   */
  public boolean isAscending() {
    return ascending;
  }

  /**
   * @param ascending The ascending to set
   */
  public void setAscending( boolean ascending ) {
    this.ascending = ascending;
  }

  /**
   * Gets customOrder
   *
   * @return value of customOrder
   */
  public List<String> getCustomOrder() {
    return customOrder;
  }

  /**
   * @param customOrder The customOrder to set
   */
  public void setCustomOrder( List<String> customOrder ) {
    this.customOrder = customOrder;
  }
}
