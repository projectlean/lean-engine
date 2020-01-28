package org.lean.core;

import org.lean.rest.LeanServletContext;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;

public abstract class BaseLeanPluginType extends BasePluginType {

  public BaseLeanPluginType( Class<? extends Annotation> pluginType, String id, String name ) {
    super( pluginType, id, name );
  }

  protected void loadPluginsFromXmlFile( String xmlFile, String alternative, String pluginListElement, String pluginElement ) throws HopException {
    InputStream inputStream = getClass().getResourceAsStream( xmlFile );

    // For use in a servlet context
    //
    if ( LeanServletContext.isInitialized() ) {
      inputStream = LeanServletContext.getInstance().getResourceAsStream( "/WEB-INF/" + xmlFile );
    }

    // Regular files
    //
    if ( inputStream == null ) {
      inputStream = getClass().getResourceAsStream( "/" + xmlFile );
    }
    // Retry to load a regular file...
    if ( inputStream == null && !StringUtils.isEmpty( alternative ) ) {
      try {
        inputStream = new FileInputStream( xmlFile );
      } catch ( Exception e ) {
        throw new HopPluginException( "Unable to load native plugins '" + xmlFile + "'", e );
      }
    }
    if ( inputStream == null ) {
      throw new HopPluginException( "Unable to find native plugins definition file: " + xmlFile );
    }
    Document document = XMLHandler.loadXMLFile( inputStream, null, true, false );

    Node componentsNode = XMLHandler.getSubNode( document, pluginListElement );
    List<Node> componentNodes = XMLHandler.getNodes( componentsNode, pluginElement );
    for ( Node componentNode : componentNodes ) {
      registerPluginFromXmlResource( componentNode, null, this.getClass(), true, null );
    }
  }


  @Override
  protected String extractCasesUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractCategory( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractDesc( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractDocumentationUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractForumUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractI18nPackageName( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractImageFile( Annotation annotation ) {
    return null;
  }

  @Override
  protected boolean extractSeparateClassLoader( Annotation annotation ) {
    return false;
  }

  @Override protected String extractSuggestion( Annotation annotation ) {
    return null;
  }
}
