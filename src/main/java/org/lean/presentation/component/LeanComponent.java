package org.lean.presentation.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.metadata.api.HopMetadataBase;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.listeners.IDoLayoutListener;
import org.lean.presentation.component.listeners.IProcessSourceDataListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  @JsonIgnore private List<IProcessSourceDataListener> processSourceDataListeners;
  @JsonIgnore private List<IDoLayoutListener> doLayoutListeners;

  public LeanComponent() {
    this.processSourceDataListeners = new ArrayList<>();
    this.doLayoutListeners = new ArrayList<>();
  }

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
    this.clipSize = c.clipSize == null ? null : new LeanSize(c.clipSize);
    this.processSourceDataListeners.addAll(c.processSourceDataListeners);
    this.doLayoutListeners.addAll(c.doLayoutListeners);
  }

  @Override
  public String toString() {
    return "LeanComponent("
        + name
        + ":"
        + (component == null ? "-" : component.getPluginId())
        + ")";
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

    // Call the process source data listeners...
    //
    for (IProcessSourceDataListener listener : processSourceDataListeners) {
      listener.beforeProcessSourceDataCalled(
          leanPresentation, page, this, dataContext, renderContext, footerResults);
    }

    component.processSourceData(
        leanPresentation, page, this, dataContext, renderContext, footerResults);

    // Call the do layout listeners
    //
    component.doLayout(leanPresentation, page, this, dataContext, renderContext, footerResults);
  }

  /**
   * Build a complete set of all the components this component depends upon for doing layout
   *
   * @param components
   * @return
   */
  public Set<LeanComponent> getDependentComponents(Map<String, LeanComponent> components)
      throws LeanException {
    Set<LeanComponent> set = new HashSet<>();

    for (String referencedComponentName : layout.getReferencedLayoutComponentNames()) {
      LeanComponent referencedComponent = components.get(referencedComponentName);
      if (referencedComponent == null) {
        throw new LeanException(
            "Component "
                + getName()
                + " references "
                + referencedComponentName
                + " which isn't known");
      }
      // Now see if this component is in the list yet...
      //
      if (!set.contains(referencedComponent)) {
        // Do a recursive search and all the referenced components as well...
        //
        set.add(referencedComponent);
        set.addAll(referencedComponent.getDependentComponents(components));
      }
    }

    return set;
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

  /** @param clipSize The clipSize to set */
  public void setClipSize(LeanSize clipSize) {
    this.clipSize = clipSize;
  }

  /**
   * Gets processSourceDataListeners
   *
   * @return value of processSourceDataListeners
   */
  public List<IProcessSourceDataListener> getProcessSourceDataListeners() {
    return processSourceDataListeners;
  }

  /** @param processSourceDataListeners The processSourceDataListeners to set */
  public void setProcessSourceDataListeners(
      List<IProcessSourceDataListener> processSourceDataListeners) {
    this.processSourceDataListeners = processSourceDataListeners;
  }

  /**
   * Gets doLayoutListeners
   *
   * @return value of doLayoutListeners
   */
  public List<IDoLayoutListener> getDoLayoutListeners() {
    return doLayoutListeners;
  }

  /** @param doLayoutListeners The doLayoutListeners to set */
  public void setDoLayoutListeners(List<IDoLayoutListener> doLayoutListeners) {
    this.doLayoutListeners = doLayoutListeners;
  }
}
