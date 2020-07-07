package org.lean.presentation.datacontext;

import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.types.chain.LeanChainConnector;
import org.lean.presentation.connector.types.filter.LeanSimpleFilterConnector;
import org.lean.presentation.connector.types.filter.SimpleFilterValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A data context for a group.
 * This means we're automatically setting variables to use in labels
 */
public class GroupDataContext implements IDataContext {

  private final IDataContext parentDataContext;
  private final RowMetaAndData groupRow;
  private IVariables variableSpace;

  public GroupDataContext( IDataContext parentDataContext, RowMetaAndData groupRow ) throws LeanException {
    this.parentDataContext = parentDataContext;
    this.groupRow = groupRow;

    variableSpace = new Variables();
    variableSpace.initializeVariablesFrom( parentDataContext.getVariableSpace() );

    // Set variables with the names of the fields in the group
    //
    for ( int i = 0; i < groupRow.getRowMeta().size(); i++ ) {
      IValueMeta groupRowValue = groupRow.getRowMeta().getValueMetaList().get( i );
      try {
        String value = groupRow.getString( i, "" );
        String variable = groupRowValue.getName().replace( " ", "_" );

        variableSpace.setVariable( variable, value );
      } catch ( HopException e ) {
        throw new LeanException( "Error converting group value '" + groupRowValue.getName() + "' to String", e );
      }
    }
  }


  @Override public LeanConnector getConnector( String name ) throws LeanException {

    // The component asks for the connector to read from.
    // We'll look up the connector in the parent.
    //
    LeanConnector parentConnector = parentDataContext.getConnector( name );
    if ( parentConnector == null ) {
      // Can't find it, give up immediately
      //
      return null;
    }

    // Copy it for safety
    //
    parentConnector = new LeanConnector( parentConnector );

    // Now we'll see if any of the columns in the group match the parent connector output
    //
    IRowMeta parentConnectorRowMeta = parentConnector.describeOutput( parentDataContext );

    // See if there are any filtered values to apply to the input using the given group row
    //
    List<SimpleFilterValue> filterValues = new ArrayList<>();
    for ( IValueMeta groupValueMeta : groupRow.getRowMeta().getValueMetaList() ) {
      IValueMeta parentConnectorValueMeta = parentConnectorRowMeta.searchValueMeta( groupValueMeta.getName() );
      if ( parentConnectorValueMeta != null ) {
        // Apply this as a simple filter value...
        //
        String fieldName = groupValueMeta.getName();
        try {
          String filterValue = groupRow.getString( fieldName, null );
          filterValues.add( new SimpleFilterValue( groupValueMeta.getName(), filterValue ) );
        } catch ( HopException e ) {
          throw new LeanException( "Error converting group row field name '" + fieldName + "' to String", e );
        }
      }
    }

    if ( !filterValues.isEmpty() ) {

      // Create the simple filter connector
      //
      LeanSimpleFilterConnector simpleFilterConnector = new LeanSimpleFilterConnector( filterValues );

      // Create a Chain data connector...
      // We give it the same name as the parent connector
      // We can only do this in the data context itself (this class)
      //
      LeanChainConnector chainConnector = new LeanChainConnector( name, Arrays.asList( parentConnector.getConnector(), simpleFilterConnector ) );
      LeanConnector connector = new LeanConnector( name, chainConnector );
      return connector;
    }

    // Simply don't bother filtering, return the parent connector
    //
    return parentConnector;
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
