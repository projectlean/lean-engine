package org.lean.core;

import org.lean.core.exception.LeanException;
import org.lean.presentation.datacontext.IDataContext;
import org.apache.hop.core.row.RowMetaInterface;

public interface ILeanDataStreaming {

  /**
   * Add a data listener which keeps on the lookout for new rows of data
   * @param rowListener
   * @throws LeanException
   */
  public void addRowListener( ILeanRowListener rowListener ) throws LeanException;

  /**
   * Start streaming data, pick up the rows with addDataListener();
   * @param dataContext the data context in which the connector needs to work (other connectors to use...)
   * @throws LeanException
   */
  public void startStreaming( IDataContext dataContext) throws LeanException;

  /**
   * End streaming of data
   * @throws LeanException
   */
  public void waitUntilFinished() throws LeanException;

  /**
   * Remove the data listener in case it's no longer needed
   *
   * @param rowListener the listener to remove
   */
  public void removeDataListener(ILeanRowListener rowListener);


  /**
   * Describes all the fields that the connector produces at runtime without actually running anything.
   *
   * @return The row metadata
   * @param dataContext the data context
   * @throws LeanException
   */
  public RowMetaInterface describeOutput(IDataContext dataContext) throws LeanException;

}
