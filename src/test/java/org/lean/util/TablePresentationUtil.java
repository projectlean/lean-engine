package org.lean.util;

import org.apache.hop.core.Const;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.core.row.value.ValueMetaBoolean;
import org.apache.hop.core.row.value.ValueMetaDate;
import org.apache.hop.core.row.value.ValueMetaInteger;
import org.apache.hop.core.row.value.ValueMetaNumber;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanSortMethod;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.component.types.table.LeanTableComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.types.chain.LeanChainConnector;
import org.lean.presentation.connector.types.distinct.LeanDistinctConnector;
import org.lean.presentation.connector.types.passthrough.LeanPassthroughConnector;
import org.lean.presentation.connector.types.selection.LeanSelectionConnector;
import org.lean.presentation.connector.types.sort.LeanSortConnector;
import org.lean.presentation.connector.types.sql.LeanSqlConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutBuilder;
import org.lean.presentation.page.LeanPage;

import java.awt.*;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TablePresentationUtil extends BasePresentationUtil {

  public static final String CONNECTOR_NAME_SQL = "SQL rows";
  public static final String COMPONENT_NAME_LABEL = "Label1";
  public static final String CONNECTOR_NAME_PASSTHROUGH = "PassThrough";
  private static final String COMPONENT_NAME_TABLE = "Table1";
  private static final String CONNECTOR_NAME_CHAIN = "Chain";

  public TablePresentationUtil(IHopMetadataProvider metadataProvider, IVariables variables) {
    super(metadataProvider, variables);
  }

  public static final LeanDatabaseConnection populateTestTable(
      IVariables variables, String tableName, int rowCount) throws Exception {

    // Create a local H2 database in some tmp space.
    //
    String dbFolder = System.getProperty("java.io.tmpdir", ".") + Const.FILE_SEPARATOR + "testDb";
    LeanDatabaseConnection connection =
        new LeanDatabaseConnection("testDb", "H2", null, null, dbFolder, null, null);
    DatabaseMeta databaseMeta = connection.createDatabaseMeta();
    Database database =
        new Database(new LoggingObject(connection.getName()), variables, databaseMeta);

    IRowMeta rowMeta = new RowMeta();
    rowMeta.addValueMeta(new ValueMetaInteger("id"));
    rowMeta.addValueMeta(new ValueMetaString("name"));
    rowMeta.addValueMeta(new ValueMetaDate("updated"));
    rowMeta.addValueMeta(new ValueMetaBoolean("important"));
    rowMeta.addValueMeta(new ValueMetaNumber("random", 9, 4));
    rowMeta.addValueMeta(new ValueMetaString("color"));

    try {

      database.connect();

      // Drop the table...
      //
      try {
        database.execStatement("DROP TABLE " + tableName);
      } catch (Exception e) {
        // Ignore error
      }

      // Create the table for the columns...
      //
      String sql = database.getCreateTableStatement(tableName, rowMeta, null, false, null, false);
      database.execStatement(sql);

      // Some names to spread around.
      //
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

      long startTime = System.currentTimeMillis();

      database.prepareInsert(rowMeta, tableName);
      PreparedStatement prepStatementInsert = database.getPrepStatementInsert();

      // Put some random rows into the table...
      //
      Random random = new Random(12345678);
      int sillyId = 0;
      for (long id = 1; id <= rowCount; id++) {
        Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
        double rnd = random.nextDouble();

        rowData[0] = id;
        rowData[1] = sillyNames.get(sillyId);
        rowData[2] = new Date(startTime + 1000); // Just to see some change
        rowData[3] = rnd > 0.5;
        rowData[4] = rnd * (id * 2 / rowCount);
        rowData[5] = colors.get((int) Math.round(rnd * 1000) % colors.size());

        database.setValuesInsert(rowMeta, rowData);
        database.insertRow();

        sillyId++;
        if (sillyId >= sillyNames.size()) {
          sillyId = 0;
        }
      }
      database.closeInsert();

      return connection;
    } finally {
      database.disconnect();
    }
  }

  public LeanPresentation createTablePresentation(int nr) throws Exception {

    LeanPresentation presentation =
        createBasePresentation(
            "Table (" + nr + ")",
            "Table " + nr + " description",
            100,
            "A table with a label right below that",
            true);

    LeanPage pageOne = presentation.getPages().get(0);

    IHopMetadataSerializer<LeanDatabaseConnection> serializer =
        metadataProvider.getSerializer(LeanDatabaseConnection.class);

    // Create a table and put a bunch of rows in it...
    //
    String tableName = "test_table";
    int rowCount = 100;
    LeanDatabaseConnection connection =
        TablePresentationUtil.populateTestTable(variables, tableName, rowCount);
    serializer.save(connection);

    LeanDatabaseConnection steelWheels =
        H2DatabaseUtil.createSteelWheelsDatabase(metadataProvider, variables);

    // Get 100 rows in the output.
    //
    ILeanConnector sqlSource =
        new LeanSqlConnector(connection.getName(), "SELECT * FROM " + tableName);
    LeanConnector source = new LeanConnector(CONNECTOR_NAME_SQL, sqlSource);
    presentation.getConnectors().add(source);

    ILeanConnector passThrough = new LeanPassthroughConnector(source.getName());
    LeanConnector passThroughConnector = new LeanConnector(CONNECTOR_NAME_PASSTHROUGH, passThrough);

    presentation.getConnectors().add(passThroughConnector);

    List<LeanColumn> columnSelection =
        Arrays.asList(
            new LeanColumn("id", "ID", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.TOP),
            new LeanColumn("name", "Name", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP),
            new LeanColumn(
                "updated",
                "Time of update",
                LeanHorizontalAlignment.LEFT,
                LeanVerticalAlignment.TOP),
            new LeanColumn(
                "important", "Imp?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP),
            new LeanColumn(
                "color", "Color", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP),
            new LeanColumn(
                "random", "Random Nr.", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP));

    columnSelection.get(0).setFormatMask("#");
    columnSelection.get(2).setFormatMask("yyyy/MM/dd HH:mm:ss");
    columnSelection.get(4).setFormatMask("0.0000");

    LeanTableComponent table =
        new LeanTableComponent(passThroughConnector.getName(), columnSelection);
    table.setBorder(false);
    table.setHorizontalMargin(4);
    table.setVerticalMargin(2);
    table.setDefaultColor(new LeanColorRGB(80, 80, 80));
    table.setBorderColor(new LeanColorRGB(120, 120, 120));
    table.setBackground(false);
    table.setBackGroundColor(new LeanColorRGB(220, 220, 220));
    table.setGridColor(new LeanColorRGB(180, 180, 180));
    table.setDefaultFont(new LeanFont(Font.MONOSPACED, "14", false, false));
    table.setHeaderFont(new LeanFont("Arial", "16", true, false));
    table.setEvenHeights(true);
    table.setHeader(true);
    table.setHeaderOnEveryPage(true);

    LeanComponent table1 = new LeanComponent(COMPONENT_NAME_TABLE, table);
    LeanLayout tableLayout = new LeanLayout(0, 0);
    table1.setLayout(tableLayout);
    pageOne.getComponents().add(table1);

    LeanLabelComponent label = new LeanLabelComponent();
    label.setLabel("<123_ö gpĨ\"456>");
    label.setDefaultFont(new LeanFont("Courier", "48", false, false));
    label.setHorizontalAlignment(LeanHorizontalAlignment.CENTER);
    label.setVerticalAlignment(LeanVerticalAlignment.TOP);
    label.setBorder(true);
    label.setDefaultColor(new LeanColorRGB(0, 140, 194));
    label.setBorderColor(new LeanColorRGB(80, 80, 80));
    label.setBackGroundColor(new LeanColorRGB(200, 200, 200));

    LeanComponent label1 = new LeanComponent(COMPONENT_NAME_LABEL, label);
    label1.setLayout(
        new LeanLayoutBuilder().left().right().topFromBottom(COMPONENT_NAME_TABLE, 0, 30).build());

    pageOne.getComponents().add(label1);

    return presentation;
  }

  public LeanPresentation createTableChainPresentation(int nr) throws Exception {
    LeanPresentation presentation = createTablePresentation(nr);

    // Let's modify the presentation
    //
    // - Select only a few fields
    // - Sort the rows
    // - Get distinct values
    //

    // Selection
    //
    List<LeanColumn> columns = Arrays.asList(new LeanColumn("color"), new LeanColumn("important"));
    LeanSelectionConnector selection = new LeanSelectionConnector(columns);

    // Sort
    //
    List<LeanSortMethod> sorts =
        Arrays.asList(
            new LeanSortMethod(LeanSortMethod.Type.NATIVE_VALUE, true),
            new LeanSortMethod(LeanSortMethod.Type.NATIVE_VALUE, true));
    LeanSortConnector sort = new LeanSortConnector(columns, sorts);

    // Distinct
    //
    LeanDistinctConnector distinct = new LeanDistinctConnector();

    // Use a Chain to test them all at once.
    //
    LeanChainConnector chain = new LeanChainConnector();

    // Read from the pass through connector
    //
    chain.setSourceConnectorName(CONNECTOR_NAME_PASSTHROUGH);
    chain.setConnectors(Arrays.asList(selection, sort, distinct));

    LeanConnector chainConnector = new LeanConnector(CONNECTOR_NAME_CHAIN, chain);
    presentation.getConnectors().add(chainConnector);

    // Modify the Table component to read from the chain
    //
    LeanPage pageOne = presentation.getPages().get(0);
    LeanComponent tableComponent = pageOne.findComponent(COMPONENT_NAME_TABLE);
    tableComponent.getComponent().setSourceConnectorName(CONNECTOR_NAME_CHAIN);

    // Only show the 2 remaining columns
    //
    LeanTableComponent table = (LeanTableComponent) tableComponent.getComponent();
    table.setColumnSelection(
        Arrays.asList(
            new LeanColumn(
                "color", "Color", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP),
            new LeanColumn(
                "important", "Imp?", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP)));

    // Remove the label
    //
    pageOne.getComponents().remove(pageOne.findComponent(COMPONENT_NAME_LABEL));

    return presentation;
  }
}
