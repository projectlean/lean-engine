package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

public class LeanFact extends LeanColumn {

  @HopMetadataProperty private AggregationMethod aggregationMethod;

  @HopMetadataProperty private boolean horizontalAggregation;

  @HopMetadataProperty private String horizontalAggregationHeader;

  @HopMetadataProperty private boolean verticalAggregation;

  @HopMetadataProperty private String verticalAggregationHeader;

  @HopMetadataProperty private LeanHorizontalAlignment headerHorizontalAlignment;

  @HopMetadataProperty private LeanVerticalAlignment headerVerticalAlignment;

  public LeanFact() {
    super();
  }

  public LeanFact(String columnName, AggregationMethod aggregationMethod) {
    super(columnName);
    this.aggregationMethod = aggregationMethod;
  }

  public LeanFact(
      String columnName,
      String headerValue,
      LeanHorizontalAlignment horizontalAlignment,
      LeanVerticalAlignment verticalAlignment,
      AggregationMethod aggregationMethod,
      String formatMask) {
    super(columnName, headerValue, horizontalAlignment, verticalAlignment);
    this.aggregationMethod = aggregationMethod;
    setFormatMask(formatMask);
  }

  public LeanFact(LeanFact f) {
    super(f);
    this.aggregationMethod = f.aggregationMethod;
    this.horizontalAggregation = f.horizontalAggregation;
    this.horizontalAggregationHeader = f.horizontalAggregationHeader;
    this.verticalAggregation = f.verticalAggregation;
    this.verticalAggregationHeader = f.verticalAggregationHeader;
    this.headerHorizontalAlignment = f.headerHorizontalAlignment;
    this.headerVerticalAlignment = f.headerVerticalAlignment;
  }

  /**
   * Gets aggregationMethod
   *
   * @return value of aggregationMethod
   */
  public AggregationMethod getAggregationMethod() {
    return aggregationMethod;
  }

  /** @param aggregationMethod The aggregationMethod to set */
  public void setAggregationMethod(AggregationMethod aggregationMethod) {
    this.aggregationMethod = aggregationMethod;
  }

  /**
   * Gets horizontalAggregation
   *
   * @return value of horizontalAggregation
   */
  public boolean isHorizontalAggregation() {
    return horizontalAggregation;
  }

  /** @param horizontalAggregation The horizontalAggregation to set */
  public void setHorizontalAggregation(boolean horizontalAggregation) {
    this.horizontalAggregation = horizontalAggregation;
  }

  /**
   * Gets horizontalAggregationHeader
   *
   * @return value of horizontalAggregationHeader
   */
  public String getHorizontalAggregationHeader() {
    return horizontalAggregationHeader;
  }

  /** @param horizontalAggregationHeader The horizontalAggregationHeader to set */
  public void setHorizontalAggregationHeader(String horizontalAggregationHeader) {
    this.horizontalAggregationHeader = horizontalAggregationHeader;
  }

  /**
   * Gets verticalAggregation
   *
   * @return value of verticalAggregation
   */
  public boolean isVerticalAggregation() {
    return verticalAggregation;
  }

  /** @param verticalAggregation The verticalAggregation to set */
  public void setVerticalAggregation(boolean verticalAggregation) {
    this.verticalAggregation = verticalAggregation;
  }

  /**
   * Gets verticalAggregationHeader
   *
   * @return value of verticalAggregationHeader
   */
  public String getVerticalAggregationHeader() {
    return verticalAggregationHeader;
  }

  /** @param verticalAggregationHeader The verticalAggregationHeader to set */
  public void setVerticalAggregationHeader(String verticalAggregationHeader) {
    this.verticalAggregationHeader = verticalAggregationHeader;
  }

  /**
   * Gets headerHorizontalAlignment
   *
   * @return value of headerHorizontalAlignment
   */
  public LeanHorizontalAlignment getHeaderHorizontalAlignment() {
    return headerHorizontalAlignment;
  }

  /** @param headerHorizontalAlignment The headerHorizontalAlignment to set */
  public void setHeaderHorizontalAlignment(LeanHorizontalAlignment headerHorizontalAlignment) {
    this.headerHorizontalAlignment = headerHorizontalAlignment;
  }

  /**
   * Gets headerVerticalAlignment
   *
   * @return value of headerVerticalAlignment
   */
  public LeanVerticalAlignment getHeaderVerticalAlignment() {
    return headerVerticalAlignment;
  }

  /** @param headerVerticalAlignment The headerVerticalAlignment to set */
  public void setHeaderVerticalAlignment(LeanVerticalAlignment headerVerticalAlignment) {
    this.headerVerticalAlignment = headerVerticalAlignment;
  }
}
