package org.lean.core;

import org.lean.core.exception.LeanException;
import org.apache.hop.core.row.IRowMeta;

public interface ILeanRowListener {

  public void rowReceived( IRowMeta rowMeta, Object[] rowData ) throws LeanException;

}
