package org.lean.core.log;

public class DurationRequest {
  private String startId;
  private String finishId;
  private String message;

  public DurationRequest(String startId, String finishId, String message) {
    this.startId = startId;
    this.finishId = finishId;
    this.message = message;
  }

  /**
   * Gets startId
   *
   * @return value of startId
   */
  public String getStartId() {
    return startId;
  }

  /** @param startId The startId to set */
  public void setStartId(String startId) {
    this.startId = startId;
  }

  /**
   * Gets finishId
   *
   * @return value of finishId
   */
  public String getFinishId() {
    return finishId;
  }

  /** @param finishId The finishId to set */
  public void setFinishId(String finishId) {
    this.finishId = finishId;
  }

  /**
   * Gets message
   *
   * @return value of message
   */
  public String getMessage() {
    return message;
  }

  /** @param message The message to set */
  public void setMessage(String message) {
    this.message = message;
  }
}
