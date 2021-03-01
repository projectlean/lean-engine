package org.lean.presentation.connector.types.sql;

import junit.framework.TestCase;
import org.apache.hop.core.database.DatabaseMetaPlugin;
import org.apache.hop.core.database.DatabasePluginType;
import org.apache.hop.core.database.IDatabase;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.databases.h2.H2DatabaseMeta;
import org.apache.hop.databases.mysql.MySqlDatabaseMeta;
import org.apache.hop.databases.oracle.OracleDatabaseMeta;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.LeanEnvironment;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.util.BasePresentationUtil;
import org.lean.util.TablePresentationUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LeanSqlConnectorTest extends TestCase {

  private IHopMetadataProvider metadataProvider;
  private IVariables variables;

  private static int rowCount = 50;
  private static String tableName = "SQL_TEST_TABLE";

  private LeanDatabaseConnection connection;

  @Override protected void setUp() throws Exception {

    metadataProvider = new MemoryMetadataProvider();
    variables = Variables.getADefaultVariableSpace();
    LeanEnvironment.init();

    // Add the database plugins from the test classpath
    //
    BasePresentationUtil.registerTestPlugins();

    IHopMetadataSerializer<LeanDatabaseConnection> dbSerializer = metadataProvider.getSerializer( LeanDatabaseConnection.class );

    // Create a table and put a bunch of rows in it...
    //
    connection = TablePresentationUtil.populateTestTable( variables, tableName, rowCount );
    dbSerializer.save( connection );
  }

  @Override protected void tearDown() throws Exception {
  }

  public void testStartStreaming() throws Exception {

    // Now we can reference the connection in the connector
    //
    String sql = "SELECT * FROM " + tableName;

    final LeanSqlConnector leanSqlConnector = new LeanSqlConnector( connection.getName(), sql );

    AtomicInteger rowCounter = new AtomicInteger( 0 );
    AtomicBoolean endReceived = new AtomicBoolean( false );

    leanSqlConnector.addRowListener( ( rowMeta, rowData ) -> {
      if ( rowMeta != null && rowData != null ) {
        rowCounter.incrementAndGet();
      }
      if ( rowMeta == null && rowData == null ) {
        endReceived.set( true );
      }
    } );

    IDataContext dataContext = new IDataContext() {
      @Override public LeanConnector getConnector( String name ) throws LeanException {
        return new LeanConnector(name, leanSqlConnector);
      }

      @Override public IVariables getVariables() {
        return Variables.getADefaultVariableSpace();
      }

      @Override public IHopMetadataProvider getMetadataProvider() {
        return metadataProvider;
      }
    };

    leanSqlConnector.startStreaming( dataContext );
    leanSqlConnector.waitUntilFinished();

    assertTrue( endReceived.get() );
    assertEquals( rowCount, rowCounter.get() );
  }
}