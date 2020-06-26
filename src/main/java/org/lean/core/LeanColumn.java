package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

public class LeanColumn {

  @HopMetadataProperty
  private String columnName;

  @HopMetadataProperty
  private String headerValue;

  @HopMetadataProperty
  private LeanHorizontalAlignment horizontalAlignment;

  @HopMetadataProperty
  private LeanVerticalAlignment verticalAlignment;

  @HopMetadataProperty
  private int width;

  @HopMetadataProperty
  private String formatMask;

  @HopMetadataProperty
  @Deprecated
  private LeanFont font;


  public LeanColumn() {
    horizontalAlignment = LeanHorizontalAlignment.LEFT;
    verticalAlignment = LeanVerticalAlignment.TOP;
  }

  public LeanColumn( LeanColumn c ) {
    this.columnName = c.columnName;
    this.headerValue = c.headerValue;
    this.horizontalAlignment = c.horizontalAlignment;
    this.verticalAlignment = c.verticalAlignment;
    this.width = c.width;
    this.formatMask = c.formatMask;
  }

  public LeanColumn( String columnName ) {
    this();
    this.columnName = columnName;
  }

  public LeanColumn( String columnName, String headerValue, LeanHorizontalAlignment horizontalAlignment, LeanVerticalAlignment verticalAlignment ) {
    this.columnName = columnName;
    this.headerValue = headerValue;
    this.horizontalAlignment = horizontalAlignment;
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * Gets columnName
   *
   * @return value of columnName
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * @param columnName The columnName to set
   */
  public void setColumnName( String columnName ) {
    this.columnName = columnName;
  }

  /**
   * Gets horizontalAlignment
   *
   * @return value of horizontalAlignment
   */
  public LeanHorizontalAlignment getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * @param horizontalAlignment The horizontalAlignment to set
   */
  public void setHorizontalAlignment( LeanHorizontalAlignment horizontalAlignment ) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * Gets verticalAlignment
   *
   * @return value of verticalAlignment
   */
  public LeanVerticalAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * @param verticalAlignment The verticalAlignment to set
   */
  public void setVerticalAlignment( LeanVerticalAlignment verticalAlignment ) {
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * Gets headerValue
   *
   * @return value of headerValue
   */
  public String getHeaderValue() {
    return headerValue;
  }

  /**
   * @param headerValue The headerValue to set
   */
  public void setHeaderValue( String headerValue ) {
    this.headerValue = headerValue;
  }

  /**
   * Gets width
   *
   * @return value of width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width The width to set
   */
  public void setWidth( int width ) {
    this.width = width;
  }

  /**
   * Gets formatMask
   *
   * @return value of formatMask
   */
  public String getFormatMask() {
    return formatMask;
  }

  /**
   * @param formatMask The formatMask to set
   */
  public void setFormatMask( String formatMask ) {
    this.formatMask = formatMask;
  }

  /**
   * Gets font
   *
   * @return value of font
   */
  @Deprecated
  public LeanFont getFont() {
    return font;
  }

  /**
   * @param font The font to set
   */
  @Deprecated
  public void setFont( LeanFont font ) {
    this.font = font;
  }
}
