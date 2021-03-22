package org.lean.www;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lean.core.draw.DrawnItem;
import org.lean.presentation.interaction.LeanInteractionAction;

import java.util.List;

public class DrawInfo {
  private DrawnItem drawnItem;
  private List<LeanInteractionAction> actions;

  public DrawInfo() {
  }

  public DrawInfo( DrawnItem drawnItem, List<LeanInteractionAction> actions ) {
    this.drawnItem = drawnItem;
    this.actions = actions;
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
   * Gets actions
   *
   * @return value of actions
   */
  public List<LeanInteractionAction> getActions() {
    return actions;
  }

  /**
   * @param actions The actions to set
   */
  public void setActions( List<LeanInteractionAction> actions ) {
    this.actions = actions;
  }
}
