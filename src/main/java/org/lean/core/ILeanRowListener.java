package org.lean.core;

import org.apache.hop.core.row.IRowMeta;
import org.lean.core.exception.LeanException;

public interface ILeanRowListener {

  public void rowReceived(IRowMeta rowMeta, Object[] rowData) throws LeanException;
}
