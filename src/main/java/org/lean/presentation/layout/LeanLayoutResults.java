package org.lean.presentation.layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.lean.core.LeanGeometry;
import org.lean.core.draw.DrawnItem;
import org.lean.core.exception.LeanException;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.page.LeanPage;

/** Contains layout results of a presentation */
public class LeanLayoutResults {

  private Map<String, LeanGeometry> componentGeometryMap;

  private Map<String, Map<String, Object>> componentDataSetMap;

  private List<LeanRenderPage> renderPages;

  private ILogChannel log;

  private String id;

  public LeanLayoutResults(ILogChannel log) {
    this.log = log;
    componentGeometryMap = new HashMap<>();
    componentDataSetMap = new HashMap<>();
    renderPages = new ArrayList<>();
    id = UUID.randomUUID().toString();
  }

  public LeanGeometry findGeometry(String componentName) {
    return componentGeometryMap.get(componentName);
  }

  public void addComponentGeometry(String componentName, LeanGeometry geometry) {
    componentGeometryMap.put(componentName, geometry);
  }

  public void addDataSet(LeanComponent component, String key, Object dataSet) {
    Map<String, Object> dataSetMap = componentDataSetMap.get(component.getName());
    if (dataSetMap == null) {
      dataSetMap = new HashMap<>();
      componentDataSetMap.put(component.getName(), dataSetMap);
    }
    dataSetMap.put(key, dataSet);
  }

  public Object getDataSet(LeanComponent component, String key) {
    Map<String, Object> dataSetMap = componentDataSetMap.get(component.getName());
    if (dataSetMap == null) {
      return null;
    }
    return dataSetMap.get(key);
  }

  public LeanRenderPage addNewPage(LeanPage page, LeanRenderPage currentRenderPage) {

    int pageNumber;
    if (currentRenderPage == null) {
      pageNumber = page.getFirstPageNumber();
    } else {
      pageNumber = currentRenderPage.getPageNumber() + 1;
    }
    // If we're dealing with a header or a footer we obviously don't want to add extra render pages
    //
    if (page.isHeader() || page.isFooter()) {
      if (currentRenderPage == null) {
        LeanRenderPage renderPage = new LeanRenderPage(page);
        renderPage.setPageNumber(pageNumber);
        renderPages.add(renderPage);
        return renderPage;
      } else {
        return currentRenderPage;
      }
    } else {
      // Regular rendering page: always create a new one...
      //
      LeanRenderPage renderPage = new LeanRenderPage(page);
      renderPage.setPageNumber(pageNumber);
      renderPages.add(renderPage);
      return renderPage;
    }
  }

  public LeanRenderPage getCurrentRenderPage(LeanPage page) {
    for (int i = renderPages.size() - 1; i >= 0; i--) {
      // The header & footer flag or the page number give us what we need.
      //
      if (page.isHeader() && renderPages.get(i).getPage().isHeader()
          || page.isFooter() && renderPages.get(i).getPage().isFooter()
          || renderPages.get(i).getPage().getPageNumber() == page.getPageNumber()) {
        return renderPages.get(i);
      }
    }

    // No page with this number found, create a new one...
    //
    return addNewPage(page, null);
  }

  public void replaceGCForHeaderFooter(HopSvgGraphics2D gc) throws LeanException {

    if (renderPages.size() > 1) {
      throw new LeanException("Multi-page headers or footers are not supported!");
    }
    if (renderPages.isEmpty()) {
      return;
    }

    LeanRenderPage renderPage = renderPages.get(0);
    renderPage.setGc(gc);
  }

  public void replaceDrawnItemsForHeaderFooter(List<DrawnItem> drawnItems) throws LeanException {
    if (renderPages.size() > 1) {
      throw new LeanException("Multi-page headers or footers are not supported!");
    }
    if (renderPages.isEmpty()) {
      return;
    }

    LeanRenderPage renderPage = renderPages.get(0);
    renderPage.setDrawnItems(drawnItems);
  }

