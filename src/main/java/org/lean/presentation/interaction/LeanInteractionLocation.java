package org.lean.presentation.interaction;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.draw.DrawnItem;

/** Describe where an interaction can take place */
public class LeanInteractionLocation {

  // The name of the component or null for all components
  @HopMetadataProperty private String componentName;

  // The ID of the plugin or null for all plugins
  @HopMetadataProperty private String componentPluginId;

  // The item type or null for all item types
  @HopMetadataProperty private String itemType;

  // The item category or null for all categories
  @HopMetadataProperty private String itemCategory;

  public LeanInteractionLocation() {}

  public LeanInteractionLocation(
      String componentName, String componentPluginId, String itemType, String itemCategory) {
    this.componentName = componentName;
    this.componentPluginId = componentPluginId;
    this.itemType = itemType;
    this.itemCategory = itemCategory;
  }

  public LeanInteractionLocation(LeanInteractionLocation location) {
    this();
    this.componentName = location.componentName;
    this.componentPluginId = location.componentPluginId;
    this.itemType = location.itemType;
    this.itemCategory = location.itemCategory;
  }

  public boolean matches(DrawnItem drawnItem) {

    if (StringUtils.isNotEmpty(componentName)) {
      if (!drawnItem.getComponentName().equals(componentName)) {
        return false;
      }
    }
    if (StringUtils.isNotEmpty(componentPluginId)) {
      if (!drawnItem.getComponentPluginId().equals(componentPluginId)) {
        return false;
      }
    }
    if (StringUtils.isNotEmpty(itemType)) {
      if (!drawnItem.getType().name().equals(itemType)) {
        return false;
      }
    }
    if (StringUtils.isNotEmpty(itemCategory)) {
      if (!drawnItem.getCategory().equals(itemCategory)) {
        return false;
      }
    }
    return true;
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
   * Gets itemType
   *
   * @return value of itemType
   */
  public String getItemType() {
    return itemType;
  }

  /** @param itemType The itemType to set */
  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  /**
   * Gets itemCategory
   *
   * @return value of itemCategory
   */
  public String getItemCategory() {
    return itemCategory;
  }

  /** @param itemCategory The itemCategory to set */
  public void setItemCategory(String itemCategory) {
    this.itemCategory = itemCategory;
  }
}
