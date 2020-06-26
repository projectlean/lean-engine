package org.lean.presentation.datacontext;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.Constants;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.LeanConnector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A data context with variables
 */
public class PresentationDataContext implements IDataContext {

  private LeanPresentation presentation;

  private IVariables variableSpace;

  private IHopMetadataProvider metadataProvider;

  public PresentationDataContext( LeanPresentation presentation, IHopMetadataProvider metadataProvider ) {
    this.presentation = presentation;
    this.metadataProvider = metadataProvider;
    variableSpace = new Variables();

    variableSpace.setVariable( Constants.VARIABLE_PRESENTATION_NAME, presentation.getName() );
    variableSpace.setVariable( Constants.VARIABLE_PRESENTATION_DESCRIPTION, presentation.getDescription() );
    variableSpace.setVariable( Constants.VARIABLE_SYSTEM_DATE, new SimpleDateFormat( "yyyy/MM/dd" ).format( new Date() ) );
  }


  @Override public LeanConnector getConnector( String name ) {
    LeanConnector connector = presentation.getConnector( name );

    // Create a copy every time someone asks for a connector.
    // This ensures that querying is safe
    //
    if ( connector != null ) {
      connector = new LeanConnector( connector );
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
  @Override public IVariables getVariableSpace() {
    return variableSpace;
  }

  /**
   * @param variableSpace The variableSpace to set
   */
  public void setVariableSpace( IVariables variableSpace ) {
    this.variableSpace = variableSpace;
  }

  /**
   * Gets metadataProvider
   *
   * @return value of metadataProvider
   */
  public IHopMetadataProvider getMetadataProvider() {
    return metadataProvider;
  }

  /**
   * @param metadataProvider The metadataProvider to set
   */
  public void setMetadataProvider( IHopMetadataProvider metadataProvider ) {
    this.metadataProvider = metadataProvider;
  }
}
