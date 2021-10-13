package org.lean.presentation.interaction;

import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.draw.DrawnItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Describes an interaction: method: how the user interacts. location: where the interaction can
 * take place action: what needs to happen
 */
public class LeanInteraction {

  @HopMetadataProperty private LeanInteractionMethod method;

  @HopMetadataProperty private LeanInteractionLocation location;

  @HopMetadataProperty private List<LeanInteractionAction> actions;

  public LeanInteraction() {}

  public LeanInteraction(
      LeanInteractionMethod method,
      LeanInteractionLocation location,
      LeanInteractionAction... actions) {
    this.method = method;
    this.location = location;
    this.actions = new ArrayList<>(Arrays.asList(actions));
  }

  public LeanInteraction(LeanInteraction interaction) {
    this();
    this.method = new LeanInteractionMethod(interaction.method);
    this.location = new LeanInteractionLocation(interaction.location);
    this.actions = new ArrayList<>();
    for (LeanInteractionAction action : interaction.actions) {
      actions.add(new LeanInteractionAction(action));
    }
  }

  public boolean matches(LeanInteractionMethod method, DrawnItem drawnItem) {
    if (method != null && !this.method.equals(method)) {
      return false;
    }
    return location.matches(drawnItem);
  }

  /**
   * Gets method
   *
   * @return value of method
   */
  public LeanInteractionMethod getMethod() {
    return method;
  }

  /** @param method The method to set */
  public void setMethod(LeanInteractionMethod method) {
    this.method = method;
  }

  /**
   * Gets location
   *
   * @return value of location
   */
  public LeanInteractionLocation getLocation() {
    return location;
  }

  /** @param location The location to set */
  public void setLocation(LeanInteractionLocation location) {
    this.location = location;
  }

  /**
   * Gets the list of actions
   *
   * @return List of actions
   */
  public List<LeanInteractionAction> getActions() {
    return actions;
  }

  /** @param actions The action to set */
  public void setActions(List<LeanInteractionAction> actions) {
    this.actions = actions;
  }
}
