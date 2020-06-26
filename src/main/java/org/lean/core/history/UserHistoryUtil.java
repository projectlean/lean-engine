package org.lean.core.history;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to update or query the user history
 */
public class UserHistoryUtil {

  /**
   * This method will update the user action history for the given user and object.
   * It will also limit the history to a certain number of objects per type.
   *
   * @param metadataProvider
   * @param user
   * @param objectType
   * @param objectName
   * @throws HopException
   */
  public static final void addUserHistoryAction( IHopMetadataProvider metadataProvider, String user, String objectType, String objectName ) throws HopException {

    IHopMetadataSerializer<LeanUserHistory> serializer = metadataProvider.getSerializer( LeanUserHistory.class );

    LeanUserHistory userHistory = serializer.load( user );
    if ( userHistory == null ) {
      userHistory = new LeanUserHistory();
      userHistory.setName( user );
    }

    userHistory.getActions().add( new LeanUserHistoryAction( objectType, objectName ) );


    Map<String, Set<LeanUserHistoryAction>> typeActionsMap = new HashMap<>();

    // Remember at most 10 per type of object...
    //
    for ( LeanUserHistoryAction action : userHistory.getActions() ) {
      Set<LeanUserHistoryAction> actions = typeActionsMap.get( action.getObjectType() );
      if ( actions == null ) {
        actions = new HashSet<>();
        typeActionsMap.put( action.getObjectType(), actions );
      }
      actions.add( action );
    }

    // Now clear the actions and keep a limited amount per object type at most...
    //
    userHistory.getActions().clear();

    for ( String type : typeActionsMap.keySet() ) {
      List<LeanUserHistoryAction> actions = new ArrayList<>( typeActionsMap.get( type ) );
      // Sort the list by date reversed
      //
      Collections.sort( actions, new Comparator<LeanUserHistoryAction>() {
        @Override public int compare( LeanUserHistoryAction o1, LeanUserHistoryAction o2 ) {
          return -o1.getActionDate().compareTo( o2.getActionDate() );
        }
      } );


      for ( int i = 0; i < actions.size() && i < Constants.USER_ACTION_HISTORY_SIZE; i++ ) {
        userHistory.getActions().add( actions.get( i ) );
      }
    }

    serializer.save( userHistory );
  }


  public static final List<LeanUserHistoryAction> getUserHistoryActions( IHopMetadataProvider metadataProvider, String user ) throws HopException {
    LeanUserHistory userHistory = metadataProvider.getSerializer( LeanUserHistory.class ).load( user );
    if ( userHistory == null ) {
      return new ArrayList<>();
    }
    return userHistory.getActions();
  }
}
