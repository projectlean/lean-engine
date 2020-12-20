package org.lean.presentation.datacontext;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.Constants;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.layout.LeanRenderPage;

public class RenderPageDataContext implements IDataContext {

  private IDataContext parentDataContext;
  private LeanRenderPage renderPage;

  private IVariables variableSpace;

  public RenderPageDataContext( IDataContext parentDataContext, LeanRenderPage renderPage ) {
    this.parentDataContext = parentDataContext;
    this.renderPage = renderPage;

    variableSpace = new Variables();
    variableSpace.copyFrom( parentDataContext.getVariableSpace() );

    // Inject page specific variables
    //
    variableSpace.setVariable( Constants.VARIABLE_PAGE_NUMBER, Integer.toString( renderPage.getPageNumber() ) );
  }


  @Override public LeanConnector getConnector( String name ) throws LeanException {
    LeanConnector connector = parentDataContext.getConnector( name );

    // Create a copy every time someone asks for a connector.
    // This ensures that querying is safe
    //
    if ( connector != null ) {
      connector = new LeanConnector( connector );
    }
    return connector;
  }

  /**
   * Gets renderPage
   *
   * @return value of renderPage
   */
  public LeanRenderPage getRenderPage() {
    return renderPage;
  }

  /**
   * @param renderPage The renderPage to set
   */
  public void setRenderPage( LeanRenderPage renderPage ) {
    this.renderPage = renderPage;
  }

  /**
   * Gets parentDataContext
   *
   * @return value of parentDataContext
   */
  public IDataContext getParentDataContext() {
    return parentDataContext;
  }

  /**
   * @param parentDataContext The parentDataContext to set
   */
  public void setParentDataContext( IDataContext parentDataContext ) {
    this.parentDataContext = parentDataContext;
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

  @Override public IHopMetadataProvider getMetadataProvider() {
    return parentDataContext.getMetadataProvider();
  }
}
