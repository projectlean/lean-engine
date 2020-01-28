package org.lean.presentation;

import org.lean.core.Constants;
import org.lean.core.LeanGeometry;
import org.lean.core.exception.LeanException;
import org.lean.core.log.LeanMetricsUtil;
import org.lean.core.metastore.IHasIdentity;
import org.lean.core.svg.LeanSVGGraphics2D;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.datacontext.PresentationDataContext;
import org.lean.presentation.datacontext.RenderPageDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.theme.LeanTheme;
import org.lean.render.IRenderContext;
import org.lean.render.context.PresentationRenderContext;
import org.lean.rest.LeanRestBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.logging.LogChannelInterface;
import org.apache.hop.core.logging.LoggingObjectInterface;
import org.apache.hop.core.logging.Metrics;
import org.apache.hop.core.metrics.MetricsSnapshotType;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.persist.MetaStoreAttribute;
import org.apache.hop.metastore.persist.MetaStoreElementType;

import javax.ws.rs.Path;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@MetaStoreElementType(
  name = "Presentation",
  description = "Top level document of the presentation metadata" )
@Path( "presentations" )
public class LeanPresentation extends LeanRestBase<LeanPresentation> implements IHasIdentity {
  private String name;

  @MetaStoreAttribute
  private String description;

  @MetaStoreAttribute
  private List<LeanPage> pages;

  @MetaStoreAttribute
  private LeanPage header;

  @MetaStoreAttribute
  private LeanPage footer;

  @MetaStoreAttribute
  private List<LeanTheme> themes;

  @MetaStoreAttribute
  private String defaultThemeName;

  @MetaStoreAttribute( factoryNameReference = true, factoryNameKey = Constants.METASTORE_FACTORY_NAME_CONNECTORS, factorySharedIndicatorName = "shared" )
  private List<LeanConnector> connectors;

  public LeanPresentation() {
    super( LeanPresentation.class, "Presentation" );
    pages = new ArrayList<>();
    connectors = new ArrayList<>();
    themes = new ArrayList<>();
  }

  /**
   * Create a copy of every page, component and connector
   *
   * @param p
   */
  public LeanPresentation( LeanPresentation p ) {
    super( LeanPresentation.class, p.name );
    this.name = p.name;
    this.description = p.description;
    this.header = p.header == null ? null : new LeanPage( p.header );
    this.footer = p.footer == null ? null : new LeanPage( p.footer );
    this.pages = new ArrayList<>();
    for ( LeanPage page : p.pages ) {
      this.pages.add( new LeanPage( page ) );
    }
    this.connectors = new ArrayList<>();
    for ( LeanConnector c : p.connectors ) {
      this.connectors.add( new LeanConnector( c ) );
    }
    this.themes = new ArrayList<>();
    for ( LeanTheme t : p.themes ) {
      this.themes.add( new LeanTheme( t ) );
    }
  }

  @Override public String toString() {
    return name != null ? name : super.toString();
  }

