package org.lean.presentation.connector.types.passthrough;

import org.apache.hop.core.row.IRowMeta;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;

import java.util.concurrent.ArrayBlockingQueue;

public class PassthroughRowListener implements ILeanRowListener {

  protected ILeanConnector connector;
  protected ArrayBlockingQueue<Object> finishedQueue;

  public PassthroughRowListener(
      ILeanConnector connector, ArrayBlockingQueue<Object> finishedQueue) {
    this.connector = connector;
    this.finishedQueue = finishedQueue;
  }

  public void rowReceived(IRowMeta rowMeta, Object[] rowData) throws LeanException {
    if (rowData == null) {
      // Signal we're done
      //
      for (ILeanRowListener rowListener : connector.getRowListeners()) {
        rowListener.rowReceived(null, null);
      }
      if (finishedQueue != null) {
        finishedQueue.add(new Object());
      }
      return;
    }

    // Pass the data along
    //
    for (ILeanRowListener rowListener : connector.getRowListeners()) {
      rowListener.rowReceived(rowMeta, rowData);
    }
  }
}
