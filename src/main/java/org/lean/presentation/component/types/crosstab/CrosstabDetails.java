package org.lean.presentation.component.types.crosstab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrosstabDetails {
  public List<List<CellInfo>> cellInfosList;
  public List<Map<List<String>, CrosstabAggregate>> horizontalAggregatesList;
  public List<Map<List<String>, CrosstabAggregate>> verticalAggregatesList;
  public List<Boolean> headerRowFlags;
  public int globalMaxYOffset;
  public int globalMinYOffset;
  public int nrHeaderLines;

  public List<List<String>> sortedVerticalCombinations;
  public List<List<String>> sortedHorizontalCombinations;
  public int totalWidth;
  public int totalHeight;

  public List<Integer> maxWidths;
  public List<Integer> maxHeights;

  public CrosstabDetails() {
    cellInfosList = new ArrayList<>();
    horizontalAggregatesList = new ArrayList<>();
    verticalAggregatesList = new ArrayList<>();
    verticalAggregatesList = new ArrayList<>();
    headerRowFlags = new ArrayList<>();
    globalMaxYOffset = 0;
    globalMinYOffset = Integer.MAX_VALUE;
    sortedVerticalCombinations = new ArrayList<>();
    sortedHorizontalCombinations = new ArrayList<>();
    maxWidths = new ArrayList<>();
    maxHeights = new ArrayList<>();
    nrHeaderLines = 0;
  }
}
