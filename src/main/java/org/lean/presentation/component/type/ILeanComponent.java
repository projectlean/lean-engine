package org.lean.presentation.component.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.metadata.api.HopMetadataObject;
import org.apache.hop.metadata.api.IHopMetadataObjectFactory;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanFont;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

/**
 * This interface identifies component type plugin classes. These contain the specific attributes of
 * a component.
 *
 * @author matt
 */
@JsonDeserialize(using = ILeanComponentDeserializer.class)
@HopMetadataObject(objectFactory = ILeanComponent.LeanComponentObjectFactory.class)
public interface ILeanComponent extends Cloneable {

  /**
   * If a component needs data from a connector, this is where that happens. You can obviously read
   * elsewhere and stub this method if you want to draw on the fly but otherwise, do it here.
   */
  void processSourceData(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;

  /**
   * First thing a component does: determine its expected size. Calculate the expected size: either
   * the size specified on the component OR the calculated size if not specified and as such dynamic
   */
  LeanSize getExpectedSize(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;

  /**
   * Next we calculate the expected geometry of a component based on the specified attachments to
   * other components, relative positions and so on.
   */
  LeanGeometry getExpectedGeometry(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;

  /**
   * Perform the layout of this component on the given page of a presentation, modify the results
   * list.
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param renderContext
   * @param results
   * @throws LeanException
   */
  void doLayout(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;

  /** Render the component using the layout results after having done the layout. */
  void render(
      LeanComponentLayoutResult layoutResult,
      LeanLayoutResults results,
      IRenderContext renderContext,
      LeanPosition offSet)
      throws LeanException;

  @JsonIgnore
  ILogChannel getLogChannel();

  @JsonIgnore
  void setLogChannel(ILogChannel log);

  /**
   * @return a copy of this components metadata
   */
  ILeanComponent clone();

  /**
   * @return Null if the dialog class is determined automatically. Otherwise returns the dialog
   *     class name.
   */
  String getDialogClassname();

  /**
   * Gets pluginId
   *
   * @return value of pluginId
   */
  String getPluginId();

  /**
   * @param pluginId The pluginId to set
   */
  void setPluginId(String pluginId);

  /**
   * Gets sourceConnectorName
   *
   * @return value of sourceConnectorName
   */
  String getSourceConnectorName();

  /**
   * @param sourceConnectorName The sourceConnectorName to set
   */
  void setSourceConnectorName(String sourceConnectorName);

  /**
   * Gets defaultFont
   *
   * @return value of defaultFont
   */
  LeanFont getDefaultFont();

  /**
   * @param defaultFont The defaultFont to set
   */
  void setDefaultFont(LeanFont defaultFont);

  /**
   * Gets defaultColor
   *
   * @return value of defaultColor
   */
  LeanColorRGB getDefaultColor();

  /**
   * @param defaultColor The defaultColor to set
   */
  void setDefaultColor(LeanColorRGB defaultColor);

  /**
   * Gets background
   *
   * @return value of background
   */
  boolean isBackground();

  /**
   * @param background The background to set
   */
  void setBackground(boolean background);

  /**
   * Gets backGroundColor
   *
   * @return value of backGroundColor
   */
  LeanColorRGB getBackGroundColor();

  /**
   * @param backGroundColor The backGroundColor to set
   */
  void setBackGroundColor(LeanColorRGB backGroundColor);

  /**
   * Gets border
   *
   * @return value of border
   */
  boolean isBorder();

  /**
   * @param border The border to set
   */
  void setBorder(boolean border);

  /**
   * Gets borderColor
   *
   * @return value of borderColor
   */
  LeanColorRGB getBorderColor();

  /**
   * @param borderColor The borderColor to set
   */
  void setBorderColor(LeanColorRGB borderColor);

  /**
   * @return The theme to use to render this component
   */
  String getThemeName();

  /**
   * @param themeName The themeName to set
   */
  void setThemeName(String themeName);

  final class LeanComponentObjectFactory implements IHopMetadataObjectFactory {
    @Override
    public Object createObject(String id, Object parentObject) throws HopException {
      PluginRegistry registry = PluginRegistry.getInstance();
      IPlugin plugin = registry.getPlugin(LeanComponentPluginType.class, id);
      return registry.loadClass(plugin);
    }

    @Override
    public String getObjectId(Object object) throws HopException {
      return ((ILeanComponent) object).getPluginId();
    }
  }
}
