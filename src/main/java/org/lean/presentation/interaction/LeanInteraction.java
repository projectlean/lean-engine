package org.lean.presentation.interaction;

import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.draw.DrawnItem;

/**
 * Describes an interaction: method: how the user interacts. location: where the interaction can
 * take place action: what needs to happen
 */
public class LeanInteraction {

  @HopMetadataProperty private LeanInteractionMethod method;

  @HopMetadataProperty private LeanInteractionLocation location;

  @HopMetadataProperty private LeanInteractionAction action;

  public LeanInteraction() {}

  public LeanInteraction(
      LeanInteractionMethod method,
      LeanInteractionLocation location,
      LeanInteractionAction action) {
    this.method = method;
    this.location = location;
    this.action = action;
  }

  public LeanInteraction( LeanInteraction interaction ) {
    this();
    this.method = new LeanInteractionMethod(interaction.method);
    this.location = new LeanInteractionLocation(interaction.location);
    this.action = new LeanInteractionAction(interaction.action);
  }

  public boolean matches( LeanInteractionMethod method, DrawnItem drawnItem ) {
    if (!this.method.equals(method)) {
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

  /**
   * @param method The method to set
   */
  public void setMethod( LeanInteractionMethod method ) {
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

  /**
   * @param location The location to set
   */
  public void setLocation( LeanInteractionLocation location ) {
    this.location = location;
  }

  /**
   * Gets action
   *
   * @return value of action
   */
  public LeanInteractionAction getAction() {
    return action;
  }

  /**
   * @param action The action to set
   */
  public void setAction( LeanInteractionAction action ) {
    this.action = action;
  }


}
