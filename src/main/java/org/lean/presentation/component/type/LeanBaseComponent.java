package org.lean.presentation.component.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanFont;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
import org.lean.core.LeanSize;
import org.lean.core.LeanTextGeometry;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;

public abstract class LeanBaseComponent implements ILeanComponent {

  @HopMetadataProperty @JsonProperty protected String pluginId;
  @HopMetadataProperty @JsonProperty protected String sourceConnectorName;
  @HopMetadataProperty @JsonProperty protected boolean background;
  @HopMetadataProperty @JsonProperty protected boolean border;
  @HopMetadataProperty @JsonProperty protected String themeName;
  @HopMetadataProperty @JsonProperty protected LeanFont defaultFont;
  @HopMetadataProperty @JsonProperty protected LeanColorRGB defaultColor;
  @HopMetadataProperty @JsonProperty protected LeanColorRGB backGroundColor;
  @HopMetadataProperty @JsonProperty protected LeanColorRGB borderColor;

  // Fields below are not serialized
  //
  @JsonIgnore protected transient ILogChannel log;

  public LeanBaseComponent() {}

  public LeanBaseComponent(String pluginId) {
    this.pluginId = pluginId;
  }

  public LeanBaseComponent(String pluginId, LeanBaseComponent c) {
    this(c.pluginId);
    this.sourceConnectorName = c.sourceConnectorName;
    this.defaultFont = c.defaultFont == null ? null : new LeanFont(c.defaultFont);
    this.defaultColor = c.defaultColor == null ? null : new LeanColorRGB(c.defaultColor);
    this.background = c.background;
    this.backGroundColor = c.backGroundColor == null ? null : new LeanColorRGB(c.backGroundColor);
    this.border = c.border;
    this.borderColor = c.borderColor == null ? null : new LeanColorRGB(c.borderColor);
    this.themeName = c.themeName;
  }

  public abstract LeanBaseComponent clone();

  /**
   * @return Null if the dialog class is determined automatically. Otherwise returns the dialog
   *     class name.
   */
  @JsonIgnore
  public String getDialogClassname() {
    return null;
  }

