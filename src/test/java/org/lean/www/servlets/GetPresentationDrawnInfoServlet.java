/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lean.www.servlets;

import org.apache.hop.core.Const;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.lean.core.draw.DrawnItem;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.interaction.LeanInteraction;
import org.lean.presentation.interaction.LeanInteractionAction;
import org.lean.presentation.interaction.LeanInteractionMethod;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.util.ComboPresentationUtil;
import org.lean.www.DrawInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetPresentationDrawnInfoServlet extends HttpServlet {

  public static final String CONTEXT_PATH = "/lean/presentationDrawnInfo";

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!request.getServletPath().startsWith(CONTEXT_PATH)) {
      return;
    }

    int x = (int) Math.round(Const.toDouble(request.getParameter("x"), -1.0));
    int y = (int) Math.round(Const.toDouble(request.getParameter("y"), -1.0));

    ByteArrayOutputStream svgStream = null;
    try {
      response.setStatus(HttpServletResponse.SC_OK);

      response.setCharacterEncoding("UTF-8");
      response.setContentType(MediaType.APPLICATION_JSON);

      IHopMetadataProvider metadataProvider = new MemoryMetadataProvider();
      ILoggingObject parent = new LoggingObject("GetPresentationSvgServlet");

      // Generate the presentation in SVG
      //
      LeanPresentation presentation =
          new ComboPresentationUtil(
                  new MemoryMetadataProvider(), Variables.getADefaultVariableSpace())
              .createComboPresentation(3000);
      LeanLayoutResults results =
          PresentationCache.renderAndCache(presentation, parent, metadataProvider);

      LeanRenderPage leanRenderPage = results.getRenderPages().get(0); // page 0 to test

      DrawnItem drawnItem = leanRenderPage.lookupDrawnItem(x, y, true);
      if (drawnItem == null) {
        drawnItem = leanRenderPage.lookupDrawnItem(x, y, false);
      }

      DrawInfo drawInfo = new DrawInfo();

      if (drawnItem != null) {
        drawInfo.setDrawnItem(drawnItem);

        // TODO: receive method from JS code
        //
        LeanInteractionMethod method = new LeanInteractionMethod(true, false);
        LeanInteraction interaction = presentation.findInteraction(method, drawnItem);
        if (interaction != null) {
          List<LeanInteractionAction> actions = interaction.getActions();
          drawInfo.setActions(actions);
        }
      }

      String json = drawInfo.toJsonString();
      svgStream = new ByteArrayOutputStream();
      try {
        svgStream.write(json.getBytes(StandardCharsets.UTF_8));
      } finally {
        svgStream.flush();
      }
      response.setContentLength(svgStream.size());

      OutputStream out = response.getOutputStream();
      out.write(svgStream.toByteArray());

    } catch (Exception e) {
      throw new IOException("Error getting presentation drawn item at (" + x + ", " + y + ")", e);
    } finally {
      if (svgStream != null) {
        svgStream.close();
      }
    }
  }

  public String toString() {
    return "Pipeline Image IHandler";
  }

  public String getService() {
    return CONTEXT_PATH + " (" + toString() + ")";
  }

  public String getContextPath() {
    return CONTEXT_PATH;
  }
}
