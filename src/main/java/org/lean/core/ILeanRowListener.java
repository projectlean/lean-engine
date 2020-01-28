package org.lean.core;

import org.lean.core.exception.LeanException;
import org.apache.hop.core.row.RowMetaInterface;

public interface ILeanRowListener {

  public void rowReceived( RowMetaInterface rowMeta, Object[] rowData ) throws LeanException;

}
