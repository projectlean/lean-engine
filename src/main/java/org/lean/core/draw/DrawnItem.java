package org.lean.core.draw;

import org.lean.core.LeanGeometry;

import java.util.Objects;

public class DrawnItem {

  private String componentName;
  private int partNumber;
  private String type;
  private String name;
  private int rowNr;
  private int colNr;
  private LeanGeometry geometry;
  private DrawnContext context;

  public DrawnItem() {
  }

  public DrawnItem( String componentName, int partNumber, String type, String name, int rowNr, int colNr, LeanGeometry geometry, DrawnContext context ) {
    this.componentName = componentName;
    this.partNumber = partNumber;
    this.type = type;
    this.name = name;
    this.rowNr = rowNr;
    this.colNr = colNr;
    this.geometry = geometry;
    this.context = context;
  }

  public DrawnItem( String componentName, int partNumber, String type, String name, int rowNr, int colNr, LeanGeometry geometry ) {
    this(componentName, partNumber, type, name, rowNr, colNr, geometry, null);
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    DrawnItem drawItem = (DrawnItem) o;
    return partNumber == drawItem.partNumber &&
      rowNr == drawItem.rowNr &&
      colNr == drawItem.colNr &&
      Objects.equals( componentName, drawItem.componentName ) &&
      Objects.equals( type, drawItem.type ) &&
      Objects.equals( name, drawItem.name ) &&
      Objects.equals( geometry, drawItem.geometry );
  }

  @Override public int hashCode() {
    return Objects.hash( componentName, partNumber, type, name, rowNr, colNr, geometry );
  }

  /**
   * Gets componentName
   *
   * @return value of componentName
   */
  public String getComponentName() {
    return componentName;
  }

  /**
   * @param componentName The componentName to set
   */
  public void setComponentName( String componentName ) {
    this.componentName = componentName;
  }

  /**
   * Gets partNumber
   *
   * @return value of partNumber
   */
  public int getPartNumber() {
    return partNumber;
  }

  /**
   * @param partNumber The partNumber to set
   */
  public void setPartNumber( int partNumber ) {
    this.partNumber = partNumber;
  }

  /**
   * Gets type
   *
   * @return value of type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type The type to set
   */
  public void setType( String type ) {
    this.type = type;
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets rowNr
   *
   * @return value of rowNr
   */
  public int getRowNr() {
    return rowNr;
  }

  /**
   * @param rowNr The rowNr to set
   */
  public void setRowNr( int rowNr ) {
    this.rowNr = rowNr;
  }

  /**
   * Gets colNr
   *
   * @return value of colNr
   */
  public int getColNr() {
    return colNr;
  }

  /**
   * @param colNr The colNr to set
   */
  public void setColNr( int colNr ) {
    this.colNr = colNr;
  }

  /**
   * Gets geometry
   *
   * @return value of geometry
   */
  public LeanGeometry getGeometry() {
    return geometry;
  }

  /**
   * @param geometry The geometry to set
   */
  public void setGeometry( LeanGeometry geometry ) {
    this.geometry = geometry;
  }

  /**
   * Gets context
   *
   * @return value of context
   */
  public DrawnContext getContext() {
    return context;
  }

  /**
   * @param context The context to set
   */
  public void setContext( DrawnContext context ) {
    this.context = context;
  }
}
