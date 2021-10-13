package org.lean.presentation.connector.types.filter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = LeanSimpleFilterConnector.class)
public class SimpleFilterValue {

  private String fieldName;

  private String filterValue;

  public SimpleFilterValue() {}

  public SimpleFilterValue(String fieldName, String filterValue) {
    this.fieldName = fieldName;
    this.filterValue = filterValue;
  }

  /**
   * Gets fieldName
   *
   * @return value of fieldName
   */
  public String getFieldName() {
    return fieldName;
  }

  /** @param fieldName The fieldName to set */
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * Gets filterValue
   *
   * @return value of filterValue
   */
  public String getFilterValue() {
    return filterValue;
  }

  /** @param filterValue The filterValue to set */
  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }
}
