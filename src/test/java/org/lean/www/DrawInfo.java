package org.lean.www;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lean.core.draw.DrawnItem;
import org.lean.presentation.interaction.LeanInteractionAction;

public class DrawInfo {
  private DrawnItem drawnItem;
  private LeanInteractionAction action;

  public DrawInfo() {
  }

  public DrawInfo( DrawnItem drawnItem, LeanInteractionAction action ) {
    this.drawnItem = drawnItem;
    this.action = action;
  }

  public String toJsonString() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString( this );
  }


  /**
   * Gets drawnItem
   *
   * @return value of drawnItem
   */
  public DrawnItem getDrawnItem() {
    return drawnItem;
  }

  /**
   * @param drawnItem The drawnItem to set
   */
  public void setDrawnItem( DrawnItem drawnItem ) {
    this.drawnItem = drawnItem;
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
