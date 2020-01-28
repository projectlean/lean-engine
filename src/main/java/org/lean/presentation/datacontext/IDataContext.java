package org.lean.presentation.datacontext;

import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.apache.hop.core.variables.VariableSpace;
import org.apache.hop.metastore.api.IMetaStore;

/**
 * This describes the context by which components get data.
 *
 */
public interface IDataContext {

  LeanConnector getConnector( String name) throws LeanException;

  VariableSpace getVariableSpace();

  IMetaStore getMetaStore();

}
