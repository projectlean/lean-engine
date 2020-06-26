package org.lean.core;

import org.apache.hop.core.row.IRowMeta;

import java.util.ArrayList;
import java.util.List;

public class LeanDataSet {
  private IRowMeta rowMeta;

  private List<Object[]> rows;

  public LeanDataSet() {
    rows = new ArrayList<>();
  }

  public LeanDataSet( IRowMeta rowMeta, List<Object[]> rows ) {
    this.rowMeta = rowMeta;
    this.rows = rows;
  }

  /**
   * Gets rowMeta
   *
   * @return value of rowMeta
   */
  public IRowMeta getRowMeta() {
    return rowMeta;
  }

  /**
   * @param rowMeta The rowMeta to set
   */
  public void setRowMeta( IRowMeta rowMeta ) {
    this.rowMeta = rowMeta;
  }

  /**
   * Gets rows
   *
   * @return value of rows
   */
  public List<Object[]> getRows() {
    return rows;
  }

  /**
   * @param rows The rows to set
   */
  public void setRows( List<Object[]> rows ) {
    this.rows = rows;
  }
}
