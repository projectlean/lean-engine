package org.lean.presentation.connector.types.sql;

import org.lean.core.LeanDatabaseConnection;
import org.lean.core.LeanEnvironment;
import org.lean.core.metastore.LeanMetaStore;
import org.lean.core.metastore.LeanMetaStoreUtil;
import org.lean.core.metastore.MetaStoreFactory;
import org.lean.util.TablePresentationUtil;
import junit.framework.TestCase;
import org.apache.hop.metastore.api.IMetaStore;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LeanSqlConnectorTest extends TestCase {

  private IMetaStore metaStore;

  private static int rowCount = 50;
  private static String tableName = "SQL_TEST_TABLE";

  private LeanDatabaseConnection connection;

  @Override protected void setUp() throws Exception {

    metaStore = LeanMetaStoreUtil.createTestMetaStore( "Test" );
    LeanEnvironment.init( metaStore );
    MetaStoreFactory<LeanDatabaseConnection> dbFactory = (MetaStoreFactory<LeanDatabaseConnection>) LeanMetaStore.getFactory( LeanDatabaseConnection.class );

    // Create a table and put a bunch of rows in it...
    //
    connection = TablePresentationUtil.populateTestTable( tableName, rowCount );
    dbFactory.saveElement( connection );
  }

  @Override protected void tearDown() throws Exception {
    LeanMetaStoreUtil.cleanupTestMetaStore( metaStore );
  }

  public void testStartStreaming() throws Exception {

    // Now we can reference the connection in the connector
    //
    String sql = "SELECT * FROM "+tableName;

    LeanSqlConnector leanSqlConnector = new LeanSqlConnector( connection.getName(), sql );

    AtomicInteger rowCounter = new AtomicInteger( 0 );
    AtomicBoolean endReceived = new AtomicBoolean( false );

    leanSqlConnector.addRowListener( (rowMeta, rowData) -> {
      if (rowMeta!=null && rowData!=null) {
        rowCounter.incrementAndGet();
      }
      if (rowMeta==null && rowData==null) {
        endReceived.set( true );
      }
    } );

    leanSqlConnector.startStreaming( null );
    leanSqlConnector.waitUntilFinished();

    assertTrue( endReceived.get() );
    assertEquals( rowCount, rowCounter.get() );
  }
}