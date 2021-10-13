package org.lean.core.history;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.lean.core.Constants;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserHistoryUtilTest {

  @Test
  public void userHistoryStoreRetrieveTest() throws Exception {

    IHopMetadataProvider metadataProvider = new MemoryMetadataProvider();

    UserHistoryUtil.addUserHistoryAction(metadataProvider, "joe", "Presentation", "Sales Report");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Overview");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "joe", "Presentation", "Sales Report");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Overview");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c1");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c2");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(
        metadataProvider, "suzy", "Presentation", "Marketing Details");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c3");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c4");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c5");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c6");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c7");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c8");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c9");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c10");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c11");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c12");
    UserHistoryUtil.addUserHistoryAction(metadataProvider, "pat", "Connector", "c13");

    List<LeanUserHistoryAction> joeActions =
        UserHistoryUtil.getUserHistoryActions(metadataProvider, "joe");
    assertEquals(1, joeActions.size());

    List<LeanUserHistoryAction> suzyActions =
        UserHistoryUtil.getUserHistoryActions(metadataProvider, "suzy");
    assertEquals(2, suzyActions.size());

    List<LeanUserHistoryAction> patActions =
        UserHistoryUtil.getUserHistoryActions(metadataProvider, "pat");
    Assert.assertEquals(Constants.USER_ACTION_HISTORY_SIZE, patActions.size());
  }
}
