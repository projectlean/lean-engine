package org.lean.presentation.interaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hop.metadata.api.HopMetadataProperty;

/** This describes an action that can be taken by a user on a presentation. */
public class LeanInteractionAction {

  public enum ActionType {
    /**
     * Open the presentation with the name either in the object name (static value) or take the name
     * from the value clicked on. In either case you can also set this string value where you
     * clicked on as a parameter.
     */
    OPEN_PRESENTATION
  }

  /** The type of action to take. */
  @HopMetadataProperty private ActionType actionType;
  /**
   * The name of the object to reference (static value, for example: open a presentation with fixed
   * name.
   */
  @HopMetadataProperty private String objectName;
  /**
   * The name of the parameter to set before the action. Its value comes from the string value
   * clicked on.
   */
  @HopMetadataProperty private String valueParameter;

  public LeanInteractionAction() {}

  public LeanInteractionAction(ActionType actionType) {
    this(actionType, null);
  }

  public LeanInteractionAction(ActionType actionType, String objectName) {
    this();
    this.actionType = actionType;
    this.objectName = objectName;
  }

  public LeanInteractionAction(LeanInteractionAction action) {
    this();
    this.actionType = action.actionType;
    this.objectName = action.objectName;
    this.valueParameter = action.valueParameter;
  }

  public String toJsonString() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(this);
  }

  /**
   * Gets actionType
   *
   * @return value of actionType
   */
  public ActionType getActionType() {
    return actionType;
  }

  /**
   * @param actionType The actionType to set
   */
  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  /**
   * Gets objectName
   *
   * @return value of objectName
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * @param objectName The objectName to set
   */
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  /**
   * Gets valueParameter
   *
   * @return value of valueParameter
   */
  public String getValueParameter() {
    return valueParameter;
  }

  /**
   * Sets valueParameter
   *
   * @param valueParameter value of valueParameter
   */
  public void setValueParameter(String valueParameter) {
    this.valueParameter = valueParameter;
  }
}
