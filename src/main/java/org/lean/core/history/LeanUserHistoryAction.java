package org.lean.core.history;

import org.apache.hop.metastore.persist.MetaStoreAttribute;

import java.util.Date;
import java.util.Objects;

public class LeanUserHistoryAction {
  @MetaStoreAttribute
  private String objectType;

  @MetaStoreAttribute
  private String objectName;

  @MetaStoreAttribute
  private Date actionDate;

  public LeanUserHistoryAction() {
    actionDate = new Date();
  }

  public LeanUserHistoryAction( String objectType, String objectName ) {
    this(objectType, objectName, new Date());
  }

  public LeanUserHistoryAction( String objectType, String objectName, Date actionDate ) {
    this.objectType = objectType;
    this.objectName = objectName;
    this.actionDate = actionDate;
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    LeanUserHistoryAction that = (LeanUserHistoryAction) o;
    return objectType.equals( that.objectType ) &&
      objectName.equals( that.objectName );
  }

  @Override public int hashCode() {
    return Objects.hash( objectType, objectName );
  }

  /**
   * Gets objectType
   *
   * @return value of objectType
   */
  public String getObjectType() {
    return objectType;
  }

  /**
   * @param objectType The objectType to set
   */
  public void setObjectType( String objectType ) {
    this.objectType = objectType;
  }

  /**
   * Gets objectName
   *
   * @return value of objectName
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * @param objectName The objectName to set
   */
  public void setObjectName( String objectName ) {
    this.objectName = objectName;
  }

  /**
   * Gets actionDate
   *
   * @return value of actionDate
   */
  public Date getActionDate() {
    return actionDate;
  }

  /**
   * @param actionDate The actionDate to set
   */
  public void setActionDate( Date actionDate ) {
    this.actionDate = actionDate;
  }
}
