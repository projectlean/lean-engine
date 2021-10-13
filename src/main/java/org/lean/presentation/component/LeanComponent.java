package org.lean.presentation.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.metadata.api.HopMetadataBase;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.RenderPageDataContext;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;
import org.lean.render.context.SimpleRenderContext;

import java.util.Collections;
import java.util.List;

/**
 * Main component class encapsulating component plugins through ILeanComponent
 *
 * @author matt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeanComponent extends HopMetadataBase implements IHopMetadata {

  @HopMetadataProperty private LeanLayout layout;
  @HopMetadataProperty private ILeanComponent component;
  @HopMetadataProperty private boolean shared;
  @HopMetadataProperty private String rotation;
  @HopMetadataProperty private String transparency;
  @HopMetadataProperty private LeanSize clipSize;

  public LeanComponent() {}

  public LeanComponent(String name, ILeanComponent component) {
    this();
    this.name = name;
    this.component = component;
  }

  public LeanComponent(LeanComponent c) {
    this();
    this.name = c.name;
    if (c.component != null) {
      this.component = c.component.clone();
      this.component.setThemeName(c.component.getThemeName());
    }
    this.layout = c.layout == null ? null : new LeanLayout(c.layout);
    this.clipSize = c.clipSize==null ? null : new LeanSize(c.clipSize);
  }

  @Override
  public String toString() {
    return "LeanComponent(" + name + ":" + component.getPluginId() + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LeanComponent)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return ((LeanComponent) obj).name.equals(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Process data in the component. Then perform the layout of the component, modify the layout
   * results.
   *
   * @param log The logging channel to log to
   * @param leanPresentation the presentation
   * @param page the page
   * @param dataContext The data context to use
   */
  public void processAndLayout(
      ILogChannel log,
      LeanPresentation leanPresentation,
      LeanPage page,
      RenderPageDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults footerResults)
      throws LeanException {
    component.setLogChannel(log);
    component.processSourceData(
        leanPresentation, page, this, dataContext, renderContext, footerResults);
    component.doLayout(leanPresentation, page, this, dataContext, renderContext, footerResults);
  }

  /**
   * This throws this component on a presentation with one page with the given size, renders it
   *
   * @param width
   * @param height
   * @param connectors The connectors to use to make this component work
   * @param themes The themes to reference
   * @return
   */
  public String getSvgXml(
      int width,
      int height,
      List<LeanConnector> connectors,
      List<LeanTheme> themes,
      IHopMetadataProvider metadataProvider)
      throws LeanException {

    LeanPresentation presentation = new LeanPresentation();
    presentation.setName(name);
    LeanPage page = new LeanPage(0, width, height, 0, 0, 0, 0);
    presentation.getPages().add(page);
    presentation.getConnectors().addAll(connectors);

    // Make a copy
    // Position on the top left
    //
    LeanComponent c = new LeanComponent(this);
    c.setLayout(LeanLayout.topLeftPage());

    page.getComponents().add(c);

    IRenderContext renderContext = new SimpleRenderContext(width, height, themes);
    LoggingObject loggingObject = new LoggingObject("componentRender");
    ILogChannel log = LogChannel.GENERAL;

    // We don't pass in any new parameters
    //
    LeanLayoutResults results =
        presentation.doLayout(
            loggingObject, renderContext, metadataProvider, Collections.emptyList());
    presentation.render(results, metadataProvider);

    if (results.getRenderPages().size() == 0) {
      throw new LeanException("No output pages generated");
    }
    LeanRenderPage renderPage = results.getRenderPages().get(0);

    return renderPage.getSvgXml();
  }

  /** @return the component */
  public ILeanComponent getComponent() {
    return component;
  }

  /** @param component the component to set */
  public void setComponent(ILeanComponent component) {
    this.component = component;
  }

  public boolean isShared() {
    return shared;
  }

  public void setShared(boolean shared) {
    this.shared = shared;
  }

  /**
   * Gets layout
   *
   * @return value of layout
   */
  public LeanLayout getLayout() {
    return layout;
  }

  /** @param layout The layout to set */
  public void setLayout(LeanLayout layout) {
    this.layout = layout;
  }

  /**
   * Gets rotation
   *
   * @return value of rotation
   */
  public String getRotation() {
    return rotation;
  }

  /** @param rotation The rotation to set */
  public void setRotation(String rotation) {
    this.rotation = rotation;
  }

  /**
   * Gets transparency
   *
   * @return value of transparency
   */
  public String getTransparency() {
    return transparency;
  }

  /** @param transparency The transparency to set */
  public void setTransparency(String transparency) {
    this.transparency = transparency;
  }

  /**
   * Gets clipSize
   *
   * @return value of clipSize
   */
  public LeanSize getClipSize() {
    return clipSize;
  }

  /**
   * @param clipSize The clipSize to set
   */
  public void setClipSize( LeanSize clipSize ) {
    this.clipSize = clipSize;
  }
}
