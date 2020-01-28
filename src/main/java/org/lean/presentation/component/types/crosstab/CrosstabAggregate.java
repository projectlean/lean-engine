package org.lean.presentation.component.types.crosstab;

public class CrosstabAggregate {
  public long count;
  public Double sum;

  public CrosstabAggregate( long count, Double sum ) {
    this.count = count;
    this.sum = sum;
  }
}