package org.lean.presentation.datacontext;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;

/**
 * This describes the context by which components get data.
 */
public interface IDataContext {

  LeanConnector getConnector( String name ) throws LeanException;

  IVariables getVariableSpace();

  IHopMetadataProvider getMetadataProvider();

}
