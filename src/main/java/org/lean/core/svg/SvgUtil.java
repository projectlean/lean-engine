package org.lean.core.svg;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.hop.core.xml.XmlHandler;
import org.lean.core.exception.LeanException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SvgUtil {

  public static final LeanSVGGraphics2D createGc() {
    DOMImplementation domImplementation = GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    String svgNamespace = "http://www.w3.org/2000/svg";
    Document document = domImplementation.createDocument( svgNamespace, "svg", null );

    return new LeanSVGGraphics2D( document );
  }

  public static Document loadSvgDom( String svgXml ) throws LeanException {

    try {
      Document document = XmlHandler.loadXmlString( svgXml );
      return document;
    } catch ( Exception e ) {
      throw new LeanException( "Unable to parse SVG XML : " + svgXml, e );
    }

  }

  public static Node createNodeCopy( Document document, Node node ) throws LeanException {
    String tagName = node.getNodeName();
    try {

      return document.importNode( node, true );

      /*
      Element element = document.createElement( tagName.replace( "#", "" )  );
      element.setNodeValue( node.getNodeValue() );

      // Copy all the attributes...
      //
      NamedNodeMap attributes = node.getAttributes();
      if (attributes!=null) {
        for ( int i = 0; i < attributes.getLength(); i++ ) {
          Node item = attributes.item( i );
          element.setAttribute( item.getNodeName(), item.getNodeValue() );
        }
      }

      // Copy all the children...
      //
      NodeList childNodes = node.getChildNodes();
      for ( int i = 0; i < childNodes.getLength(); i++ ) {
        Node childNode = childNodes.item( i );

        // If we're dealing with a CDATA section, this is not a real child, it's simply the data section
        //
        if ( node.getNodeValue() == null && childNode.getNodeName().startsWith( "#" ) ) {
          element.setTextContent( node.getTextContent() );
        } else {
          Node childNodeCopy = createNodeCopy( document, childNode );
          element.appendChild( childNodeCopy );
        }
      }

      return element;
       */
    } catch ( Exception e ) {
      throw new LeanException( "Unable to copy node with name " + tagName, e );
    }
  }
}
