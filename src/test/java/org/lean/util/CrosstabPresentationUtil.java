package org.lean.util;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.AggregationMethod;
import org.lean.core.LeanDimension;
import org.lean.core.LeanFact;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.crosstab.LeanCrosstabComponent;
import org.lean.presentation.layout.LeanLayout;

import java.util.Arrays;

public class CrosstabPresentationUtil extends BasePresentationUtil {

  public CrosstabPresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public LeanPresentation createCrosstabPresentation(int nr) throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Crosstab (" + nr + ")",
            "Crosstab " + nr + " description",
            100000,
            "A crosstab top left of the page");

    LeanCrosstabComponent crosstab = new LeanCrosstabComponent(CONNECTOR_SAMPLE_ROWS);
    crosstab.setHorizontalDimensions(
        Arrays.asList(
            new LeanDimension(
                "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE),
            new LeanDimension(
                "important", "?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.MIDDLE)));
    crosstab.setVerticalDimensions(
        Arrays.asList(
            new LeanDimension(
                "name", "Customer", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.MIDDLE)));
    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.MIDDLE,
            AggregationMethod.SUM,
            "0.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.MIDDLE);
    LeanFact countFact =
        new LeanFact(
            "name",
            "Count",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.MIDDLE,
            AggregationMethod.COUNT,
            "0");
    countFact.setHorizontalAggregation(true);
    countFact.setHorizontalAggregationHeader("Total Count");
    countFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    countFact.setHeaderVerticalAlignment(LeanVerticalAlignment.MIDDLE);
    crosstab.setFacts(Arrays.asList(sumFact, countFact));
    crosstab.setBackground(true);
    crosstab.setBorder(false);
    crosstab.setHorizontalMargin(3);
    crosstab.setVerticalMargin(2);
    crosstab.setEvenHeights(true);
    crosstab.setHeaderOnEveryPage(true);
    crosstab.setShowingVerticalTotals(true);
    crosstab.setShowingHorizontalTotals(true);

    LeanComponent crosstabComponent = new LeanComponent("Table1", crosstab);
    crosstabComponent.setLayout(new LeanLayout(0, 0));

    // Add the table to the first page.
    //
    presentation.getPages().get(0).getComponents().add(crosstabComponent);

    return presentation;
  }

  public LeanPresentation createCrosstabPresentationOnlyVerticalDimensions(int nr)
      throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Crosstab only vertical" + nr,
            "Crosstab only vertical " + nr + " description",
            50,
            "Crosstab with only vertical dimensions");

    LeanCrosstabComponent crosstab = new LeanCrosstabComponent(CONNECTOR_SAMPLE_ROWS);
    crosstab.setHorizontalDimensions(Arrays.asList());

    crosstab.setVerticalDimensions(
        Arrays.asList(
            new LeanDimension("id", "ID", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.TOP),
            new LeanDimension(
                "name", "Name", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP),
            new LeanDimension(
                "updated",
                "Time of update",
                LeanHorizontalAlignment.LEFT,
                LeanVerticalAlignment.TOP),
            new LeanDimension(
                "important", "Imp?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP)));
    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.SUM,
            "#.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    sumFact.setFormatMask("0.00");
    LeanFact countFact =
        new LeanFact(
            "name",
            "Count",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.COUNT,
            "#");
    countFact.setHorizontalAggregation(true);
    countFact.setHorizontalAggregationHeader("Total Count");
    countFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    countFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    crosstab.setFacts(Arrays.asList(sumFact, countFact));
    crosstab.setBackground(true);
    crosstab.setBorder(false);
    crosstab.setHorizontalMargin(6);
    crosstab.setVerticalMargin(3);
    crosstab.setEvenHeights(true);
    crosstab.setHeaderOnEveryPage(true);

    LeanComponent crosstabComponent = new LeanComponent("Table1", crosstab);
    crosstabComponent.setLayout(new LeanLayout(0, 0));

    // Add crosstab to first page
    //
    presentation.getPages().get(0).getComponents().add(crosstabComponent);

    return presentation;
  }

  public LeanPresentation createCrosstabPresentationOnlyHorizontalDimensions(int nr)
      throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Crosstab only horizontal (" + nr + ")",
            "Crosstab only horizontal " + nr + " description",
            100,
            "Crosstab with only horizontal dimensions");

    LeanCrosstabComponent crosstab = new LeanCrosstabComponent(CONNECTOR_SAMPLE_ROWS);
    crosstab.setHorizontalDimensions(
        Arrays.asList(
            new LeanDimension(
                "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP),
            new LeanDimension(
                "important", "?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP)));

    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.SUM,
            "#.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    LeanFact countFact =
        new LeanFact(
            "name",
            "Count",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.COUNT,
            "#");
    countFact.setHorizontalAggregation(true);
    countFact.setHorizontalAggregationHeader("Total Count");
    countFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    countFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    crosstab.setFacts(Arrays.asList(sumFact, countFact));
    crosstab.setBackground(true);
    crosstab.setBorder(false);
    crosstab.setHorizontalMargin(6);
    crosstab.setVerticalMargin(3);
    crosstab.setEvenHeights(true);
    crosstab.setHeaderOnEveryPage(true);

    LeanComponent crosstabComponent = new LeanComponent("Table1", crosstab);
    crosstabComponent.setLayout(new LeanLayout(25, 5));

    presentation.getPages().get(0).getComponents().add(crosstabComponent);

    return presentation;
  }

  public LeanPresentation createCrosstabPresentationOnlyFacts(int nr) throws Exception {
    LeanPresentation presentation =
        createBasePresentation(
            "Crosstab only facts (" + nr + ")",
            "Crosstab only facts " + nr + " description",
            50,
            "Crosstab with only facts");

    LeanCrosstabComponent crosstab = new LeanCrosstabComponent(CONNECTOR_SAMPLE_ROWS);
    crosstab.setHorizontalDimensions(Arrays.asList());
    crosstab.setVerticalDimensions(Arrays.asList());
    LeanFact sumFact =
        new LeanFact(
            "random",
            "Sum",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.SUM,
            "#.000");
    sumFact.setHorizontalAggregation(true);
    sumFact.setHorizontalAggregationHeader("Total Sum");
    sumFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    sumFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    LeanFact countFact =
        new LeanFact(
            "name",
            "Count",
            LeanHorizontalAlignment.RIGHT,
            LeanVerticalAlignment.TOP,
            AggregationMethod.COUNT,
            "#");
    countFact.setHorizontalAggregation(true);
    countFact.setHorizontalAggregationHeader("Total Count");
    countFact.setHeaderHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    countFact.setHeaderVerticalAlignment(LeanVerticalAlignment.TOP);
    crosstab.setFacts(Arrays.asList(sumFact, countFact));
    crosstab.setBackground(true);
    crosstab.setBorder(false);
    crosstab.setHorizontalMargin(6);
    crosstab.setVerticalMargin(3);
    crosstab.setEvenHeights(true);
    crosstab.setHeaderOnEveryPage(true);

    LeanComponent crosstabComponent = new LeanComponent("Table1", crosstab);
    crosstabComponent.setLayout(new LeanLayout(25, 5));

    presentation.getPages().get(0).getComponents().add(crosstabComponent);

    return presentation;
  }
}
