package org.lean.core.exception;

public class LeanException extends Exception {
  private static final long serialVersionUID = -2472634745866870891L;

  public LeanException() {
    super();
  }

  public LeanException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public LeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public LeanException(String message) {
    super(message);
  }

  public LeanException(Throwable cause) {
    super(cause);
  }
}
