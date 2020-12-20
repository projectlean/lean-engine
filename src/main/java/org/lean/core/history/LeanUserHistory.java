package org.lean.core.history;

import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.HopMetadataBase;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;

import java.util.ArrayList;
import java.util.List;

@HopMetadata(
  key = "user-history",
  name = "Lean User History",
  description = "Describes user action history"
)
public class LeanUserHistory extends HopMetadataBase implements IHopMetadata {

  @HopMetadataProperty
  private List<LeanUserHistoryAction> actions;

  public LeanUserHistory() {
    actions = new ArrayList<>();
  }

  public LeanUserHistory( String name, List<LeanUserHistoryAction> actions ) {
    this.name = name;
    this.actions = actions;
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
