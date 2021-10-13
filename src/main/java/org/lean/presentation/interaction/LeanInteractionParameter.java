package org.lean.presentation.interaction;

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.Objects;

public class LeanInteractionParameter {

  @HopMetadataProperty private ParameterSourceType sourceType;
  @HopMetadataProperty private String parameterName;

  public LeanInteractionParameter() {}

  public LeanInteractionParameter(LeanInteractionParameter p) {
    this.sourceType = p.sourceType;
    this.parameterName = p.parameterName;
  }

  public LeanInteractionParameter(ParameterSourceType sourceType, String parameterName) {
    this.sourceType = sourceType;
    this.parameterName = parameterName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeanInteractionParameter that = (LeanInteractionParameter) o;
    return sourceType == that.sourceType && Objects.equals(parameterName, that.parameterName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceType, parameterName);
  }

  /**
   * Gets sourceType
   *
   * @return value of sourceType
   */
  public ParameterSourceType getSourceType() {
    return sourceType;
  }

  /** @param sourceType The sourceType to set */
  public void setSourceType(ParameterSourceType sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * Gets parameterName
   *
   * @return value of parameterName
   */
  public String getParameterName() {
    return parameterName;
  }

  /** @param parameterName The parameterName to set */
  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public enum ParameterSourceType {
    PresentationName,
    ComponentName,
    ComponentPluginId,
    ItemType,
    ItemCategory,
    ItemValue,
  }
}
