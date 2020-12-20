package org.lean.presentation.theme;

import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.HopMetadataBase;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;
import org.lean.core.Constants;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanFont;
import org.lean.core.exception.LeanException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@HopMetadata(
  key = "theme",
  name = "Lean Theme",
  description = "A theme with colors and fonts to use as default in the components"
)
public class LeanTheme extends HopMetadataBase implements IHopMetadata {

  @HopMetadataProperty
  protected String description;

  @HopMetadataProperty
  protected List<LeanColorRGB> colors;

  @HopMetadataProperty
  protected LeanColorRGB backgroundColor;

  @HopMetadataProperty
  protected LeanColorRGB defaultColor;

  @HopMetadataProperty
  protected LeanFont defaultFont;

  @HopMetadataProperty
  protected LeanColorRGB borderColor;

  @HopMetadataProperty
  protected LeanFont horizontalDimensionsFont;

  @HopMetadataProperty
  protected LeanColorRGB horizontalDimensionsColor;

  @HopMetadataProperty
  protected LeanFont verticalDimensionsFont;

  @HopMetadataProperty
  protected LeanColorRGB verticalDimensionsColor;

  @HopMetadataProperty
  protected LeanFont factsFont;

  @HopMetadataProperty
  protected LeanColorRGB factsColor;

  @HopMetadataProperty
  protected LeanFont titleFont;

  @HopMetadataProperty
  protected LeanColorRGB titleColor;

  @HopMetadataProperty
  protected LeanColorRGB axisColor;

  @HopMetadataProperty
  protected LeanColorRGB gridColor;

  @HopMetadataProperty
  private boolean shared;

  public LeanTheme() {
    colors = new ArrayList<>();
  }

  public LeanTheme( String name, String description, List<LeanColorRGB> colors ) {
    this.name = name;
    this.description = description;
    this.colors = colors;
    this.backgroundColor = null;
    this.defaultColor = null;
    this.defaultFont = null;
    this.borderColor = null;
  }

  public LeanTheme( LeanTheme s ) {
    this();
    this.name = s.name;
    this.description = s.description;
    for ( LeanColorRGB color : s.getColors() ) {
      colors.add( new LeanColorRGB( color ) );
    }
    this.backgroundColor = s.backgroundColor == null ? null : new LeanColorRGB( s.backgroundColor );
    this.defaultColor = s.defaultColor == null ? null : new LeanColorRGB( s.defaultColor );
    this.defaultFont = s.defaultFont == null ? null : new LeanFont( s.defaultFont );
    this.borderColor = s.borderColor == null ? null : new LeanColorRGB( s.borderColor );
    this.horizontalDimensionsFont = s.horizontalDimensionsFont == null ? null : new LeanFont( s.horizontalDimensionsFont );
    this.horizontalDimensionsColor = s.horizontalDimensionsColor == null ? null : new LeanColorRGB( s.horizontalDimensionsColor );
    this.verticalDimensionsFont = s.verticalDimensionsFont == null ? null : new LeanFont( s.verticalDimensionsFont );
    this.verticalDimensionsColor = s.verticalDimensionsColor == null ? null : new LeanColorRGB( s.verticalDimensionsColor );
    this.factsFont = s.factsFont == null ? null : new LeanFont( s.factsFont );
    this.factsColor = s.factsColor == null ? null : new LeanColorRGB( s.factsColor );
    this.titleFont = s.titleFont == null ? null : new LeanFont( s.titleFont );
    this.titleColor = s.titleColor == null ? null : new LeanColorRGB( s.titleColor );
    this.axisColor = s.axisColor == null ? null : new LeanColorRGB( s.axisColor );
    this.gridColor = s.gridColor == null ? null : new LeanColorRGB( s.gridColor );
  }


  public static final LeanTheme getDefault() {
    LeanTheme theme = new LeanTheme();

    theme.setName( Constants.DEFAULT_THEME_NAME );
    theme.setDescription( Constants.DEFAULT_THEME_DESCRIPTION );

    theme.getColors().clear();
    theme.getColors().addAll( Arrays.asList(
      new LeanColorRGB( "#003f5c" ),
      new LeanColorRGB( "#2f4b7c" ),
      new LeanColorRGB( "#665191" ),
      new LeanColorRGB( "#a05195" ),
      new LeanColorRGB( "#d45087" ),
      new LeanColorRGB( "#f95d6a" ),
      new LeanColorRGB( "#ff7c43" ),
      new LeanColorRGB( "#ffa600" )
    ) );

    theme.setBackgroundColor( new LeanColorRGB( "#ffffff" ) ); // Simply white
    theme.setDefaultColor( new LeanColorRGB( "#000000" ) ); // Simply black
    theme.setDefaultFont( new LeanFont( "Arial", "12", false, false ) );
    theme.setBorderColor( new LeanColorRGB( "#f0f0f0" ) ); // very light gray

    theme.setHorizontalDimensionsFont( new LeanFont( "Arial", "12", true, false ) );
    theme.setHorizontalDimensionsColor( new LeanColorRGB( "#000000" ) );
    theme.setVerticalDimensionsFont( new LeanFont( "Arial", "12", true, false ) );
    theme.setVerticalDimensionsColor( new LeanColorRGB( "#000000" ) );
    theme.setFactsFont( new LeanFont( "Hack", "12", false, false ) );
    theme.setFactsColor( new LeanColorRGB( "#000000" ) );
    theme.setTitleFont( new LeanFont( "Arial", "10", true, true ) );
    theme.setTitleColor( new LeanColorRGB( "#c8c8c8" ) );
    theme.setAxisColor( new LeanColorRGB( "#000000" ) );
    theme.setGridColor( new LeanColorRGB( "#c8c8c8" ) );


    return theme;
  }