  // First
  public abstract LeanSize getExpectedSize(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException;

  /**
   * Third Calculate expected geometry of the component based on relative positioning and
   * everything.
   *
   * @param presentation
   * @param page
   * @param component
   * @param results
   */
  public LeanGeometry getExpectedGeometry(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {

    // Get the natural imageSize of this component
    //
    LeanSize expectedSize =
        getExpectedSize(presentation, page, component, dataContext, renderContext, results);
    if (expectedSize == null) {
      expectedSize = new LeanSize(0, 0);
    }

    int x = 0;
    int y = 0;
    int width = expectedSize.getWidth();
    int height = expectedSize.getHeight();

    LeanLayout layout = component.getLayout();

    // Validate some basic static information.
    layout.validate(component);

    LeanAttachment left = layout.getLeft();
    LeanAttachment top = layout.getTop();
    LeanAttachment right = layout.getRight();
    LeanAttachment bottom = layout.getBottom();

    // First calculate left and top : (x,y)

    // Left means x coordinate
    //
    if (left != null) {
      LeanGeometry geometry = lookupGeometry(left.getComponentName(), page, results, presentation);

      switch (left.getAlignment()) {
        case DEFAULT:
        case LEFT:
          x =
              geometry.getX()
                  + calcPct(geometry.getWidth(), left.getPercentage())
                  + left.getOffset();
          break;
        case RIGHT:
          x =
              geometry.getX()
                  + geometry.getWidth()
                  - calcPct(geometry.getWidth(), left.getPercentage())
                  + left.getOffset();
          break;
        case CENTER:
          x =
              (geometry.getWidth() - width) / 2
                  + calcPct(geometry.getWidth(), left.getPercentage())
                  + left.getOffset();
          break;
        case TOP:
        case BOTTOM:
          throw new LeanException(
              "Setting a TOP or BOTTOM alignment makes no sense for left attachments on component "
                  + component.getName());
      }
    }

    // top means y coordinate
    //
    if (top != null) {
      LeanGeometry geometry = lookupGeometry(top.getComponentName(), page, results, presentation);
      switch (top.getAlignment()) {
        case DEFAULT:
        case TOP:
          y =
              geometry.getY()
                  + calcPct(geometry.getHeight(), top.getPercentage())
                  + top.getOffset();
          break;
        case BOTTOM:
          y =
              geometry.getY()
                  + geometry.getHeight()
                  - calcPct(geometry.getHeight(), top.getPercentage())
                  + top.getOffset();
          break;
        case CENTER:
          y =
              geometry.getY()
                  + geometry.getHeight() / 2
                  + calcPct(geometry.getHeight(), top.getPercentage())
                  + top.getOffset();
          break;
      }
    }

    // We calculated the coordinates.
    // Now see if we need to adjust the width and height.

    // Right attachment : width

    if (right != null) {
      LeanGeometry geometry = lookupGeometry(right.getComponentName(), page, results, presentation);
      if (left == null) {
        // We're calculating the x-boundary, not the width
        //
        switch (right.getAlignment()) {
          case LEFT:
            x =
                geometry.getX()
                    - width
                    + calcPct(geometry.getHeight(), right.getPercentage())
                    + right.getOffset();
            break;
          case DEFAULT:
          case RIGHT:
            x =
                geometry.getX()
                    + geometry.getWidth()
                    - width // hug the right
                    - calcPct(geometry.getHeight(), right.getPercentage())
                    + right.getOffset();
            break;
          case CENTER:
            x =
                geometry.getX()
                    + geometry.getWidth() / 2
                    - geometry.getWidth()
                    + calcPct(geometry.getHeight(), right.getPercentage())
                    + right.getOffset();
            break;
        }
      } else {
        // We have a left and right boundary, so we can calculate the width
        //
        switch (right.getAlignment()) {
          case LEFT:
            width =
                geometry.getX()
                    - x
                    + calcPct(geometry.getWidth(), right.getPercentage())
                    + right.getOffset();
            break;
          case DEFAULT:
          case RIGHT:
            // We calculate the width, stretch or shrink the component area
            // So we're asked to take the right boundary of the referenced geometry
            // Then we're subtracting the width of the geometry.
            // Which is to say that this is the same as the x location
            //
            width =
                geometry.getX()
                    + geometry.getWidth()
                    - x
                    - calcPct(geometry.getWidth(), right.getPercentage())
                    + right.getOffset();
            break;
          case CENTER:
            width =
                geometry.getX()
                    + geometry.getWidth() / 2
                    - x
                    + calcPct(geometry.getWidth(), right.getPercentage())
                    + right.getOffset();
            break;
        }
      }
    }

    // bottom means height
    //
    if (bottom != null) {
      LeanGeometry geometry =
          lookupGeometry(bottom.getComponentName(), page, results, presentation);

      if (top == null) {
        // We're calculating the y-location, not the height
        //
        switch (bottom.getAlignment()) {
          case TOP:
            y =
                geometry.getY()
                    - height
                    + calcPct(geometry.getHeight(), bottom.getPercentage())
                    + bottom.getOffset();
            break;
          case DEFAULT:
          case BOTTOM:
            y =
                geometry.getY()
                    + geometry.getHeight()
                    - height // hug the bottom
                    + calcPct(geometry.getHeight(), bottom.getPercentage())
                    + bottom.getOffset();
            break;
          case CENTER:
            y =
                geometry.getY()
                    + geometry.getHeight() / 2
                    - geometry.getHeight()
                    + bottom.getOffset();
            break;
        }
      } else {
        // We calculate the width
        //
        switch (bottom.getAlignment()) {
          case TOP:
            height =
                geometry.getY()
                    - y
                    + calcPct(geometry.getWidth(), bottom.getPercentage())
                    + bottom.getOffset();
            break;
          case DEFAULT:
          case BOTTOM:
            height =
                geometry.getY()
                    + geometry.getHeight()
                    - y
                    - calcPct(geometry.getWidth(), bottom.getPercentage())
                    + bottom.getOffset();
            break;
          case CENTER:
            height =
                geometry.getY()
                    + geometry.getHeight() / 2
                    - y
                    - calcPct(geometry.getWidth(), bottom.getPercentage())
                    + bottom.getOffset();
            break;
        }
      }
    }

    // Let's not do negative width/height.
    // It's a misconfiguration
    //
    width = Math.max(0, width);
    height = Math.max(0, height);

    // Now we have the actual position and imageSize of the component
    //
    return new LeanGeometry(x, y, width, height);
  }

  private LeanGeometry lookupGeometry(
      String componentName, LeanPage page, LeanLayoutResults results, LeanPresentation presentation)
      throws LeanException {
    if (StringUtils.isEmpty(componentName)) {
      // Use the geometry of the page...
      //
      int width = page.getWidthBetweenMargins();
      int height = presentation.getUsableHeight(page);
      LeanGeometry geometry = new LeanGeometry(0, 0, width, height);
      return geometry;
    } else {
      LeanGeometry geometry = results.findGeometry(componentName);
      if (geometry == null) {
        throw new LeanException(
            "Unable to find the geometry of component "
                + componentName
                + " on page "
                + page.getPageNumber());
      }
      return geometry;
    }
  }

  private int calcPct(int height, int percentage) {
    return (int) ((double) height * (double) percentage / 100);
  }

  /**
   * Fourth Now we do the layout of the component. In case the component doesn't fit on the page,
   * move it to a next page...
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param renderContext
   * @param results
   */
  public void doLayout(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {

    // Get the current page on which we're rendering...
    // Create a new one if we need to move on to a next page
    //
    LeanRenderPage renderPage = results.getCurrentRenderPage(page);

    // Calculate the expected geometry for this component
    //
    LeanGeometry expectedGeometry =
        getExpectedGeometry(presentation, page, component, dataContext, renderContext, results);

    int bottomOfComponent = expectedGeometry.getY() + expectedGeometry.getHeight();
    int usablePageHeight = presentation.getUsableHeight(page);

    // Check if the component fits on the current page (height only for now).
    //
    if (bottomOfComponent > usablePageHeight) {
      // Component is too large, move it to a new page based upon the same...
      //
      renderPage = results.addNewPage(page, renderPage);

      // OK, now we render on this page...
      // We'll have to re-calculate the y-coordinate of the component to be at the very top of the
      // page...
      //
      expectedGeometry.setY(page.getTopMargin());
    }

    // Now create a layout result to remember during rendering...
    //
    LeanComponentLayoutResult result = new LeanComponentLayoutResult();
    result.setRenderPage(renderPage);
    result.setSourcePage(page);
    result.setComponent(component);
    result.setGeometry(expectedGeometry);
    result.setPartNumber(1); // Only one part ever for a label, perhaps later more

    // Store the geometry also in the results for layout purposes...
    //
    results.addComponentGeometry(component.getName(), expectedGeometry);

    renderPage.getLayoutResults().add(result);
  }

  /** Finally... Render the component using the layout results after having done the layout. */
  public abstract void render(
      LeanComponentLayoutResult layoutResult,
      LeanLayoutResults results,
      IRenderContext renderContext,
      LeanPosition offSet)
      throws LeanException;

  protected Font createFont(LeanFont leanFont) {
    int size = 10;
    int style = Font.PLAIN;

    if (StringUtils.isNotEmpty(leanFont.getFontSize())) {
      size = Const.toInt(leanFont.getFontSize(), 10);
    }
    if (leanFont.isBold()) {
      style |= Font.BOLD;
    }
    if (leanFont.isItalic()) {
      style |= Font.ITALIC;
    }

    return new Font(leanFont.getFontName(), style, size);
  }

  /**
   * If a background color is set, use that. If a theme is set for this component, take the
   * background color from that theme.
   *
   * @param renderContext the render context to look up the color with
   * @return The background color of this component or the one from the defined theme (if any is
   *     set).
   * @throws LeanException in case the requested color isn't defined anywhere
   */
  protected LeanColorRGB lookupBackgroundColor(IRenderContext renderContext) throws LeanException {
    if (backGroundColor != null) {
      return backGroundColor;
    }
    LeanTheme theme = renderContext.lookupTheme(themeName);
    if (theme != null) {
      return theme.lookupBackgroundColor();
    }
    if (defaultColor != null) {
      return defaultColor;
    }
    throw new LeanException("No background color nor default color found (no theme used or found");
  }

  /**
   * If a default color is set, use that. If a theme is set for this component, take the default
   * color from that scheme.
   *
   * @param renderContext the render context to look up the color with
   * @return The default color of this component or the one from the defined theme (if any is set).
   *     It returns null otherwise.
   */
  protected LeanColorRGB lookupDefaultColor(IRenderContext renderContext) throws LeanException {
    if (defaultColor != null) {
      return defaultColor;
    }
    LeanTheme theme = renderContext.lookupTheme(themeName);
    if (theme != null) {
      return theme.getDefaultColor();
    }
    throw new LeanException("There is no default color set (no theme found)");
  }

  /**
   * If a border color is set, use that. If a theme is set for this component, take the border color
   * from there.
   *
   * @param renderContext the render context to look up the color with
   * @return The border color of this component or the one from the defined theme (if any is set)
   * @throws LeanException in case the requested color isn't defined anywhere
   */
  protected LeanColorRGB lookupBorderColor(IRenderContext renderContext) throws LeanException {
    if (borderColor != null) {
      return borderColor;
    }
    LeanTheme theme = renderContext.lookupTheme(themeName);
    if (theme != null) {
      return theme.lookupBorderColor();
    }
    if (defaultColor != null) {
      return defaultColor;
    }
    throw new LeanException("No background color nor default color found (no theme used or found");
  }

  /**
   * Look up the default font from component settings or from the active theme
   *
   * @param renderContext The context to lookup a theme in
   * @return The default font or null if no font is found
   */
  protected LeanFont lookupDefaultFont(IRenderContext renderContext) throws LeanException {
    if (defaultFont != null) {
      return defaultFont;
    }
    LeanTheme theme = renderContext.lookupTheme(themeName);
    if (theme != null) {
      return theme.getDefaultFont();
    }
    throw new LeanException("There is no default font set (no theme found)");
  }

  protected void drawBackGround(
      SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext)
      throws LeanException {
    // Is there a background?
    //
    if (background) {
      LeanColorRGB actualBackgroundColor = lookupBackgroundColor(renderContext);
      if (actualBackgroundColor != null) {
        Color oldColor = gc.getColor();
        Color bg =
            new Color(backGroundColor.getR(), backGroundColor.getG(), backGroundColor.getB());
        gc.setColor(bg);
        gc.fillRect(
            componentGeometry.getX(),
            componentGeometry.getY(),
            componentGeometry.getWidth(),
            componentGeometry.getHeight());
        gc.setBackground(bg);
        gc.setColor(oldColor);
      }
    }
  }

  /**
   * Draw a border around the component in the selected theme
   *
   * @param gc
   * @param componentGeometry
   */
  protected void drawBorder(
      SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext)
      throws LeanException {
    // Should we draw a border?
    //
    if (isBorder()) {
      LeanColorRGB realBorderColor = lookupBorderColor(renderContext);
      if (realBorderColor != null) {
        LeanColorRGB oldColor = enableColor(gc, realBorderColor);
        gc.drawRect(
            componentGeometry.getX(),
            componentGeometry.getY(),
            componentGeometry.getWidth(),
            componentGeometry.getHeight());
        enableColor(gc, oldColor);
      }
    }
  }

  /**
   * Enable to specified color, get the old color back
   *
   * @param gc the graphical context
   * @param leanColor the color to set
   * @return The old color on the gc
   */
  protected LeanColorRGB enableColor(SVGGraphics2D gc, LeanColorRGB leanColor) {
    // The label color...
    //
    Color oldColor = gc.getColor();

    if (leanColor != null) {
      gc.setColor(new Color(leanColor.getR(), leanColor.getG(), leanColor.getB()));
    }

    return new LeanColorRGB(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue());
  }

  /**
   * Enable to specified background color, get the old background color back
   *
   * @param gc the graphical context
   * @param leanColor the background color to set
   * @return The old color on the gc
   */
  protected LeanColorRGB enableBackgroundColor(SVGGraphics2D gc, LeanColorRGB leanColor) {
    // The label color...
    //
    Color oldColor = gc.getBackground();

    if (leanColor != null) {
      gc.setBackground(new Color(leanColor.getR(), leanColor.getG(), leanColor.getB()));
    }

    return new LeanColorRGB(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue());
  }

  /**
   * Set all the default in terms of background, border, color and font
   *
   * @param gc
   * @param componentGeometry
   */
  protected void setBackgroundBorderFont(
      SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext)
      throws LeanException {
    drawBackGround(gc, componentGeometry, renderContext);
    drawBorder(gc, componentGeometry, renderContext);
    enableColor(gc, lookupDefaultColor(renderContext));
    enableFont(gc, lookupDefaultFont(renderContext));
  }

  /**
   * Calculate the correct width and height or a string on a gc. Also return the descent of the
   * string
   *
   * @param gc
   * @param string The string to calculate the LeanTextGeometry for
   * @return The string geometry
   */
  protected LeanTextGeometry calculateTextGeometry(SVGGraphics2D gc, String string) {
    // Calculate the proper imageSize of the string...
    //
    boolean emptyString = StringUtils.isEmpty(string);
    TextLayout textLayout =
        new TextLayout(
            emptyString ? "Apache Hop" : string, gc.getFont(), gc.getFontRenderContext());
    Rectangle2D bounds = textLayout.getBounds();

    // Height is negative, don't like it.
    // I would rather have the label start at upper left, not lower left
    // Descent: The part below the text baseline (lower part of g,p,f,y, ...)
    //
    int descent = (int) textLayout.getDescent();
    int textWidth = (int) textLayout.getVisibleAdvance();
    int textHeight = (int) (bounds.getHeight() + descent);

    return new LeanTextGeometry(
        emptyString ? 0 : textWidth,
        textHeight,
        -(int) bounds.getX(),
        (int) (-bounds.getY() + textLayout.getDescent()));
  }

  /**
   * Enable the specified font on the gc
   *
   * @param gc
   * @param fontChoice The font to set
   */
  protected void enableFont(SVGGraphics2D gc, LeanFont fontChoice) {
    LeanFont font = fontChoice;
    if (font == null) {
      font = defaultFont;
    }

    if (font != null && StringUtils.isNotEmpty(font.getFontName())) {
      gc.setFont(createFont(font));
    }
  }

  /**
   * Gets log
   *
   * @return value of log
   */
  @JsonIgnore
  public ILogChannel getLogChannel() {
    return log;
  }

  /**
   * @param log The log to set
   */
  @JsonIgnore
  public void setLogChannel(ILogChannel log) {
    this.log = log;
  }

  /**
   * Gets pluginId
   *
   * @return value of pluginId
   */
  public String getPluginId() {
    return pluginId;
  }

  /**
   * @param pluginId The pluginId to set
   */
  public void setPluginId(String pluginId) {
    this.pluginId = pluginId;
  }

  /**
   * Gets sourceConnectorName
   *
   * @return value of sourceConnectorName
   */
  public String getSourceConnectorName() {
    return sourceConnectorName;
  }

  /**
   * @param sourceConnectorName The sourceConnectorName to set
   */
  public void setSourceConnectorName(String sourceConnectorName) {
    this.sourceConnectorName = sourceConnectorName;
  }

  /**
   * Gets defaultFont
   *
   * @return value of defaultFont
   */
  public LeanFont getDefaultFont() {
    return defaultFont;
  }

  /**
   * @param defaultFont The defaultFont to set
   */
  public void setDefaultFont(LeanFont defaultFont) {
    this.defaultFont = defaultFont;
  }

  /**
   * Gets defaultColor
   *
   * @return value of defaultColor
   */
  public LeanColorRGB getDefaultColor() {
    return defaultColor;
  }

  /**
   * @param defaultColor The defaultColor to set
   */
  public void setDefaultColor(LeanColorRGB defaultColor) {
    this.defaultColor = defaultColor;
  }

  /**
   * Gets background
   *
   * @return value of background
   */
  public boolean isBackground() {
    return background;
  }

  /**
   * @param background The background to set
   */
  public void setBackground(boolean background) {
    this.background = background;
  }

  /**
   * Gets backGroundColor
   *
   * @return value of backGroundColor
   */
  public LeanColorRGB getBackGroundColor() {
    return backGroundColor;
  }

  /**
   * @param backGroundColor The backGroundColor to set
   */
  public void setBackGroundColor(LeanColorRGB backGroundColor) {
    this.backGroundColor = backGroundColor;
  }

  /**
   * Gets border
   *
   * @return value of border
   */
  public boolean isBorder() {
    return border;
  }

  /**
   * @param border The border to set
   */
  public void setBorder(boolean border) {
    this.border = border;
  }

  /**
   * Gets borderColor
   *
   * @return value of borderColor
   */
  public LeanColorRGB getBorderColor() {
    return borderColor;
  }

  /**
   * @param borderColor The borderColor to set
   */
  public void setBorderColor(LeanColorRGB borderColor) {
    this.borderColor = borderColor;
  }

  /**
   * Gets themeName
   *
   * @return value of themeName
   */
  public String getThemeName() {
    return themeName;
  }

  /**
   * @param themeName The themeName to set
   */
  public void setThemeName(String themeName) {
    this.themeName = themeName;
  }
}
