package org.lean.core;

import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.HopMetadataBase;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;
import org.lean.core.exception.LeanException;
import org.lean.core.metastore.IHasIdentity;

import javax.ws.rs.Path;

/** For now we assume sane defaults like JDBC, no generic connections, ... */
@HopMetadata(
    key = "lean-database-connection",
    name = "Lean Database Connection",
    description = "A description of a connection to a relational database")
@Path("databases")
public class LeanDatabaseConnection extends HopMetadataBase implements IHopMetadata, IHasIdentity {

  /** This is a reference to Hop database type codes (MYSQL, POSTGRESQL, MSSQL, ORACLE, ...) */
  @HopMetadataProperty private String databaseTypeCode;

  @HopMetadataProperty private String hostname;

  @HopMetadataProperty private String port;

  @HopMetadataProperty private String databaseName;

  @HopMetadataProperty private String username;

  @HopMetadataProperty(password = true)
  private String password;

  public LeanDatabaseConnection() {}

  public LeanDatabaseConnection(
      String name,
      String databaseTypeCode,
      String hostname,
      String port,
      String databaseName,
      String username,
      String password) {
    this();
    this.name = name;
    this.databaseTypeCode = databaseTypeCode;
    this.hostname = hostname;
    this.port = port;
    this.databaseName = databaseName;
    this.username = username;
    this.password = password;
  }

  public DatabaseMeta createDatabaseMeta() throws LeanException {
    try {
      return new DatabaseMeta(
          name, databaseTypeCode, "JDBC", hostname, databaseName, port, username, password);
    } catch (Exception e) {
      throw new LeanException("Unable to create (convert to) Hop database connection object", e);
    }
  }

  /**
   * Gets databaseTypeCode
   *
   * @return value of databaseTypeCode
   */
  public String getDatabaseTypeCode() {
    return databaseTypeCode;
  }

  /** @param databaseTypeCode The databaseTypeCode to set */
  public void setDatabaseTypeCode(String databaseTypeCode) {
    this.databaseTypeCode = databaseTypeCode;
  }

  /**
   * Gets hostname
   *
   * @return value of hostname
   */
  public String getHostname() {
    return hostname;
  }

  /** @param hostname The hostname to set */
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  /**
   * Gets port
   *
   * @return value of port
   */
  public String getPort() {
    return port;
  }

  /** @param port The port to set */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * Gets databaseName
   *
   * @return value of databaseName
   */
  public String getDatabaseName() {
    return databaseName;
  }

  /** @param databaseName The databaseName to set */
  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  /**
   * Gets username
   *
   * @return value of username
   */
  public String getUsername() {
    return username;
  }

  /** @param username The username to set */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets password
   *
   * @return value of password
   */
  public String getPassword() {
    return password;
  }

  /** @param password The password to set */
  public void setPassword(String password) {
    this.password = password;
  }
}
