package org.lean.presentation.connector.types.sampledata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMetaBuilder;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@JsonDeserialize(as = LeanSampleDataConnector.class)
@LeanConnectorPlugin(
    id = "SampleDataConnector",
    name = "Sample data",
    description = "A sample data connector giving back a configurable list of sample rows")
public class LeanSampleDataConnector extends LeanBaseConnector implements ILeanConnector {

  @HopMetadataProperty private int rowCount;

  public LeanSampleDataConnector() {
    this(100);
  }

  public LeanSampleDataConnector(int rowCount) {
    super("SampleDataConnector");
    this.rowCount = rowCount;
  }

  public LeanSampleDataConnector(LeanSampleDataConnector c) {
    super(c);
    this.rowCount = c.rowCount;
  }

  public LeanSampleDataConnector clone() {
    return new LeanSampleDataConnector(this);
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) {

    return new RowMetaBuilder()
        .addInteger("id")
        .addString("name")
        .addDate("updated")
        .addBoolean("important")
        .addNumber("random")
        .addString("color")
        .addString("country")
        .build();
  }

  /**
   * For the sampledata usecase we pass 100 rows with a few interesting data types...
   *
   * @param dataContext the data context to optionally reference (not used here)
   * @throws LeanException
   */
  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {

    Random random = new Random(12345678);

    IRowMeta rowMeta = describeOutput(dataContext);

    List<String> sillyNames =
        Arrays.asList(
            "Adam Zapel",
            "Ali Gaither",
            "Anna Conda",
            "Anne Teak",
            "Barb Dwyer",
            "Bonnie Ann Clyde",
            "Candace Spencer",
            "Doug Hole",
            "Earl Lee Riser",
            "Kent C. Strait",
            "Jed I Knight",
            "Bug Light",
            "Chris P. Bacon",
            "Ken Hurt",
            "Ben Dover",
            "Dixie Normous",
            "Justin Slider",
            "Mike Litoris");
    List<String> colors = Arrays.asList("Red", "Green", "Blue");
    List<String> countries = Arrays.asList("Atlantis", "Sokovia", "Wakanda", "Zamunda");

    long startTime = System.currentTimeMillis();

    for (long id = 1; id <= rowCount; id++) {
      double rnd = random.nextDouble();
      int sillyId = (int) ((random.nextDouble() * id * sillyNames.size())) % sillyNames.size();

      Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
      rowData[0] = id;
      rowData[1] = sillyNames.get(sillyId);
      rowData[2] = new Date(startTime + 1000); // Just to see some change
      rowData[3] = random.nextDouble() > 0.5;
      rowData[4] = random.nextDouble();
      rowData[5] = colors.get((int) Math.round(rnd * 1000) % colors.size());
      rowData[6] = countries.get((int) Math.round(random.nextDouble() * 1000) % countries.size());

      for (ILeanRowListener rowListener : rowListeners) {
        rowListener.rowReceived(rowMeta, rowData);
      }

      sillyId++;
      if (sillyId >= sillyNames.size()) {
        sillyId = 0;
      }
    }

    // Signal to all row listeners that no more rows are forthcoming.
    //
    outputDone();
  }

  @Override
  public void waitUntilFinished() throws LeanException {
    // StartStreaming works synchronized, no need to get complicated about it
  }

  /**
   * Gets rowCount
   *
   * @return value of rowCount
   */
  public int getRowCount() {
    return rowCount;
  }

  /** @param rowCount The rowCount to set */
  public void setRowCount(int rowCount) {
    this.rowCount = rowCount;
  }
}
