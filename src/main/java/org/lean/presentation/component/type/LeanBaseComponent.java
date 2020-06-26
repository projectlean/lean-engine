package org.lean.presentation.component.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanFont;
import org.lean.core.LeanGeometry;
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

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public abstract class LeanBaseComponent implements ILeanComponent {

  @HopMetadataProperty
  @JsonProperty
  protected String pluginId;

  @HopMetadataProperty
  @JsonProperty
  protected String sourceConnectorName;

  @HopMetadataProperty
  @JsonProperty
  private LeanFont defaultFont;

  @HopMetadataProperty
  @JsonProperty
  private LeanColorRGB defaultColor;

  @HopMetadataProperty
  @JsonProperty
  protected boolean background;

  @HopMetadataProperty
  @JsonProperty
  private LeanColorRGB backGroundColor;

  @HopMetadataProperty
  @JsonProperty
  protected boolean border;

  @HopMetadataProperty
  @JsonProperty
  private LeanColorRGB borderColor;

  @HopMetadataProperty
  @JsonProperty
  protected String themeName;

  // Fields below are not serialized
  //
  @JsonIgnore
  protected transient ILogChannel log;

  public LeanBaseComponent() {
  }

  public LeanBaseComponent( String pluginId ) {
    this.pluginId = pluginId;
  }

  public LeanBaseComponent( String pluginId, LeanBaseComponent c ) {
    this( c.pluginId );
    this.sourceConnectorName = c.sourceConnectorName;
    this.defaultFont = c.defaultFont == null ? null : new LeanFont( c.defaultFont );
    this.defaultColor = c.defaultColor == null ? null : new LeanColorRGB( c.defaultColor );
    this.background = c.background;
    this.backGroundColor = c.backGroundColor == null ? null : new LeanColorRGB( c.backGroundColor );
    this.border = c.border;
    this.borderColor = c.borderColor == null ? null : new LeanColorRGB( c.borderColor );
  }

  public abstract LeanBaseComponent clone();

  /**
   * @return Null if the dialog class is determined automatically.  Otherwise returns the dialog class name.
   */
  @JsonIgnore
  public String getDialogClassname() {
    return null;
  }

  // First
  public abstract LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException;

  // Second
  public LeanGeometry getNaturalGeometry( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    int x = 0;
    int y = 0;
    int width;
    int height;

    if ( component.getSize() != null && component.getSize().isDefined() ) {
      width = component.getSize().getWidth();
      height = component.getSize().getHeight();
    } else {
      // Calculate the imageSize based on the content
      //
      LeanSize expectedSize = getExpectedSize( presentation, page, component, dataContext, renderContext, results );

      width = expectedSize.getWidth();
      height = expectedSize.getHeight();
    }

    int pageWidth = page.getWidthBeweenMargins();
    int pageHeight = presentation.getUsableHeight( page );

    LeanLayout layout = component.getLayout();
    if ( layout.getLeft() != null ) {
      x = layout.getLeft().getPercentage() * pageWidth / 100 + layout.getLeft().getOffset();
    }
    if ( layout.getRight() != null ) {
      x = pageWidth - layout.getRight().getPercentage() * pageWidth / 100 + layout.getRight().getOffset() - width;
    }
    if ( layout.getTop() != null ) {
      y = layout.getTop().getPercentage() * pageHeight / 100 + layout.getTop().getOffset();
    }
    if ( layout.getBottom() != null ) {
      y = pageHeight - layout.getBottom().getPercentage() * pageHeight / 100 + layout.getBottom().getOffset() - height;
    }

    return new LeanGeometry( x, y, width, height );
  }

  /**
   * Third
   * Calculate expected geometry of the component based on relative positioning and everything.
   *
   * @param presentation
   * @param page
   * @param component
   * @param results
   */
  public LeanGeometry getExpectedGeometry( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {

    // Get the natural imageSize of this component
    //
    LeanGeometry naturalGeometry = getNaturalGeometry( presentation, page, component, dataContext, renderContext, results );

    int x = naturalGeometry.getX();
    int y = naturalGeometry.getY();
    int width = naturalGeometry.getWidth();
    int height = naturalGeometry.getHeight();

    boolean noWidth = width <= 0;
    boolean noHeight = height <= 0;

    LeanLayout layout = component.getLayout();

    // First calculate left and top : (x,y)

    // Left means x coordinate
    //
    if ( layout.getLeft() != null ) {
      LeanAttachment left = layout.getLeft();
      if ( left.getComponentName() == null ) {
        // This means relative to the page
        //
        int pageWidth = page.getWidthBeweenMargins();
        switch ( left.getAlignment() ) {
          case DEFAULT:
          case LEFT:
            x = pageWidth * (int) ( ( (double) left.getPercentage() / 100 ) ) + left.getOffset();
            break;
          case RIGHT:
            x = pageWidth - pageWidth * (int) ( (double) left.getPercentage() / 100 ) - left.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a natural imageSize
            //
            x = ( pageWidth - width ) / 2 + left.getOffset();
            break;
          case TOP:
          case BOTTOM:
            throw new LeanException( "Setting a TOP or BOTTOM alignment makes no sense for left attachments on component " + component.getName() );
        }
      } else {
        // Get the geometry after a layout result of the neighbor
        //
        LeanGeometry neighborGeometry = results.findGeometry( left.getComponentName() );

        // The next should never occur if our cocktail sort is OK and/or if the components exists in the right order on the page.
        //
        if ( neighborGeometry == null ) {
          throw new LeanException(
            "Unable to find component layout result for " + component.getName() + " referencing left neighbor " + left.getComponentName() + " : component layout sort problem?" );
        }

        switch ( left.getAlignment() ) {
          case DEFAULT:
          case LEFT:
            x = neighborGeometry.getX() + left.getOffset();
            break;
          case RIGHT:
            x = neighborGeometry.getX() + neighborGeometry.getWidth() + left.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a imageSize
            // We'll use the specified imageSize of the component
            //
            x = ( neighborGeometry.getWidth() - width ) / 2 + left.getOffset();
            break;
          case TOP:
          case BOTTOM:
            throw new LeanException( "Setting a TOP or BOTTOM alignment makes no sense for left attachments on component " + component.getName() );
        }
      }
    }

    // top means y coordinate
    //
    if ( layout.getTop() != null ) {
      LeanAttachment top = layout.getTop();
      if ( top.getComponentName() == null ) {
        // This means relative to the page
        //
        int pageHeight = presentation.getUsableHeight( page );
        switch ( top.getAlignment() ) {
          case DEFAULT:
          case TOP:
            y = pageHeight * (int) ( (double) top.getPercentage() / 100 ) + top.getOffset();
            break;
          case BOTTOM:
            y = pageHeight - pageHeight * (int) ( (double) top.getPercentage() / 100 ) - top.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a natural imageSize
            //
            y = ( pageHeight - height ) / 2 + top.getOffset();
            break;
          case LEFT:
          case RIGHT:
            throw new LeanException( "Setting a LEFT or RIGHT alignment makes no sense for top attachments on component " + component.getName() );
        }
      } else {
        // Get the geometry after a layout result of the neighbor
        //
        LeanGeometry neighborGeometry = results.findGeometry( top.getComponentName() );

        // The next should never occur if our cocktail sort is OK and/or if the components exists in the right order on the page.
        //
        if ( neighborGeometry == null ) {
          throw new LeanException( "Unable to find component layout result for " + component.getName() + " referencing top neighbor " + top.getComponentName() + " : component layout sort problem?" );
        }

        switch ( top.getAlignment() ) {
          case DEFAULT:
          case TOP:
            y = neighborGeometry.getY() + top.getOffset();
            break;
          case BOTTOM:
            y = neighborGeometry.getY() + neighborGeometry.getHeight() + top.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a imageSize
            // We'll use the specified imageSize of the component
            //
            y = ( neighborGeometry.getHeight() - height ) / 2 + top.getOffset();
            break;
          case LEFT:
          case RIGHT:
            throw new LeanException( "Setting a LEFT or RIGHT alignment makes no sense for top attachments on component " + component.getName() );
        }
      }
    }

    // We calculated the coordinates.
    // Now see if we need to adjust the width and height.

    // Right attachment : width

    if ( layout.getRight() != null ) {
      LeanAttachment right = layout.getRight();
      if ( right.getComponentName() == null ) {
        // This means relative to the page
        //
        int pageWidth = page.getWidthBeweenMargins();
        switch ( right.getAlignment() ) {
          case DEFAULT:
          case LEFT:
            width = -x + pageWidth * (int) ( (double) right.getPercentage() / 100 ) + right.getOffset();
            break;
          case RIGHT:
            width = -x + pageWidth - pageWidth * (int) ( (double) right.getPercentage() / 100 ) - right.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a natural imageSize
            // Don't change the width
            //
            break;
          case TOP:
          case BOTTOM:
            throw new LeanException( "Setting a TOP or BOTTOM alignment makes no sense for right attachments on component " + component.getName() );
        }
      } else {
        // Get the geometry after a layout result of the neighbor
        //
        LeanGeometry neighborGeometry = results.findGeometry( right.getComponentName() );

        // The next should never occur if our cocktail sort is OK and/or if the components exists in the right order on the page.
        //
        if ( neighborGeometry == null ) {
          throw new LeanException(
            "Unable to find component layout result for " + component.getName() + " referencing right neighbor " + right.getComponentName() + " : component layout sort problem?" );
        }

        switch ( right.getAlignment() ) {
          case DEFAULT:
          case LEFT:
            width = neighborGeometry.getX() - x + right.getOffset();
            break;
          case RIGHT:
            // If we have no information about the left hand side of the component
            // we can simply calculate the X position backwards with the natural width.
            //
            if ( layout.getLeft() == null ) {
              x = neighborGeometry.getX() + neighborGeometry.getWidth() - naturalGeometry.getWidth();
            } else {
              // In the other case we need to calculate the width, stretch or shrink the component area
              //
              width = neighborGeometry.getX() + neighborGeometry.getWidth() - x + right.getOffset();
            }
            break;
          case CENTER:
            // Center only makes sense if this component has a imageSize
            // Don't change the width
            //
            break;
          case TOP:
          case BOTTOM:
            throw new LeanException( "Setting a TOP or BOTTOM alignment makes no sense for right attachments on component " + component.getName() );
        }
      }
    }

    // bottom means height
    //
    if ( layout.getBottom() != null ) {
      LeanAttachment bottom = layout.getBottom();
      if ( bottom.getComponentName() == null ) {
        // This means relative to the page
        //
        int pageHeight = presentation.getUsableHeight( page );
        switch ( bottom.getAlignment() ) {
          case DEFAULT:
          case TOP:
            height = -y + (int) ( pageHeight * ( (double) bottom.getPercentage() / 100 ) ) + bottom.getOffset();
            break;
          case BOTTOM:
            height = -y + (int) ( pageHeight - (double) pageHeight * ( bottom.getPercentage() / 100 ) ) - bottom.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a natural imageSize
            // Don't change the height
            break;
          case LEFT:
          case RIGHT:
            throw new LeanException( "Setting a LEFT or RIGHT alignment makes no sense for bottom attachments on component " + component.getName() );
        }
      } else {
        // Get the geometry after a layout result of the neighbor
        //
        LeanGeometry neighborGeometry = results.findGeometry( bottom.getComponentName() );

        // The next should never occur if our cocktail sort is OK and/or if the components exists in the right order on the page.
        //
        if ( neighborGeometry == null ) {
          throw new LeanException(
            "Unable to find component layout result for " + component.getName() + " referencing top neighbor " + bottom.getComponentName() + " : component layout sort problem?" );
        }

        switch ( bottom.getAlignment() ) {
          case DEFAULT:
          case TOP:
            height = neighborGeometry.getY() - y + bottom.getOffset();
            break;
          case BOTTOM:
            // If we don't know the top we don't know the position
            // Then we can calculate backwards with the natural height of the component
            //
            height = neighborGeometry.getY() + neighborGeometry.getHeight() - y + bottom.getOffset();
            break;
          case CENTER:
            // Center only makes sense if this component has a imageSize
            // Don't change the height
            //
            break;
          case LEFT:
          case RIGHT:
            throw new LeanException( "Setting a LEFT or RIGHT alignment makes no sense for bottom attachments on component " + component.getName() );
        }
      }
    }

    // Let's not do negative width/height.
    // It's a misconfiguration
    //
    width = Math.max( 0, width );
    height = Math.max( 0, height );

    // Now we have the actual position and imageSize of the component
    //
    return new LeanGeometry( x, y, width, height );
  }

  /**
   * Fourth
   * Now we do the layout of the component.
   * In case the component doesn't fit on the page, move it to a next page...
   *
   * @param presentation
   * @param page
   * @param component
   * @param dataContext
   * @param renderContext
   * @param results
   */
  public void doLayout( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext,
                        IRenderContext renderContext, LeanLayoutResults results ) throws LeanException {

    // Get the current page on which we're rendering...
    // Create a new one if we need to move on to a next page
    //
    LeanRenderPage renderPage = results.getCurrentRenderPage( page );

    // Calculate the expected geometry for this component
    //
    LeanGeometry expectedGeometry = getExpectedGeometry( presentation, page, component, dataContext, renderContext, results );

    // Check if the component fits on the current page (height only for now.
    //
    if ( expectedGeometry.getY() + expectedGeometry.getHeight() > page.getHeight() - page.getBottomMargin() ) {
      // Label is too large, move it to a new page based upon the same...
      //
      renderPage = results.addNewPage( page, renderPage.getPageNumber() + 1 );

      // OK, now we render on this page...
      // We'll have to re-calculate the y-coordinate of the component to be at the very top of the page...
      //
      expectedGeometry.setY( page.getTopMargin() );
    }

    // Now create a layout result to remember during rendering...
    //
    LeanComponentLayoutResult result = new LeanComponentLayoutResult();
    result.setRenderPage( renderPage );
    result.setSourcePage( page );
    result.setComponent( component );
    result.setGeometry( expectedGeometry );
    result.setPartNumber( 1 ); // Only one part ever for a label, perhaps later more

    // Store the geometry also in the results for layout purposes...
    //
    results.addComponentGeometry( component.getName(), expectedGeometry );

    renderPage.getLayoutResults().add( result );
  }

  /**
   * Finally...
   * Render the component using the layout results after having done the layout.
   */
  abstract public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException;

  protected Font createFont( LeanFont leanFont ) {
    int size = 10;
    int style = Font.PLAIN;

    if ( StringUtils.isNotEmpty( leanFont.getFontSize() ) ) {
      size = Const.toInt( leanFont.getFontSize(), 10 );
    }
    if ( leanFont.isBold() ) {
      style |= Font.BOLD;
    }
    if ( leanFont.isItalic() ) {
      style |= Font.ITALIC;
    }

    return new Font( leanFont.getFontName(), style, size );
  }


  /**
   * If a background color is set, use that.
   * If a theme is set for this component, take the background color from that theme.
   *
   * @param renderContext the render context to look up the color with
   * @return The background color of this component or the one from the defined theme (if any is set).
   * @throws LeanException in case the requested color isn't defined anywhere
   */
  protected LeanColorRGB lookupBackgroundColor( IRenderContext renderContext ) throws LeanException {
    if ( backGroundColor != null ) {
      return backGroundColor;
    }
    LeanTheme theme = renderContext.lookupTheme( themeName );
    if ( theme != null ) {
      return theme.lookupBackgroundColor();
    }
    if ( defaultColor != null ) {
      return defaultColor;
    }
    throw new LeanException( "No background color nor default color found (no theme used or found" );
  }

  /**
   * If a default color is set, use that.
   * If a theme is set for this component, take the default color from that scheme.
   *
   * @param renderContext the render context to look up the color with
   * @return The default color of this component or the one from the defined theme (if any is set).  It returns null otherwise.
   */
  protected LeanColorRGB lookupDefaultColor( IRenderContext renderContext ) throws LeanException {
    if ( defaultColor != null ) {
      return defaultColor;
    }
    LeanTheme theme = renderContext.lookupTheme( themeName );
    if ( theme != null ) {
      return theme.getDefaultColor();
    }
    throw new LeanException( "There is no default color set (no theme found or found)" );

  }

  /**
   * If a border color is set, use that.
   * If a theme is set for this component, take the border color from there.
   *
   * @param renderContext the render context to look up the color with
   * @return The border color of this component or the one from the defined theme (if any is set)
   * @throws LeanException in case the requested color isn't defined anywhere
   */
  protected LeanColorRGB lookupBorderColor( IRenderContext renderContext ) throws LeanException {
    if ( borderColor != null ) {
      return borderColor;
    }
    LeanTheme theme = renderContext.lookupTheme( themeName );
    if ( theme != null ) {
      return theme.lookupBorderColor();
    }
    if ( defaultColor != null ) {
      return defaultColor;
    }
    throw new LeanException( "No background color nor default color found (no theme used or found" );
  }

  /**
   * Look up the default font from component settings or from the active theme
   *
   * @param renderContext The context to lookup a theme in
   * @return The default font or null if no font is found
   */
  protected LeanFont lookupDefaultFont( IRenderContext renderContext ) throws LeanException {
    if ( defaultFont != null ) {
      return defaultFont;
    }
    LeanTheme theme = renderContext.lookupTheme( themeName );
    if ( theme != null ) {
      return theme.getDefaultFont();
    }
    throw new LeanException( "There is no default font set (no theme found or found)" );
  }


  protected void drawBackGround( SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext ) throws LeanException {
    // Is there a background?
    //
    if ( background ) {

      LeanColorRGB actualBackgroundColor = lookupBackgroundColor( renderContext );
      if ( actualBackgroundColor != null ) {
        Color oldColor = gc.getColor();
        Color bg = new Color( backGroundColor.getR(), backGroundColor.getG(), backGroundColor.getB() );
        gc.setColor( bg );
        gc.fillRect( componentGeometry.getX(), componentGeometry.getY(), componentGeometry.getWidth(), componentGeometry.getHeight() );
        gc.setBackground( bg );
        gc.setColor( oldColor );
      }
    }
  }

  /**
   * Draw a border around the component in the selected theme
   *
   * @param gc
   * @param componentGeometry
   */
  protected void drawBorder( SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext ) throws LeanException {
    // Should we draw a border?
    //
    if ( isBorder() ) {
      LeanColorRGB realBorderColor = lookupBorderColor( renderContext );
      if ( realBorderColor != null ) {
        LeanColorRGB oldColor = enableColor( gc, realBorderColor );
        gc.drawRect( componentGeometry.getX(), componentGeometry.getY(), componentGeometry.getWidth(), componentGeometry.getHeight() );
        enableColor( gc, oldColor );
      }
    }
  }

  /**
   * Enable to specified color, get the old color back
   *
   * @param gc        the graphical context
   * @param leanColor the color to set
   * @return The old color on the gc
   */
  protected LeanColorRGB enableColor( SVGGraphics2D gc, LeanColorRGB leanColor ) {
    // The label color...
    //
    Color oldColor = gc.getColor();

    if ( leanColor != null ) {
      gc.setColor( new Color( leanColor.getR(), leanColor.getG(), leanColor.getB() ) );
    }

    return new LeanColorRGB( oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue() );
  }


  /**
   * Set all the default in terms of background, border, color and font
   *
   * @param gc
   * @param componentGeometry
   */
  protected void setBackgroundBorderFont( SVGGraphics2D gc, LeanGeometry componentGeometry, IRenderContext renderContext ) throws LeanException {
    drawBackGround( gc, componentGeometry, renderContext );
    drawBorder( gc, componentGeometry, renderContext );
    enableColor( gc, lookupDefaultColor( renderContext ) );
    enableFont( gc, lookupDefaultFont( renderContext ) );
  }

  /**
   * Calculate the correct width and height or a string on a gc.
   * Also return the descent of the string
   *
   * @param gc
   * @param string The string to calculate the LeanTextGeometry for
   * @return The string geometry
   */
  protected LeanTextGeometry calculateTextGeometry( SVGGraphics2D gc, String string ) {
    // Calculate the proper imageSize of the string...
    //
    TextLayout textLayout = new TextLayout( string, gc.getFont(), gc.getFontRenderContext() );
    Rectangle2D bounds = textLayout.getBounds();

    // Height is negative, don't like it.
    // I would rather have the label start at upper left, not lower left
    // Descent: The part below the text baseline (lower part of g,p,f,y, ...)
    //
    int descent = (int) textLayout.getDescent();
    int textWidth = (int) textLayout.getVisibleAdvance();
    int textHeight = (int) ( bounds.getHeight() + descent );

    return new LeanTextGeometry( textWidth, textHeight, -(int) bounds.getX(), (int) ( -bounds.getY() + textLayout.getDescent() ) );
  }

  /**
   * Enable the specified font on the gc
   *
   * @param gc
   * @param fontChoice The font to set
   */
  protected void enableFont( SVGGraphics2D gc, LeanFont fontChoice ) {
    LeanFont font = fontChoice;
    if ( font == null ) {
      font = defaultFont;
    }

    if ( font != null && StringUtils.isNotEmpty( font.getFontName() ) ) {
      gc.setFont( createFont( font ) );
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
  public void setLogChannel( ILogChannel log ) {
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
  public void setPluginId( String pluginId ) {
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
  public void setSourceConnectorName( String sourceConnectorName ) {
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
  public void setDefaultFont( LeanFont defaultFont ) {
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
  public void setDefaultColor( LeanColorRGB defaultColor ) {
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
  public void setBackground( boolean background ) {
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
  public void setBackGroundColor( LeanColorRGB backGroundColor ) {
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
  public void setBorder( boolean border ) {
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
  public void setBorderColor( LeanColorRGB borderColor ) {
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
  public void setThemeName( String themeName ) {
    this.themeName = themeName;
  }
}
