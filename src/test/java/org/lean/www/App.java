package org.lean.www;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Ignore;
import org.lean.core.LeanEnvironment;
import org.lean.www.servlets.GetPresentationDrawnInfoServlet;
import org.lean.www.servlets.GetPresentationSvgServlet;
import org.lean.www.servlets.RootServlet;

@Ignore
public class App {
  public static void main(String[] args) throws Exception {
    // Boot up Lean...
    //
    LeanEnvironment.init();

    // Root contexts
    //
    ServletContextHandler contextHandler =
        new ServletContextHandler(ServletContextHandler.SESSIONS);

    // Add the contextHandler servlet
    //
    RootServlet rootServlet = new RootServlet();
    contextHandler.addServlet(new ServletHolder(rootServlet), RootServlet.CONTEXT_PATH);

    GetPresentationSvgServlet getPresentationSvgServlet = new GetPresentationSvgServlet();
    contextHandler.addServlet(
        new ServletHolder(getPresentationSvgServlet), GetPresentationSvgServlet.CONTEXT_PATH);

    GetPresentationDrawnInfoServlet getPresentationDrawnInfoServlet =
        new GetPresentationDrawnInfoServlet();
    contextHandler.addServlet(
        new ServletHolder( getPresentationDrawnInfoServlet ),
        GetPresentationDrawnInfoServlet.CONTEXT_PATH);

    Server jettyServer = new Server(8080);
    jettyServer.setHandler(contextHandler);

    try {
      jettyServer.start();
      jettyServer.join();
    } finally {
      jettyServer.destroy();
    }
  }
}
