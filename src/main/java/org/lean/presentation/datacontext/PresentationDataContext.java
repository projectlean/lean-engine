package org.lean.presentation.datacontext;

import org.lean.core.Constants;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.LeanConnector;
import org.apache.hop.core.variables.VariableSpace;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metastore.api.IMetaStore;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A data context with variables
 */
public class PresentationDataContext implements IDataContext {

  private LeanPresentation presentation;

  private VariableSpace variableSpace;

  private IMetaStore metaStore;

  public PresentationDataContext( LeanPresentation presentation, IMetaStore metaStore ) {
    this.presentation = presentation;
    this.metaStore = metaStore;
    variableSpace = new Variables();

    variableSpace.setVariable( Constants.VARIABLE_PRESENTATION_NAME, presentation.getName() );
    variableSpace.setVariable( Constants.VARIABLE_PRESENTATION_DESCRIPTION, presentation.getDescription() );
    variableSpace.setVariable( Constants.VARIABLE_SYSTEM_DATE, new SimpleDateFormat("yyyy/MM/dd").format( new Date() ) );
  }


  @Override public LeanConnector getConnector( String name ) {
    LeanConnector connector = presentation.getConnector( name );

    // Create a copy every time someone asks for a connector.
    // This ensures that querying is safe
    //
    if (connector!=null) {
      connector = new LeanConnector(connector);
    }
    return connector;
  }

  /**
   * Gets presentation
   *
   * @return value of presentation
   */
  public LeanPresentation getPresentation() {
    return presentation;
  }

  /**
   * @param presentation The presentation to set
   */
  public void setPresentation( LeanPresentation presentation ) {
    this.presentation = presentation;
  }

  /**
   * Gets variableSpace
   *
   * @return value of variableSpace
   */
  @Override public VariableSpace getVariableSpace() {
    return variableSpace;
  }

  /**
   * @param variableSpace The variableSpace to set
   */
  public void setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
  }

  /**
   * Gets metaStore
   *
   * @return value of metaStore
   */
  @Override public IMetaStore getMetaStore() {
    return metaStore;
  }

  /**
   * @param metaStore The metaStore to set
   */
  public void setMetaStore( IMetaStore metaStore ) {
    this.metaStore = metaStore;
  }
}
