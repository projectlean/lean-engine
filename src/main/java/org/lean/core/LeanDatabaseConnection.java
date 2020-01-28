package org.lean.core;

import org.lean.core.exception.LeanException;
import org.lean.core.metastore.IHasIdentity;
import org.lean.rest.LeanRestBase;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.metastore.persist.MetaStoreAttribute;
import org.apache.hop.metastore.persist.MetaStoreElementType;

import javax.ws.rs.Path;

/**
 * For now we assume sane defaults like JDBC, no generic connections, ...
 *
 */
@MetaStoreElementType(
  name = "Lean Database Connection",
  description = "A description of a connection to a relational database")
@Path( "databases" )
public class LeanDatabaseConnection extends LeanRestBase<LeanDatabaseConnection> implements IHasIdentity {
  private String name;

  /**
   * This is a reference to Hop database type codes (MYSQL, POSTGRESQL, MSSQL, ORACLE, ...)
   */
  @MetaStoreAttribute
  private String databaseTypeCode;

  @MetaStoreAttribute
  private String hostname;

  @MetaStoreAttribute
  private String port;

  @MetaStoreAttribute
  private String databaseName;

  @MetaStoreAttribute
  private String username;

  @MetaStoreAttribute(password = true)
  private String password;

  public LeanDatabaseConnection() {
    super(LeanDatabaseConnection.class, "Database Connection");
  }

  public LeanDatabaseConnection( String name, String databaseTypeCode, String hostname, String port, String databaseName, String username, String password ) {
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
      return new DatabaseMeta( name, databaseTypeCode, "JDBC", hostname, databaseName, port, username, password );
    } catch(Exception e) {
      throw new LeanException( "Unable to create (convert to) Hop database connection object", e );
    }
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets databaseTypeCode
   *
   * @return value of databaseTypeCode
   */
  public String getDatabaseTypeCode() {
    return databaseTypeCode;
  }

  /**
   * @param databaseTypeCode The databaseTypeCode to set
   */
  public void setDatabaseTypeCode( String databaseTypeCode ) {
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

  /**
   * @param hostname The hostname to set
   */
  public void setHostname( String hostname ) {
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

  /**
   * @param port The port to set
   */
  public void setPort( String port ) {
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

  /**
   * @param databaseName The databaseName to set
   */
  public void setDatabaseName( String databaseName ) {
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

  /**
   * @param username The username to set
   */
  public void setUsername( String username ) {
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

  /**
   * @param password The password to set
   */
  public void setPassword( String password ) {
    this.password = password;
  }
}
