package org.lean.presentation.datacontext;

import org.lean.core.Constants;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.layout.LeanRenderPage;
import org.apache.hop.core.variables.VariableSpace;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metastore.api.IMetaStore;

public class RenderPageDataContext implements IDataContext {

  private IDataContext parentDataContext;
  private LeanRenderPage renderPage;

  private VariableSpace variableSpace;

  public RenderPageDataContext( IDataContext parentDataContext, LeanRenderPage renderPage) {
    this.parentDataContext = parentDataContext;
    this.renderPage = renderPage;

    variableSpace = new Variables();
    variableSpace.copyVariablesFrom( parentDataContext.getVariableSpace() );

    // Inject page specific variables
    //
    variableSpace.setVariable( Constants.VARIABLE_PAGE_NUMBER, Integer.toString(renderPage.getPageNumber()) );
  }


  @Override public LeanConnector getConnector( String name ) throws LeanException {
    LeanConnector connector = parentDataContext.getConnector( name );

    // Create a copy every time someone asks for a connector.
    // This ensures that querying is safe
    //
    if (connector!=null) {
      connector = new LeanConnector(connector);
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
  @Override public VariableSpace getVariableSpace() {
    return variableSpace;
  }

  /**
   * @param variableSpace The variableSpace to set
   */
  public void setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
  }

  @Override public IMetaStore getMetaStore() {
    return parentDataContext.getMetaStore();
  }
}
