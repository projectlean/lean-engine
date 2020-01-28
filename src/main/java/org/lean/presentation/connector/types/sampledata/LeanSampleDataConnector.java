package org.lean.presentation.connector.types.sampledata;

import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.datacontext.IDataContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.core.row.RowMetaInterface;
import org.apache.hop.core.row.value.ValueMetaBoolean;
import org.apache.hop.core.row.value.ValueMetaDate;
import org.apache.hop.core.row.value.ValueMetaInteger;
import org.apache.hop.core.row.value.ValueMetaNumber;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.metastore.persist.MetaStoreAttribute;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@JsonDeserialize( as = LeanSampleDataConnector.class )
public class LeanSampleDataConnector extends LeanBaseConnector implements ILeanConnector {

  @MetaStoreAttribute
  private int rowCount;

  public LeanSampleDataConnector() {
    this(100);
  }

  public LeanSampleDataConnector( int rowCount ) {
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

  @Override public RowMetaInterface describeOutput( IDataContext dataContext ) {

    RowMetaInterface rowMeta = new RowMeta();

    rowMeta.addValueMeta(new ValueMetaInteger( "id" ) );
    rowMeta.addValueMeta(new ValueMetaString( "name" ) );
    rowMeta.addValueMeta(new ValueMetaDate( "updated" ) );
    rowMeta.addValueMeta(new ValueMetaBoolean( "important" ) );
    rowMeta.addValueMeta(new ValueMetaNumber( "random" ) );
    rowMeta.addValueMeta(new ValueMetaString( "color" ) );
    rowMeta.addValueMeta(new ValueMetaString( "country" ) );

    return rowMeta;
  }

  /**
   * For the sampledata usecase we pass 100 rows with a few interesting data types...
   * @param dataContext the data context to optionally reference (not used here)
   * @throws LeanException
   */
  @Override public void startStreaming(IDataContext dataContext) throws LeanException {

    Random random = new Random(12345678);

    RowMetaInterface rowMeta = describeOutput( dataContext );

    List<String> sillyNames = Arrays.asList("Adam Zapel", "Ali Gaither", "Anna Conda", "Anne Teak", "Barb Dwyer", "Bonnie Ann Clyde",
      "Candace Spencer", "Doug Hole", "Earl Lee Riser", "Kent C. Strait", "Jed I Knight", "Bug Light", "Chris P. Bacon", "Ken Hurt", "Ben Dover", "Dixie Normous", "Justin Slider", "Mike Litoris" );
    List<String> colors = Arrays.asList("Red", "Green", "Blue");
    List<String> countries = Arrays.asList( "Atlantis", "Sokovia", "Wakanda", "Zamunda" );

    long startTime = System.currentTimeMillis();

    for (long id=1;id<=rowCount;id++) {
      double rnd = random.nextDouble();
      int sillyId = (int)((random.nextDouble() * id * sillyNames.size()))%sillyNames.size();

      Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
      rowData[0] = id;
      rowData[1] = sillyNames.get(sillyId);
      rowData[2] = new Date(startTime+1000); // Just to see some change
      rowData[3] = random.nextDouble()>0.5;
      rowData[4] = random.nextDouble();
      rowData[5] = colors.get((int)Math.round(rnd*1000)%colors.size());
      rowData[6] = countries.get((int)Math.round(random.nextDouble()*1000)%countries.size());

      for ( ILeanRowListener rowListener : rowListeners) {
        rowListener.rowReceived( rowMeta, rowData );
      }

      sillyId++;
      if (sillyId>=sillyNames.size()) {
        sillyId=0;
      }
    }

    // Signal to all row listeners that no more rows are forthcoming.
    //
    outputDone();

  }

  @Override public void waitUntilFinished() throws LeanException {
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

  /**
   * @param rowCount The rowCount to set
   */
  public void setRowCount( int rowCount ) {
    this.rowCount = rowCount;
  }
}
