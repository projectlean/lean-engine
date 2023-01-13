package org.lean.presentation.connector.types.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMetaBuilder;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

@JsonDeserialize(as = LeanMetadataPresentationsConnector.class)
@LeanConnectorPlugin(
    id = "MetadataPresentationsConnector",
    name = "Presentations list",
    description = "Lists the available presentations")
public class LeanMetadataPresentationsConnector extends LeanBaseConnector
    implements ILeanConnector {

  public LeanMetadataPresentationsConnector() {
    super("MetadataPresentationsConnector");
  }

  public LeanMetadataPresentationsConnector(LeanMetadataPresentationsConnector c) {
    super(c);
  }

  public LeanMetadataPresentationsConnector clone() {
    return new LeanMetadataPresentationsConnector(this);
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) throws LeanException {
    return new RowMetaBuilder().addString("name").addString("description").build();
  }

  /**
   * Output the names of the elements for the given key
   *
   * @param dataContext the data context to reference
   * @throws LeanException
   */
  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {
    IRowMeta rowMeta = describeOutput(dataContext);

    try {
      IHopMetadataProvider provider = dataContext.getMetadataProvider();
      IHopMetadataSerializer<LeanPresentation> serializer =
          provider.getSerializer(LeanPresentation.class);

      List<String> names = serializer.listObjectNames();

      for (String name : names) {
        LeanPresentation presentation = serializer.load(name);

        Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
        rowData[0] = name;
        rowData[1] = presentation.getDescription();

        for (ILeanRowListener rowListener : rowListeners) {
          rowListener.rowReceived(rowMeta, rowData);
        }
      }
    } catch (Exception e) {
      throw new LeanException("Error writing presentation metadata output", e);
    }

    // Signal to all row listeners that no more rows are forthcoming.
    //
    outputDone();
  }

  @Override
  public void waitUntilFinished() throws LeanException {
    // StartStreaming works synchronized, no need to get complicated about it
  }
}
