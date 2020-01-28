package org.lean.core.metastore;

import org.apache.hop.metastore.api.IMetaStore;

import java.util.HashMap;
import java.util.Map;

public class LeanMetaStore {
  private static IMetaStore metaStore = null;
  private static Map<Class<?>, MetaStoreFactory<?>> factories;

  public static void init( IMetaStore metaStore ) {
    LeanMetaStore.metaStore = metaStore;
    LeanMetaStore.factories = new HashMap<>();
  }

  public static IMetaStore getMetaStore() {
    return metaStore;
  }

  public static void registerFactory( Class<?> clazz, MetaStoreFactory<?> factory ) {
    factories.put( clazz, factory );
  }

  public static MetaStoreFactory<?> getFactory( Class<?> clazz ) {
    return factories.get( clazz );
  }
}
