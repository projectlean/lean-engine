package org.lean.core.history;

import org.lean.core.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.apache.hop.metastore.stores.memory.MemoryMetaStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserHistoryUtilTest {

  @Test
  public void userHistoryStoreRetrieveTest() throws Exception {

    MemoryMetaStore metaStore = new MemoryMetaStore();
    metaStore.setName( "Memory" );

    UserHistoryUtil.addUserHistoryAction( metaStore, "joe", "Presentation", "Sales Report" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Overview" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "joe", "Presentation", "Sales Report" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Overview" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c1" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c2" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "suzy", "Presentation", "Marketing Details" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c3" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c4" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c5" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c6" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c7" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c8" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c9" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c10" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c11" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c12" );
    UserHistoryUtil.addUserHistoryAction( metaStore, "pat", "Connector", "c13" );

    List<LeanUserHistoryAction> joeActions = UserHistoryUtil.getUserHistoryActions( metaStore, "joe" );
    assertEquals( 1, joeActions.size() );

    List<LeanUserHistoryAction> suzyActions = UserHistoryUtil.getUserHistoryActions( metaStore, "suzy" );
    assertEquals( 2, suzyActions.size() );

    List<LeanUserHistoryAction> patActions = UserHistoryUtil.getUserHistoryActions( metaStore, "pat" );
    Assert.assertEquals( Constants.USER_ACTION_HISTORY_SIZE, patActions.size() );

  }

}
