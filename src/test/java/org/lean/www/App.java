package org.lean.www;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.lean.core.LeanEnvironment;
import org.lean.www.servlets.GetPresentationDrawnItemServlet;
import org.lean.www.servlets.GetPresentationSvgServlet;
import org.lean.www.servlets.RootServlet;

public class App {
  public static void main(String[] args) throws Exception {
    // Boot up Lean...
    //
    LeanEnvironment.init();

    // Root contexts
    //
    ServletContextHandler contextHandler = new ServletContextHandler( ServletContextHandler.SESSIONS);

    // Add the contextHandler servlet
    //
    RootServlet rootServlet = new RootServlet();
    contextHandler.addServlet(new ServletHolder(rootServlet), RootServlet.CONTEXT_PATH);

    GetPresentationSvgServlet getPresentationSvgServlet = new GetPresentationSvgServlet();
    contextHandler.addServlet(new ServletHolder(getPresentationSvgServlet), GetPresentationSvgServlet.CONTEXT_PATH);

    GetPresentationDrawnItemServlet getPresentationDrawnItemServlet = new GetPresentationDrawnItemServlet();
    contextHandler.addServlet(new ServletHolder(getPresentationDrawnItemServlet), GetPresentationDrawnItemServlet.CONTEXT_PATH);


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
