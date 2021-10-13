/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.lean.www.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class RootServlet extends HttpServlet {
  public static final String CONTEXT_PATH = "/lean/view";
  private static final Class<?> PKG = RootServlet.class; // Needed by Translator
  private static final long serialVersionUID = 3634806745372015720L;

  public RootServlet() {}

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (!request.getServletPath().startsWith(CONTEXT_PATH)) {
      return;
    }

    response.setContentType("text/html;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);

    PrintWriter out = response.getWriter();

    String html = new String(Files.readAllBytes(new File("src/main/webapp/index.html").toPath()));

    out.println(html);
  }

  public String toString() {
    return "Root IHandler";
  }

  public String getService() {
    return CONTEXT_PATH + " (" + toString() + ")";
  }

  public String getContextPath() {
    return CONTEXT_PATH;
  }
}
