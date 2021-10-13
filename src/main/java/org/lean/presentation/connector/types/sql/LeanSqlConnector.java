package org.lean.presentation.connector.types.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

import java.sql.ResultSet;

@JsonDeserialize(as = LeanSqlConnector.class)
@LeanConnectorPlugin(
    id = "SqlConnector",
    name = "Execute a SQL query",
    description = "Reads data from a relational database using a SQL query")
public class LeanSqlConnector extends LeanBaseConnector implements ILeanConnector {

  @HopMetadataProperty private String databaseConnectionName;

  @HopMetadataProperty private String sql;

  @JsonIgnore private transient ResultSet resultSet;

  public LeanSqlConnector() {
    super("SqlConnector");
  }

  public LeanSqlConnector(String databaseConnectionName, String sql) {
    this();
    this.databaseConnectionName = databaseConnectionName;
    this.sql = sql;
  }

  public LeanSqlConnector(LeanSqlConnector c) {
    super(c);
    this.databaseConnectionName = c.databaseConnectionName;
    this.sql = c.sql;
  }

  public LeanSqlConnector clone() {
    return new LeanSqlConnector(this);
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) throws LeanException {
    Database database = null;

    try {
      IHopMetadataSerializer<LeanDatabaseConnection> serializer =
          dataContext.getMetadataProvider().getSerializer(LeanDatabaseConnection.class);
      LeanDatabaseConnection databaseConnection = serializer.load(databaseConnectionName);

      DatabaseMeta databaseMeta = databaseConnection.createDatabaseMeta();
      database =
          new Database(
              new LoggingObject("Database connection '" + databaseConnectionName + "'"),
              dataContext.getVariables(),
              databaseMeta);
      database.connect();

      IRowMeta rowMeta = database.getQueryFields(sql, false);

      return rowMeta;
    } catch (Exception e) {
      throw new LeanException("Unable to describe output of SQL query", e);
    } finally {
      if (database != null) {
        database.disconnect();
      }
    }
  }

  /**
   * For the sampledata usecase we pass 100 rows with a few interesting data types...
   *
   * @param dataContext the data context to optionally reference (not used here)
   * @throws LeanException
   */
  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {

    Database database = null;
    try {
      IHopMetadataSerializer<LeanDatabaseConnection> serializer =
          dataContext.getMetadataProvider().getSerializer(LeanDatabaseConnection.class);
      LeanDatabaseConnection databaseConnection = serializer.load(databaseConnectionName);

      DatabaseMeta databaseMeta = databaseConnection.createDatabaseMeta();

      database =
          new Database(
              new LoggingObject("Database connection '" + databaseConnectionName + "'"),
              dataContext.getVariables(),
              databaseMeta);
      database.connect();

      resultSet = database.openQuery(sql);
      Object[] row = database.getRow(resultSet);
      while (row != null) {
        passToRowListeners(database.getReturnRowMeta(), row);
        row = database.getRow(resultSet);
      }
      database.closeQuery(resultSet);

      // Signal to all row listeners (and subsequent connectors) that no more rows are forthcoming .
      //
      outputDone();

    } catch (Exception e) {
      throw new LeanException(
          "Couldn't stream data from database connection " + databaseConnectionName, e);
    } finally {
      if (database != null) {
        database.disconnect();
      }
    }
  }

  @Override
  public void waitUntilFinished() throws LeanException {
    // StartStreaming works synchronized, no need to get complicated about it
  }

  /**
   * Gets databaseConnectionName
   *
   * @return value of databaseConnectionName
   */
  public String getDatabaseConnectionName() {
    return databaseConnectionName;
  }

  /** @param databaseConnectionName The databaseConnectionName to set */
  public void setDatabaseConnectionName(String databaseConnectionName) {
    this.databaseConnectionName = databaseConnectionName;
  }

  /**
   * Gets sql
   *
   * @return value of sql
   */
  public String getSql() {
    return sql;
  }

  /** @param sql The sql to set */
  public void setSql(String sql) {
    this.sql = sql;
  }
}
