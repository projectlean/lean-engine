package org.lean.core;

import org.apache.hop.metadata.api.HopMetadataProperty;

/** To attach the location of one component to another */
public class LeanAttachment {

  @HopMetadataProperty private String componentName;
  @HopMetadataProperty private int percentage;
  @HopMetadataProperty private int offset;
  @HopMetadataProperty private Alignment alignment;

  public LeanAttachment() {
    componentName = null;
    percentage = 0;
    offset = 0;
    alignment = Alignment.DEFAULT;
  }

  public LeanAttachment(int percentage, int offset) {
    this.percentage = percentage;
    this.offset = offset;
    alignment = Alignment.DEFAULT;
  }

  public LeanAttachment(String componentName, int percentage, int offset) {
    this.componentName = componentName;
    this.percentage = percentage;
    this.offset = offset;
    alignment = Alignment.DEFAULT;
  }

  public LeanAttachment(String componentName, int percentage, int offset, Alignment alignment) {
    this.componentName = componentName;
    this.percentage = percentage;
    this.offset = offset;
    this.alignment = alignment;
  }

  public LeanAttachment(LeanAttachment attachment) {
    this.componentName = attachment.componentName;
    this.percentage = attachment.percentage;
    this.offset = attachment.offset;
    this.alignment = attachment.alignment;
  }

  @Override public String toString() {
    return "LeanAttachment{" +
      "componentName='" + componentName + '\'' +
      ", percentage=" + percentage +
      ", offset=" + offset +
      ", alignment=" + alignment +
      '}';
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
   * Gets percentage
   *
   * @return value of percentage
   */
  public int getPercentage() {
    return percentage;
  }

  /** @param percentage The percentage to set */
  public void setPercentage(int percentage) {
    this.percentage = percentage;
  }

  /**
   * Gets offset
   *
   * @return value of offset
   */
  public int getOffset() {
    return offset;
  }

  /** @param offset The offset to set */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * Gets alignment
   *
   * @return value of alignment
   */
  public Alignment getAlignment() {
    return alignment;
  }

  /** @param alignment The alignment to set */
  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }

  public enum Alignment {
    DEFAULT,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER,
  }
}
