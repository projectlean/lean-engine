package org.lean.rest;

import javax.servlet.ServletContext;

public class LeanServletContext {
  private static LeanServletContext leanServletContext;

  private ServletContext context;

  private LeanServletContext(ServletContext context) {
    this.context = context;
  }

  public static void initialize(ServletContext context) {
    leanServletContext = new LeanServletContext( context );
  }

  public static ServletContext getInstance() {
    if (!isInitialized()) {
      throw new RuntimeException( "Servlet Context is not initialized" );
    }
    return leanServletContext.context;
  }

  public static boolean isInitialized() {
    return leanServletContext!=null;
  }
}
