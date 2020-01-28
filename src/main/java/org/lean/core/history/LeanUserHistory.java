package org.lean.core.history;

import org.apache.hop.metastore.persist.MetaStoreAttribute;
import org.apache.hop.metastore.persist.MetaStoreElementType;

import java.util.ArrayList;
import java.util.List;

@MetaStoreElementType(
  name = "Lean User History",
  description = "Describes user action history"
)
public class LeanUserHistory {

  /**
   * This is the name of the user for which we have history
   */
  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private List<LeanUserHistoryAction> actions;

  public LeanUserHistory() {
    actions = new ArrayList<>();
  }

  public LeanUserHistory( String name, List<LeanUserHistoryAction> actions ) {
    this.name = name;
    this.actions = actions;
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets actions
   *
   * @return value of actions
   */
  public List<LeanUserHistoryAction> getActions() {
    return actions;
  }

  /**
   * @param actions The actions to set
   */
  public void setActions( List<LeanUserHistoryAction> actions ) {
    this.actions = actions;
  }
}
