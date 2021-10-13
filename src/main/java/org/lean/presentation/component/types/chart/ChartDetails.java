package org.lean.presentation.component.types.chart;

import org.apache.hop.core.row.IValueMeta;
import org.lean.core.LeanTextGeometry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChartDetails {

  public double x;
  public double y;
  public double width;
  public double height;
  public List<Set<String>> horizontalValues;
  public Set<List<String>> horizontalCombinations;
  public List<Set<String>> verticalValues;
  public Set<List<String>> verticalCombinations;
  public List<String> labels;
  public List<LeanTextGeometry> labelGeometries;
  public double maxLabelHeight = 0;

  public double partWidth;
  public double partHeight;

  public List<List<String>> factLabels;
  public List<List<Object>> factValues;
  public List<List<IValueMeta>> factValueMetas;

  public double minValue;
  public double maxValue;
  public double maxFactWidth;
  public double overshoot;
  public double valueRange;
  public double valueFactor;
  public String minLabel;
  public String maxLabel;
  public LeanTextGeometry minLabelGeometry;
  public LeanTextGeometry maxLabelGeometry;
  public LeanTextGeometry titleGeometry;
  public double titleHeight;
  public int legendWidth;
  public int legendHeight;
  public int nrLegendColumns;
  public int nrLegendRows;
  public int maxLegendLabelWidth;
  public int maxLegendLabelHeight;
  public List<String> legendLabels;
  public List<LeanTextGeometry> legendLabelGeos;
  public int legendMarkerSize;
  public int maxNrLegendColumns;

  public ChartDetails(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.horizontalValues = new ArrayList<>();
    this.horizontalCombinations = new HashSet<>();
    this.verticalValues = new ArrayList<>();
    this.verticalCombinations = new HashSet<>();
    this.labels = new ArrayList<>();
    this.labelGeometries = new ArrayList<>();

    this.factLabels = new ArrayList<>();
    this.factValues = new ArrayList<>();
    this.factValueMetas = new ArrayList<>();
  }
}