  /** Set the page numbers on the render pages */
  public void setRenderPageNumbers() {
    for (int i = 0; i < renderPages.size(); i++) {
      renderPages.get(i).setPageNumber(i + 1);
    }
  }

  public void saveSvgPages(
      String baseFolder,
      String baseName,
      boolean convertToPdfs,
      boolean mergePdfs,
      boolean splitFolders)
      throws LeanException {

    if (splitFolders) {
      File baseParent = new File(baseFolder);
      if (!baseParent.exists()) {
        baseParent.mkdirs();
      }
      File svgsFolder = new File(baseParent.toString() + File.separator + "svgs");
      if (!svgsFolder.exists()) {
        svgsFolder.mkdirs();
      }
      File pdfsFolder = new File(baseParent.toString() + File.separator + "pdfs");
      if (!pdfsFolder.exists()) {
        pdfsFolder.mkdirs();
      }
      File pdfFolder = new File(baseParent.toString() + File.separator + "pdf");
      if (!pdfFolder.exists()) {
        pdfFolder.mkdirs();
      }
    }

    PDFMergerUtility mergerUtility = null;
    if (convertToPdfs && mergePdfs) {
      // For performance reasons
      System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
      mergerUtility = new PDFMergerUtility();
    }
    List<String> svgFilenames = new ArrayList<>();
    List<String> pdfFilenames = new ArrayList<>();

    for (int i = 0; i < renderPages.size(); i++) {
      LeanRenderPage renderPage = renderPages.get(i);

      String svgFilename;
      String pdfFilename;
      if (splitFolders) {
        svgFilename =
            baseFolder
                + File.separator
                + "svgs"
                + File.separator
                + baseName
                + "_"
                + (i + 1)
                + ".svg";
        pdfFilename =
            baseFolder
                + File.separator
                + "pdfs"
                + File.separator
                + baseName
                + "_"
                + (i + 1)
                + ".pdf";
      } else {
        svgFilename = baseFolder + File.separator + baseName + "_" + (i + 1) + ".svg";
        pdfFilename = baseFolder + File.separator + baseName + "_" + (i + 1) + ".pdf";
      }

      svgFilenames.add(svgFilename);
      pdfFilenames.add(pdfFilename);

      try (FileOutputStream stream = new FileOutputStream(svgFilename)) {
        try {
          stream.write(renderPage.getSvgXml().getBytes("UTF-8"));
          stream.flush();
        } catch (Exception e) {
          throw new LeanException("Unable to convert rendering to SVG XML", e);
        }

        // Save file to PDF as well...
        //
        if (convertToPdfs) {
          try (FileInputStream svgInputStream = new FileInputStream(svgFilename)) {
            TranscoderInput input = new TranscoderInput(svgInputStream);
            TranscoderOutput output = new TranscoderOutput(new FileOutputStream(pdfFilename));
            PDFTranscoder transcoder = new PDFTranscoder();
            transcoder.transcode(input, output);
          } catch (Exception e) {
            throw new LeanException(
                "Unable to transcode SVG '" + svgFilename + "' to PDF '" + pdfFilename, e);
          }

          if (mergePdfs) {
            mergerUtility.addSource(pdfFilename);
          }
        }

      } catch (IOException e) {
        throw new LeanException("Unable to write to file " + svgFilename, e);
      }
    }

    if (convertToPdfs && mergePdfs) {
      String pdfFilename;
      if (splitFolders) {
        pdfFilename = baseFolder + File.separator + "pdf" + File.separator + baseName + ".pdf";
      } else {
        pdfFilename = baseFolder + File.separator + baseName + ".pdf";
      }
      mergerUtility.setDestinationFileName(pdfFilename);
      try {
        mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
      } catch (IOException e) {
        throw new LeanException("Error merging PDF documents into " + pdfFilename, e);
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
  public void setRenderPages(List<LeanRenderPage> renderPages) {
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
  public void setLog(ILogChannel log) {
    this.log = log;
  }

  /**
   * Gets id
   *
   * @return value of id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id to set
   */
  public void setId(String id) {
    this.id = id;
  }
}