  public LeanColorRGB lookupDefaultColor() throws LeanException {
    if ( defaultColor == null ) {
      throw new LeanException( "No default color defined in theme '" + name + "'" );
    }
    return defaultColor;
  }

  public LeanFont lookupDefaultFont() throws LeanException {
    if ( defaultFont == null ) {
      throw new LeanException( "No default font defined in theme '" + name + "'" );
    }
    return defaultFont;
  }

  public LeanColorRGB lookupBackgroundColor() throws LeanException {
    if ( backgroundColor == null && defaultColor == null ) {
      throw new LeanException( "No background color nor default color defined in theme '" + name + "'" );
    }
    if ( backgroundColor != null ) {
      return backgroundColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupBorderColor() throws LeanException {
    if ( borderColor == null && defaultColor == null ) {
      throw new LeanException( "No border color nor default color defined in theme '" + name + "'" );
    }
    if ( borderColor != null ) {
      return borderColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupHorizontalDimensionsColor() throws LeanException {
    if ( horizontalDimensionsColor == null && defaultColor == null ) {
      throw new LeanException( "No horizontal dimensions color nor default color defined in theme '" + name + "'" );
    }
    if ( horizontalDimensionsColor != null ) {
      return horizontalDimensionsColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupVerticalDimensionsColor() throws LeanException {
    if ( verticalDimensionsColor == null && defaultColor == null ) {
      throw new LeanException( "No vertical dimensions color nor default color defined in theme '" + name + "'" );
    }
    if ( verticalDimensionsColor != null ) {
      return verticalDimensionsColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupFactsColor() throws LeanException {
    if ( factsColor == null && defaultColor == null ) {
      throw new LeanException( "No facts color nor default color defined in theme '" + name + "'" );
    }
    if ( factsColor != null ) {
      return factsColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupTitleColor() throws LeanException {
    if ( titleColor == null && defaultColor == null ) {
      throw new LeanException( "No title color nor default color defined in theme '" + name + "'" );
    }
    if ( titleColor != null ) {
      return titleColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupAxisColor() throws LeanException {
    if ( axisColor == null && defaultColor == null ) {
      throw new LeanException( "No axis color nor default color defined in theme '" + name + "'" );
    }
    if ( axisColor != null ) {
      return axisColor;
    }
    return defaultColor;
  }

  public LeanColorRGB lookupGridColor() throws LeanException {
    if ( gridColor == null && defaultColor == null ) {
      throw new LeanException( "No grid color nor default color defined in theme '" + name + "'" );
    }
    if ( gridColor != null ) {
      return gridColor;
    }
    return defaultColor;
  }


  public LeanFont lookupHorizontalDimensionsFont() throws LeanException {
    if ( horizontalDimensionsFont == null && defaultFont == null ) {
      throw new LeanException( "No horizontal dimensions font nor default font defined in theme '" + name + "'" );
    }
    if ( horizontalDimensionsFont != null ) {
      return horizontalDimensionsFont;
    }
    return defaultFont;
  }


  public LeanFont lookupVerticalDimensionsFont() throws LeanException {
    if ( verticalDimensionsFont == null && defaultFont == null ) {
      throw new LeanException( "No vertical dimensions font nor default font defined in theme '" + name + "'" );
    }
    if ( verticalDimensionsFont != null ) {
      return verticalDimensionsFont;
    }
    return defaultFont;
  }

  public LeanFont lookupFactsFont() throws LeanException {
    if ( factsFont == null && defaultFont == null ) {
      throw new LeanException( "No facts font nor default font defined in theme '" + name + "'" );
    }
    if ( factsFont != null ) {
      return factsFont;
    }
    return defaultFont;
  }

  public LeanFont lookupTitleFont() throws LeanException {
    if ( titleFont == null && defaultFont == null ) {
      throw new LeanException( "No title font nor default font defined in theme '" + name + "'" );
    }
    if ( titleFont != null ) {
      return titleFont;
    }
    return defaultFont;
  }

  /**
   * Gets description
   *
   * @return value of description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * Gets colors
   *
   * @return value of colors
   */
  public List<LeanColorRGB> getColors() {
    return colors;
  }

  /**
   * @param colors The colors to set
   */
  public void setColors( List<LeanColorRGB> colors ) {
    this.colors = colors;
  }

  /**
   * Gets backgroundColor
   *
   * @return value of backgroundColor
   */
  public LeanColorRGB getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * @param backgroundColor The backgroundColor to set
   */
  public void setBackgroundColor( LeanColorRGB backgroundColor ) {
    this.backgroundColor = backgroundColor;
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
   * Gets shared
   *
   * @return value of shared
   */
  public boolean isShared() {
    return shared;
  }

  /**
   * @param shared The shared to set
   */
  public void setShared( boolean shared ) {
    this.shared = shared;
  }

  /**
   * Gets horizontalDimensionsFont
   *
   * @return value of horizontalDimensionsFont
   */
  public LeanFont getHorizontalDimensionsFont() {
    return horizontalDimensionsFont;
  }

  /**
   * @param horizontalDimensionsFont The horizontalDimensionsFont to set
   */
  public void setHorizontalDimensionsFont( LeanFont horizontalDimensionsFont ) {
    this.horizontalDimensionsFont = horizontalDimensionsFont;
  }

  /**
   * Gets horizontalDimensionsColor
   *
   * @return value of horizontalDimensionsColor
   */
  public LeanColorRGB getHorizontalDimensionsColor() {
    return horizontalDimensionsColor;
  }

  /**
   * @param horizontalDimensionsColor The horizontalDimensionsColor to set
   */
  public void setHorizontalDimensionsColor( LeanColorRGB horizontalDimensionsColor ) {
    this.horizontalDimensionsColor = horizontalDimensionsColor;
  }

  /**
   * Gets verticalDimensionsFont
   *
   * @return value of verticalDimensionsFont
   */
  public LeanFont getVerticalDimensionsFont() {
    return verticalDimensionsFont;
  }

  /**
   * @param verticalDimensionsFont The verticalDimensionsFont to set
   */
  public void setVerticalDimensionsFont( LeanFont verticalDimensionsFont ) {
    this.verticalDimensionsFont = verticalDimensionsFont;
  }

  /**
   * Gets verticalDimensionsColor
   *
   * @return value of verticalDimensionsColor
   */
  public LeanColorRGB getVerticalDimensionsColor() {
    return verticalDimensionsColor;
  }

  /**
   * @param verticalDimensionsColor The verticalDimensionsColor to set
   */
  public void setVerticalDimensionsColor( LeanColorRGB verticalDimensionsColor ) {
    this.verticalDimensionsColor = verticalDimensionsColor;
  }

  /**
   * Gets factsFont
   *
   * @return value of factsFont
   */
  public LeanFont getFactsFont() {
    return factsFont;
  }

  /**
   * @param factsFont The factsFont to set
   */
  public void setFactsFont( LeanFont factsFont ) {
    this.factsFont = factsFont;
  }

  /**
   * Gets factsColor
   *
   * @return value of factsColor
   */
  public LeanColorRGB getFactsColor() {
    return factsColor;
  }

  /**
   * @param factsColor The factsColor to set
   */
  public void setFactsColor( LeanColorRGB factsColor ) {
    this.factsColor = factsColor;
  }

  /**
   * Gets titleFont
   *
   * @return value of titleFont
   */
  public LeanFont getTitleFont() {
    return titleFont;
  }

  /**
   * @param titleFont The titleFont to set
   */
  public void setTitleFont( LeanFont titleFont ) {
    this.titleFont = titleFont;
  }

  /**
   * Gets titleColor
   *
   * @return value of titleColor
   */
  public LeanColorRGB getTitleColor() {
    return titleColor;
  }

  /**
   * @param titleColor The titleColor to set
   */
  public void setTitleColor( LeanColorRGB titleColor ) {
    this.titleColor = titleColor;
  }

  /**
   * Gets axisColor
   *
   * @return value of axisColor
   */
  public LeanColorRGB getAxisColor() {
    return axisColor;
  }

  /**
   * @param axisColor The axisColor to set
   */
  public void setAxisColor( LeanColorRGB axisColor ) {
    this.axisColor = axisColor;
  }

  /**
   * Gets gridColor
   *
   * @return value of gridColor
   */
  public LeanColorRGB getGridColor() {
    return gridColor;
  }

  /**
   * @param gridColor The gridColor to set
   */
  public void setGridColor( LeanColorRGB gridColor ) {
    this.gridColor = gridColor;
  }
}
