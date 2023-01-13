package org.lean.presentation.connector.types.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMetaBuilder;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

@JsonDeserialize(as = LeanMetadataTypesConnector.class)
@LeanConnectorPlugin(
    id = "MetadataTypesConnector",
    name = "Metadata types",
    description = "Lists the available metadata types")
public class LeanMetadataTypesConnector extends LeanBaseConnector implements ILeanConnector {

  public LeanMetadataTypesConnector() {
    super("MetadataTypesConnector");
  }

  public LeanMetadataTypesConnector(LeanMetadataTypesConnector c) {
    super(c);
  }

  public LeanMetadataTypesConnector clone() {
    return new LeanMetadataTypesConnector(this);
  }

  @Override
  public IRowMeta describeOutput(IDataContext dataContext) throws LeanException {
    return new RowMetaBuilder()
        .addString("key")
        .addString("description")
        .addInteger("elementCount")
        .build();
  }

  /**
   * We simply output the available metadata types: key, description and number of elements.
   *
   * @param dataContext the data context to reference
   * @throws LeanException
   */
  @Override
  public void startStreaming(IDataContext dataContext) throws LeanException {
    IRowMeta rowMeta = describeOutput(dataContext);

    try {
      IHopMetadataProvider provider = dataContext.getMetadataProvider();
      List<Class<IHopMetadata>> metadataClasses = provider.getMetadataClasses();
      for (Class<IHopMetadata> metadataClass : metadataClasses) {

        HopMetadata metadata = metadataClass.getAnnotation(HopMetadata.class);
        if (metadata==null) {
          continue;
        }
        IHopMetadataSerializer<IHopMetadata> serializer = provider.getSerializer(metadataClass);

        Object[] rowData = RowDataUtil.allocateRowData(rowMeta.size());
        rowData[0] = metadata.key();
        rowData[1] = metadata.description();
        rowData[2] = (long) serializer.listObjectNames().size();

        for (ILeanRowListener rowListener : rowListeners) {
          rowListener.rowReceived(rowMeta, rowData);
        }
      }
    } catch (Exception e) {
      throw new LeanException("Error writing metadata types output", e);
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