  public String toJsonString() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString( this );
  }

  public static LeanPresentation fromJsonString( String jsonString ) throws IOException {
    return new ObjectMapper().readValue( jsonString, LeanPresentation.class );
  }

    /**
     * Perform the layout of this presentation.
     *
     * @param parent        the parent logging object
     * @param renderContext The rendering context
     * @param metaStore     The metaStore to reference external metadata with
     * @return The layout results
     * @throws LeanException
     */
  public LeanLayoutResults doLayout( LoggingObjectInterface parent, IRenderContext renderContext, IMetaStore metaStore ) throws LeanException {

    LogChannelInterface log = new LogChannel( getName(), parent, true );
    PresentationDataContext presentationDataContext = new PresentationDataContext( this, metaStore );
    LeanLayoutResults results = new LeanLayoutResults( log );

    log.logBasic( "Started layout of presentation" );
    log.snap( new Metrics( MetricsSnapshotType.START, LeanMetricsUtil.PRESENTATION_START_LAYOUT, "Presentation starts layout" ) );


    try {
      List<LeanPage> pagesCopy = new ArrayList<>( pages );
      pagesCopy.sort( Comparator.comparingInt( LeanPage::getPageNumber ) );

      // Loop over the components on every page, generate layout results...
      //
      for ( LeanPage page : pagesCopy ) {

        List<LeanComponent> sortedComponents = page.getSortedComponents();
        for ( LeanComponent leanComponent : sortedComponents ) {
          ILeanComponent component = leanComponent.getComponent();
          component.setLogChannel( log );
          component.processSourceData( this, page, leanComponent, presentationDataContext, renderContext, results );
          component.doLayout( this, page, leanComponent, presentationDataContext, renderContext, results );
        }
      }

      return results;
    } finally {
      log.snap( new Metrics( MetricsSnapshotType.STOP, LeanMetricsUtil.PRESENTATION_FINISH_LAYOUT, "Presentation finished layout" ) );
      log.logBasic( "Finished layout of presentation" );

    }

  }

  /**
   * Render this presentation by rendering all the render pages in the layout results...
   * At the end, we'll have some stuff drawn on the Graphics Context of each render page...
   *
   * @param results   Where to store rendering results
   * @param metaStore The metaStore to reference external metadata with
   * @return The presentation rendering log channel
   * @throws LeanException in case something goes wrong
   */
  public LogChannelInterface render( LeanLayoutResults results, IMetaStore metaStore ) throws LeanException {

    LogChannelInterface log = results.getLog();
    PresentationDataContext presentationDataContext = new PresentationDataContext( this, metaStore );
    PresentationRenderContext presentationRenderContext = new PresentationRenderContext( this );

    log.logBasic( "Started rendering presentation" );
    log.snap( new Metrics( MetricsSnapshotType.START, LeanMetricsUtil.PRESENTATION_START_RENDER, "Presentation starts rendering" ) );

    try {
      // Now that we know the layout, we know the page numbers.
      //
      results.setRenderPageNumbers();

      // Loop over all the pages that were allocated
      //
      for ( LeanRenderPage renderPage : results.getRenderPages() ) {
        LeanPage page = renderPage.getPage();
        SVGGraphics2D gc = renderPage.getGc();

        AffineTransform parentTransform = gc.getTransform();

        // First render header and footer if present
        //
        renderHeaderFooter( log, renderPage, parentTransform, presentationDataContext, presentationRenderContext );

        // Draw at top left of page
        //
        gc.translate( page.getLeftMargin(), page.getTopMargin() + getHeaderHeight() );

        // Loop over all the component layout results on the page...
        //
        List<LeanComponentLayoutResult> componentLayoutResults = renderPage.getLayoutResults();
        for ( LeanComponentLayoutResult componentLayoutResult : componentLayoutResults ) {
          LeanComponent leanComponent = componentLayoutResult.getComponent();

          // Render the component...
          //
          AffineTransform beforeRotation = gc.getTransform();

          // Do we rotate?
          // If so, rotate around the center of the object
          //
          if ( StringUtils.isNotEmpty( leanComponent.getRotation() ) ) {
            LeanGeometry geometry = componentLayoutResult.getGeometry();
            double angle = Math.toRadians( Const.toDouble( leanComponent.getRotation(), 0 ) );
            int originX = geometry.getX() + geometry.getWidth() / 2;
            int originY = geometry.getY() + geometry.getHeight() / 2;
            gc.rotate( angle, originX, originY );
          }

          // Transparency?
          //
          Composite beforeComposite = gc.getComposite();
          if ( StringUtils.isNotEmpty( leanComponent.getTransparency() ) ) {
            double alpha = Const.toDouble( leanComponent.getTransparency(), 0 ) / 100;
            if ( alpha > 1.0f ) {
              alpha = 1.0f;
            }
            if ( alpha < 0.0f ) {
              alpha = 0.0f;
            }
            gc.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float) alpha ) );
          }

          // Clipping of string drawing...
          boolean clip = leanComponent.getSize() != null && leanComponent.getSize().isDefined();
          Shape oldClip = gc.getClip();
          if ( clip ) {
            LeanGeometry lg = componentLayoutResult.getGeometry();
            gc.setClip( lg.getX(), lg.getY(), lg.getWidth(), lg.getHeight() );
          }

          LeanComponent component = componentLayoutResult.getComponent();
          component.getComponent().render( componentLayoutResult, results, presentationRenderContext );

          if ( clip ) {
            gc.setClip( oldClip );
          }

          // Remember where we've drawn this component
          //
          LeanGeometry componentGeometry = results.findGeometry( component.getName() );
          if ( componentGeometry != null ) {
            componentLayoutResult.getRenderPage().addComponentDrawnItem( component, componentGeometry );
          }

          gc.setComposite( beforeComposite );
          gc.setTransform( beforeRotation );
        }
      }

    } finally {
      log.snap( new Metrics( MetricsSnapshotType.STOP, LeanMetricsUtil.PRESENTATION_FINISH_RENDER, "Presentation finished rendering" ) );
      log.logBasic( "Finished rendering presentation" );
    }

    return log;

  }

  /**
   * Render the header and footers on top of the render page...
   */
  private void renderHeaderFooter( LogChannelInterface log, LeanRenderPage renderPage, AffineTransform parentTransform, IDataContext presentationDataContext, IRenderContext renderContext )
    throws LeanException {
    LeanPage page = renderPage.getPage();
    LeanSVGGraphics2D gc = renderPage.getGc();

    // What is the render context for header and footer?
    //
    RenderPageDataContext pageDataContext = new RenderPageDataContext( presentationDataContext, renderPage );

    if ( header != null ) {
      // Do the layout of the header on every page again...
      //
      List<LeanComponent> sortedComponents = header.getSortedComponents();

      // Create a new results object which maps onto the existing render page
      //
      LeanLayoutResults headerResults = new LeanLayoutResults( log );

      for ( LeanComponent component : sortedComponents ) {
        component.processAndLayout( log, this, header, pageDataContext, renderContext, headerResults );
      }

      // We did the layout and generated a new page for the header
      // It's contained in headerResults
      // We don't want to render on these RenderPages though, we want to render on the given renderPage.
      //
      headerResults.replaceGCForHeaderFooter( gc );

      // Before rendering, position rendering at the top of the page, after the margin...
      //
      gc.translate( page.getLeftMargin(), page.getTopMargin() );

      // Now render the header onto the given render page GC
      // Only one header "page" is supported
      //
      List<LeanComponentLayoutResult> componentLayoutResults = headerResults.getRenderPages().get( 0 ).getLayoutResults();
      for ( LeanComponentLayoutResult componentLayoutResult : componentLayoutResults ) {
        // Render the component...
        //
        componentLayoutResult.getComponent().getComponent().render( componentLayoutResult, headerResults, renderContext );
      }

      // Reset the gc translation...
      //
      gc.setTransform( parentTransform );
    }

    if ( footer != null ) {

      // Do the layout of the footer on every page again...
      //
      List<LeanComponent> sortedComponents = footer.getSortedComponents();

      // Create a new results object which maps onto the existing render page
      //
      LeanLayoutResults footerResults = new LeanLayoutResults( log );

      for ( LeanComponent leanComponent : sortedComponents ) {
        leanComponent.processAndLayout( log, this, footer, pageDataContext, renderContext, footerResults );
      }

      // We did the layout and generated a new page for the footer
      // It's contained in footerResults
      // We don't want to render on these RenderPages though, we want to render on the given renderPage.
      //
      footerResults.replaceGCForHeaderFooter( gc );

      // Before rendering, position rendering at the bottom of the page.
      // The position is the page height minus bottom margin and footer height
      //
      gc.translate( page.getLeftMargin(), page.getHeight() - page.getBottomMargin() - getFooterHeight() );

      // Now render the footer onto the given render page GC
      // Only one footer "page" is supported
      //
      List<LeanComponentLayoutResult> componentLayoutResults = footerResults.getRenderPages().get( 0 ).getLayoutResults();
      for ( LeanComponentLayoutResult componentLayoutResult : componentLayoutResults ) {
        // Render the component...
        //
        componentLayoutResult.getComponent().getComponent().render( componentLayoutResult, footerResults, renderContext );
      }

      // Reset the gc translation...
      //
      gc.setTransform( parentTransform );
    }
  }

  /**
   * Look for the connector with the given name and hand it's implementation
   *
   * @param name The name of the connector to look for.
   * @return The connector implementation or null if the connector couldn't be found
   */
  public LeanConnector getConnector( String name ) {
    for ( LeanConnector connector : connectors ) {
      if ( connector.getName().equalsIgnoreCase( name ) ) {
        return connector;
      }
    }
    return null;
  }

  /**
   * Look for the theme with the given name
   *
   * @param themeName the theme name to look for
   * @return The theme or null if nothing could be found
   */
  public LeanTheme lookupTheme( String themeName ) {
    if ( themeName == null ) {
      return null;
    }
    for ( LeanTheme theme : themes ) {
      if ( theme.getName().equalsIgnoreCase( themeName ) ) {
        return theme;
      }
    }
    return null;
  }

  /**
   * @return The default theme using the default theme name or null if it couldn't be found.
   */
  @JsonIgnore
  public LeanTheme getDefaultTheme() {
    return lookupTheme( defaultThemeName );
  }

  /**
   * Calculate how much usable room is on the base.
   * It's the height of the page minus the header imageSize, the footer imageSize and the page margins
   *
   * @param page The page to render on.
   * @return The usable height on the page
   */
  public int getUsableHeight( LeanPage page ) {
    int height = page.getHeight();
    height -= page.getTopMargin();
    height -= page.getBottomMargin();
    height -= getHeaderHeight();
    height -= getFooterHeight();
    return height;
  }

  @JsonIgnore
  public int getHeaderHeight() {
    if ( header == null ) {
      return 0;
    } else {
      return header.getHeight();
    }
  }

  @JsonIgnore
  public int getFooterHeight() {
    if ( footer == null ) {
      return 0;
    } else {
      return footer.getHeight();
    }
  }

  public LeanConnector findConnector( String connectorName ) {
    for ( LeanConnector connector : connectors ) {
      if ( connector.getName().equalsIgnoreCase( connectorName ) ) {
        return connector;
      }
    }
    return null;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * @return the connectors
   */
  public List<LeanConnector> getConnectors() {
    return connectors;
  }

  /**
   * @param connectors the connectors to set
   */
  public void setConnectors( List<LeanConnector> connectors ) {
    this.connectors = connectors;
  }

  /**
   * Gets pages
   *
   * @return value of pages
   */
  public List<LeanPage> getPages() {
    return pages;
  }

  /**
   * @param pages The pages to set
   */
  public void setPages( List<LeanPage> pages ) {
    this.pages = pages;
  }

  /**
   * Gets header
   *
   * @return value of header
   */
  public LeanPage getHeader() {
    return header;
  }

  /**
   * @param header The header to set
   */
  public void setHeader( LeanPage header ) {
    this.header = header;
  }

  /**
   * Gets footer
   *
   * @return value of footer
   */
  public LeanPage getFooter() {
    return footer;
  }

  /**
   * @param footer The footer to set
   */
  public void setFooter( LeanPage footer ) {
    this.footer = footer;
  }

  /**
   * Gets themes
   *
   * @return value of themes
   */
  public List<LeanTheme> getThemes() {
    return themes;
  }

  /**
   * @param themes The themes to set
   */
  public void setThemes( List<LeanTheme> themes ) {
    this.themes = themes;
  }

  /**
   * Gets defaultThemeName
   *
   * @return value of defaultThemeName
   */
  public String getDefaultThemeName() {
    return defaultThemeName;
  }

  /**
   * @param defaultThemeName The defaultThemeName to set
   */
  public void setDefaultThemeName( String defaultThemeName ) {
    this.defaultThemeName = defaultThemeName;
  }
}
