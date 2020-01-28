package org.lean.core.metastore;

import org.apache.commons.io.FileUtils;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.api.exceptions.MetaStoreException;
import org.apache.hop.metastore.stores.memory.MemoryMetaStore;
import org.apache.hop.metastore.stores.xml.XmlMetaStore;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class LeanMetaStoreUtil {

  public static IMetaStore createTestMetaStore( String name) throws MetaStoreException {

    /*
    String folder = System.getProperty( "java.io.tmpdir" ) + "/"+name+"/"+ UUID.randomUUID();
    new File( folder + "/metastore" ).mkdirs();
    XmlMetaStore xmlMetaStore = new XmlMetaStore( folder );
    xmlMetaStore.setName( name );
    return xmlMetaStore;
    *
    */

    MemoryMetaStore metaStore = new MemoryMetaStore();
    metaStore.setName( name );
    return metaStore;
  }

  public static void cleanupTestMetaStore( IMetaStore metaStore) throws IOException {
    if (metaStore instanceof XmlMetaStore) {
      FileUtils.deleteDirectory( new File( ((XmlMetaStore)metaStore).getRootFolder() ).getParentFile() );
    }
  }
}
