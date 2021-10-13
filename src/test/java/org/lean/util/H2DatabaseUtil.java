package org.lean.util;

import org.apache.commons.io.FileUtils;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.exception.LeanException;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class H2DatabaseUtil {

  public static final String CONNECTOR_STEEL_WHEELS_NAME = "SteelWheels";

  /**
   * Create and populate the SteelWheels database
   *
   * @param metadataProvider The metadata provider to save the database connection in
   * @return The created/populated SteelWheels database connection
   */
  public static final LeanDatabaseConnection createSteelWheelsDatabase(
      IHopMetadataProvider metadataProvider, IVariables variables) throws LeanException {
    try {

      String h2DatabaseName =
          System.getProperty("java.io.tmpdir") + File.separator + CONNECTOR_STEEL_WHEELS_NAME;

      LeanDatabaseConnection connection = new LeanDatabaseConnection();
      connection.setDatabaseTypeCode("H2");
      connection.setName(CONNECTOR_STEEL_WHEELS_NAME);
      connection.setDatabaseName(h2DatabaseName);

      IHopMetadataSerializer<LeanDatabaseConnection> serializer =
          metadataProvider.getSerializer(LeanDatabaseConnection.class);
      serializer.save(connection);

      // Delete old database
      //
      File[] files =
          new File(System.getProperty("java.io.tmpdir"))
              .listFiles(
                  new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                      return pathname.toString().endsWith(".db")
                          && pathname.toString().contains(CONNECTOR_STEEL_WHEELS_NAME);
                    }
                  });
      for (File file : files) {
        FileUtils.forceDelete(file);
      }

      // Read the script
      //
      List<String> lines =
          Files.readAllLines(
              Paths.get("src/test/resources/steelwheels/steelwheels.script"),
              StandardCharsets.UTF_8);

      DatabaseMeta databaseMeta = connection.createDatabaseMeta();
      databaseMeta.setForcingIdentifiersToUpperCase(true);
      Database database =
          new Database(new LoggingObject(connection.getName()), variables, databaseMeta);
      try {
        database.connect();

        for (String line : lines) {
          database.execStatement(line);
        }

      } finally {
        database.disconnect();
      }

      return connection;
    } catch (Exception e) {
      throw new LeanException(
          "Unable to create/populate database " + CONNECTOR_STEEL_WHEELS_NAME, e);
    }
  }
}
