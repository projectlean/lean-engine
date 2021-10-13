package org.lean.presentation.interaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.ArrayList;
import java.util.List;

/** This describes an action that can be taken by a user on a presentation. */
public class LeanInteractionAction {

  // The type of action to take
  @HopMetadataProperty private ActionType actionType;
  // The name of the object to reference
  @HopMetadataProperty private String objectName;
  // The parameter to set
  @HopMetadataProperty private List<LeanInteractionParameter> parameters;

  public LeanInteractionAction() {
    parameters = new ArrayList<>();
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
    for (LeanInteractionParameter parameter : action.parameters) {
      this.parameters.add(new LeanInteractionParameter(parameter));
    }
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

  /** @param actionType The actionType to set */
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

  /** @param objectName The objectName to set */
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  /**
   * Gets parameters
   *
   * @return value of parameters
   */
  public List<LeanInteractionParameter> getParameters() {
    return parameters;
  }

  /** @param parameters The parameters to set */
  public void setParameters(List<LeanInteractionParameter> parameters) {
    this.parameters = parameters;
  }

  // TODO: make the action types plugins with a way to provide a JavaScript function for the browser
  // side functionality
  //
  public enum ActionType {
    OpenPresentation,
    FilterOnly,
    FilterInclude,
    FilterExclude,
  }
}
