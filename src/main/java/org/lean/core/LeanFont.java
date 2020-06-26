package org.lean.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.hop.metadata.api.HopMetadataProperty;

@JsonDeserialize( as = LeanFont.class )
public class LeanFont {

  @HopMetadataProperty
  @JsonProperty
  private String fontName;

  @HopMetadataProperty
  @JsonProperty
  private String fontSize;

  @HopMetadataProperty
  @JsonProperty
  private boolean bold;

  @HopMetadataProperty
  @JsonProperty
  private boolean italic;

  public LeanFont() {
  }

  public LeanFont( String fontName, String fontSize, boolean bold, boolean italic ) {
    this.fontName = fontName;
    this.fontSize = fontSize;
    this.bold = bold;
    this.italic = italic;
  }

  public LeanFont( LeanFont f ) {
    this( f.fontName, f.fontSize, f.bold, f.italic );
  }

  @Override public boolean equals( Object obj ) {
    if ( !( obj instanceof LeanFont ) ) {
      return false;
    }
    if ( obj == this ) {
      return true;
    }
    LeanFont font = (LeanFont) obj;

    boolean sameName = fontName == null && font.fontName == null || fontName != null && fontName.equals( font.fontName );
    boolean sameSize = fontSize == null && font.fontSize == null || fontSize != null && fontSize.equals( font.fontSize );
    boolean sameBold = bold == font.bold;
    boolean sameItalic = italic == font.italic;

    return sameName && sameSize && sameBold && sameItalic;
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontName( String fontName ) {
    this.fontName = fontName;
  }

  public String getFontSize() {
    return fontSize;
  }

  public void setFontSize( String fontSize ) {
    this.fontSize = fontSize;
  }

  public boolean isBold() {
    return bold;
  }

  public void setBold( boolean bold ) {
    this.bold = bold;
  }

  public boolean isItalic() {
    return italic;
  }

  public void setItalic( boolean italic ) {
    this.italic = italic;
  }
}
