package org.lean.presentation.layout;

import org.lean.core.LeanGeometry;
import org.lean.core.exception.LeanException;
import org.lean.core.svg.LeanSVGGraphics2D;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.page.LeanPage;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.hop.core.logging.ILogChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains layout results of a presentation
 */
public class LeanLayoutResults {

  private Map<String, LeanGeometry> componentGeometryMap;

  private Map<String, Map<String, Object>> componentDataSetMap;

  private List<LeanRenderPage> renderPages;

  private ILogChannel log;

  public LeanLayoutResults( ILogChannel log ) {
    this.log = log;
    componentGeometryMap = new HashMap<>();
    componentDataSetMap = new HashMap<>();
    renderPages = new ArrayList<>();
  }

  public LeanGeometry findGeometry( String componentName ) {
    return componentGeometryMap.get( componentName );
  }

  public void addComponentGeometry( String componentName, LeanGeometry geometry ) {
    componentGeometryMap.put( componentName, geometry );
  }

  public void addDataSet( LeanComponent component, String key, Object dataSet ) {
    Map<String, Object> dataSetMap = componentDataSetMap.get( component.getName() );
    if ( dataSetMap == null ) {
      dataSetMap = new HashMap<>();
      componentDataSetMap.put( component.getName(), dataSetMap );
    }
    dataSetMap.put( key, dataSet );
  }

  public Object getDataSet( LeanComponent component, String key ) {
    Map<String, Object> dataSetMap = componentDataSetMap.get( component.getName() );
    if ( dataSetMap == null ) {
      return null;
    }
    return dataSetMap.get( key );
  }

  public LeanRenderPage addNewPage( LeanPage page, int pageNumber ) {
    LeanRenderPage renderPage = new LeanRenderPage( page );
    renderPage.setPageNumber( pageNumber );
    renderPages.add( renderPage );
    return renderPage;
  }

  public LeanRenderPage getCurrentRenderPage( LeanPage page ) {
    LeanRenderPage renderPage = null;

    for ( int i = renderPages.size() - 1; i >= 0; i-- ) {
      if ( renderPages.get( i ).getPage().getPageNumber() == page.getPageNumber() ) {
        renderPage = renderPages.get( i );
        break;
      }
    }

    // No page with this number found, create a new one...
    //
    if ( renderPage == null ) {
      renderPage = addNewPage( page, 1 );
    }

    return renderPage;
  }

  public void replaceGCForHeaderFooter( LeanSVGGraphics2D gc ) throws LeanException {

    if ( renderPages.size() > 1 ) {
      throw new LeanException( "Multi-page headers or footers are not supported!" );
    }
    if ( renderPages.isEmpty() ) {
      throw new LeanException( "At least one header or footer page was expected!" );
    }

    LeanRenderPage renderPage = renderPages.get( 0 );
    renderPage.setGc( gc );
  }

  /**
   * Set the page numbers on the render pages
   */
  public void setRenderPageNumbers() {
    for ( int i = 0; i < renderPages.size(); i++ ) {
      renderPages.get( i ).setPageNumber( i + 1 );
    }
  }


  public void saveSvgPages( String baseFolder, String baseName, boolean convertToPdfs, boolean mergePdfs, boolean splitFolders ) throws LeanException {

    if ( splitFolders ) {
      File baseParent = new File( baseFolder );
      if ( !baseParent.exists() ) {
        baseParent.mkdirs();
      }
      File svgsFolder = new File( baseParent.toString() + File.separator + "svgs" );
      if ( !svgsFolder.exists() ) {
        svgsFolder.mkdirs();
      }
      File pdfsFolder = new File( baseParent.toString() + File.separator + "pdfs" );
      if ( !pdfsFolder.exists() ) {
        pdfsFolder.mkdirs();
      }
      File pdfFolder = new File( baseParent.toString() + File.separator + "pdf" );
      if ( !pdfFolder.exists() ) {
        pdfFolder.mkdirs();
      }
    }

    PDFMergerUtility mergerUtility = null;
    if ( convertToPdfs && mergePdfs ) {
      // For performance reasons
      System.setProperty( "sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider" );
      mergerUtility = new PDFMergerUtility();
    }
    List<String> svgFilenames = new ArrayList<>();
    List<String> pdfFilenames = new ArrayList<>();

    for ( int i = 0; i < renderPages.size(); i++ ) {
      LeanRenderPage renderPage = renderPages.get( i );

      String svgFilename;
      String pdfFilename;
      if ( splitFolders ) {
        svgFilename = baseFolder + File.separator + "svgs" + File.separator + baseName + "_" + ( i + 1 ) + ".svg";
        pdfFilename = baseFolder + File.separator + "pdfs" + File.separator + baseName + "_" + ( i + 1 ) + ".pdf";
      } else {
        svgFilename = baseFolder + File.separator + baseName + "_" + ( i + 1 ) + ".svg";
        pdfFilename = baseFolder + File.separator + baseName + "_" + ( i + 1 ) + ".pdf";
      }

      svgFilenames.add( svgFilename );
      pdfFilenames.add( pdfFilename );

      try {
        FileOutputStream stream = new FileOutputStream( svgFilename );
        try {
          stream.write(renderPage.getSvgXml().getBytes("UTF-8"));
          stream.flush();
        } catch ( Exception e ) {
          throw new LeanException( "Unable to convert rendering to SVG XML", e );
        } finally {
          try {
            stream.close();
          } catch ( IOException e ) {
            throw new LeanException( "Unable to close file", e );
          }
        }

        // Save file to PDF as well...
        //
        if ( convertToPdfs ) {
          try {

            FileInputStream svgInputStream = new FileInputStream( svgFilename );
            TranscoderInput input = new TranscoderInput( svgInputStream );
            TranscoderOutput output = new TranscoderOutput( new FileOutputStream( pdfFilename ) );
            PDFTranscoder transcoder = new PDFTranscoder();
            transcoder.transcode( input, output );

          } catch ( Exception e ) {
            throw new LeanException( "Unable to transcode SVG '" + svgFilename + "' to PDF '" + pdfFilename, e );
          }

          if ( mergePdfs ) {
            mergerUtility.addSource( pdfFilename );
          }
        }

      } catch ( IOException e ) {
        throw new LeanException( "Unable to write to file " + svgFilename, e );
      }
    }

    if ( convertToPdfs && mergePdfs ) {
      String pdfFilename;
      if ( splitFolders ) {
        pdfFilename = baseFolder + File.separator + "pdf" + File.separator + baseName + ".pdf";
      } else {
        pdfFilename = baseFolder + File.separator + baseName + ".pdf";
      }
      mergerUtility.setDestinationFileName( pdfFilename );
      try {
        mergerUtility.mergeDocuments( MemoryUsageSetting.setupMainMemoryOnly() );
      } catch ( IOException e ) {
        throw new LeanException( "Error merging PDF documents into " + pdfFilename, e );
      }
    }
  }


  /**
   * Gets renderPages
   *
   * @return value of renderPages
   */
  public List<LeanRenderPage> getRenderPages() {
    return renderPages;
  }

  /**
   * @param renderPages The renderPages to set
   */
  public void setRenderPages( List<LeanRenderPage> renderPages ) {
    this.renderPages = renderPages;
  }

  /**
   * Gets log
   *
   * @return value of log
   */
  public ILogChannel getLog() {
    return log;
  }

  /**
   * @param log The log to set
   */
  public void setLog( ILogChannel log ) {
    this.log = log;
  }

}

