package org.lean.core.draw;

import org.lean.core.LeanGeometry;

import java.util.Objects;

// TODO: support Rotation of a drawn item, push up LeanGeometry.contains()
//
public class DrawnItem {

  private String componentName;
  private String componentPluginId;
  private int partNumber;
  private DrawnItemType type;
  private String category;
  private int rowNr;
  private int colNr;
  private LeanGeometry geometry;
  private DrawnContext context;

  public enum DrawnItemType {
    Component,
    ComponentItem,
  }

  public enum Category {
    ComponentArea,
    Label,
    Cell,
    Header,
    Title,
    LegendTitle,
    LegendEntry,
    XAxisLabel,
    YAxisLabel,
  }

  public DrawnItem() {}

  public DrawnItem(
      String componentName,
      String componentPluginId,
      int partNumber,
      DrawnItemType type,
      String category,
      int rowNr,
      int colNr,
      LeanGeometry geometry,
      DrawnContext context) {
    this.componentName = componentName;
    this.componentPluginId = componentPluginId;
    this.partNumber = partNumber;
    this.type = type;
    this.category = category;
    this.rowNr = rowNr;
    this.colNr = colNr;
    this.geometry = geometry;
    this.context = context;
  }

  public DrawnItem(
      String componentName,
      String componentPluginId,
      int partNumber,
      DrawnItemType type,
      String category,
      int rowNr,
      int colNr,
      LeanGeometry geometry) {
    this(
        componentName, componentPluginId, partNumber, type, category, rowNr, colNr, geometry, null);
  }

  @Override
  public String toString() {
    String string =
        "DrawnItem{"
            + "componentName='"
            + componentName
            + '\''
            + ", componentPluginId='"
            + componentPluginId
            + '\''
            + ", partNumber="
            + partNumber
            + ", type="
            + type.name()
            + ", category='"
            + category
            + '\''
            + ", rowNr="
            + rowNr
            + ", colNr="
            + colNr
            + ", geometry="
            + geometry;

    if (context != null) {
      string += ", context=" + context;
    }
    string += '}';

    return string;
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    DrawnItem drawnItem = (DrawnItem) o;
    return partNumber == drawnItem.partNumber && rowNr == drawnItem.rowNr && colNr == drawnItem.colNr && Objects.equals( componentName, drawnItem.componentName ) && Objects
      .equals( componentPluginId, drawnItem.componentPluginId ) && type == drawnItem.type && Objects.equals( category, drawnItem.category );
  }

  @Override public int hashCode() {
    return Objects.hash( componentName, componentPluginId, partNumber, type, category, rowNr, colNr );
  }

  /**
   * Gets componentName
   *
   * @return value of componentName
   */
  public String getComponentName() {
    return componentName;
  }

  /** @param componentName The componentName to set */
  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  /**
   * Gets componentPluginId
   *
   * @return value of componentPluginId
   */
  public String getComponentPluginId() {
    return componentPluginId;
  }

  /** @param componentPluginId The componentPluginId to set */
  public void setComponentPluginId(String componentPluginId) {
    this.componentPluginId = componentPluginId;
  }

  /**
   * Gets partNumber
   *
   * @return value of partNumber
   */
  public int getPartNumber() {
    return partNumber;
  }

  /** @param partNumber The partNumber to set */
  public void setPartNumber(int partNumber) {
    this.partNumber = partNumber;
  }

  /**
   * Gets type
   *
   * @return value of type
   */
  public DrawnItemType getType() {
    return type;
  }

  /** @param type The type to set */
  public void setType(DrawnItemType type) {
    this.type = type;
  }

  /**
   * Gets category
   *
   * @return value of category
   */
  public String getCategory() {
    return category;
  }

  /**
   * @param category The category to set
   */
  public void setCategory( String category ) {
    this.category = category;
  }

  /**
   * Gets rowNr
   *
   * @return value of rowNr
   */
  public int getRowNr() {
    return rowNr;
  }

  /** @param rowNr The rowNr to set */
  public void setRowNr(int rowNr) {
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

  /** @param colNr The colNr to set */
  public void setColNr(int colNr) {
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

  /** @param geometry The geometry to set */
  public void setGeometry(LeanGeometry geometry) {
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

  /** @param context The context to set */
  public void setContext(DrawnContext context) {
    this.context = context;
  }
}
