package org.lean.presentation.layout;

import org.apache.hop.core.gui.Point;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
import org.lean.core.draw.DrawnItem;
import org.lean.core.draw.DrawnItem.DrawnItemType;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.page.LeanPage;

import java.awt.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.lean.core.draw.DrawnItem.*;

/** A page on which we can render... */
public class LeanRenderPage {
  /** The original page */
  private LeanPage page;

  /** The render page number (1 based) */
  private int pageNumber;

  /** The graphics context */
  private HopSvgGraphics2D gc;

  /** All the component fragments on this page */
  private List<LeanComponentLayoutResult> layoutResults;

  private List<DrawnItem> drawnItems;

  public LeanRenderPage() {
    layoutResults = new ArrayList<>();
  }

  public LeanRenderPage(LeanPage page) {
    this();
    this.page = page;

    gc = HopSvgGraphics2D.newDocument();

    // Set the imageSize to be the imageSize of the page...
    //
    gc.setSVGCanvasSize(new Dimension(page.getWidth(), page.getHeight()));

    this.drawnItems = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "LeanRenderPage(#" + pageNumber + ")";
  }

  public String getSvgXml() throws LeanException {
    try {
      StringWriter stringWriter = new StringWriter();
      gc.stream(stringWriter, true);
      return stringWriter.toString();
    } catch (Exception e) {
      throw new LeanException("Error converting SVG to XML", e);
    }
  }

  public void addDrawnItem(
      String componentName,
      String componentPluginId,
      int partNumber,
      DrawnItemType type,
      String category,
      int rowNr,
      int colNr,
      LeanGeometry geometry) {
    drawnItems.add(new DrawnItem(componentName, componentPluginId, partNumber, type, category, rowNr, colNr, geometry));
  }

  public void addComponentDrawnItem( LeanComponent component, LeanGeometry componentGeometry, LeanPosition offSet ) {

    LeanGeometry geometry = new LeanGeometry(componentGeometry);
    geometry.translate(offSet);

    addDrawnItem(
        component.getName(),
        component.getComponent().getPluginId(),
        0,
        DrawnItemType.Component,
        Category.ComponentArea.name(),
        0,
        0,
        geometry);
  }

  /**
   * Lookup the component names given a location on the page in the order they were drawn
   *
   * @param x
   * @param y
   * @return
   */
  public List<String> lookupComponentName(int x, int y) {
    List<String> componentNames = new ArrayList<>();
    for (DrawnItem item : drawnItems) {
      if (item.getGeometry().contains(x, y)) {
        componentNames.add(item.getComponentName());
      }
    }
    return new ArrayList<>(componentNames);
  }

  /**
   * Lookup the last drawn item given a location on the page in the order they were drawn
   *
   * @param x
   * @param y
   * @return The last drawn item or null if nothing was found
   */
  public DrawnItem lookupDrawnItem(int x, int y) {
    return lookupDrawnItem(x, y, false);
  }

  /**
   * Lookup the last drawn item given a location on the page in the order they were drawn
   *
   * @param x
   * @param y
   * @param excludeComponents
   * @return The last drawn item or null if nothing was found
   */
  public DrawnItem lookupDrawnItem(int x, int y, boolean excludeComponents) {
    for (int i = drawnItems.size() - 1; i >= 0; i--) {
      DrawnItem item = drawnItems.get(i);
      if (!excludeComponents || item.getType()!=DrawnItemType.Component) {
        if (item.getGeometry().contains( x, y )) {
          return item;
        }
      }
    }
    return null;
  }

  public LeanGeometry lookupComponentGeometry(String componentName) {
    for (DrawnItem item : drawnItems) {
      if (item.getType()==DrawnItemType.Component) {
        if (item.getComponentName().equals( componentName )) {
          return item.getGeometry();
        }
      }
    }
    return null;
  }

  /**
   * Gets page
   *
   * @return value of page
   */
  public LeanPage getPage() {
    return page;
  }

  /** @param page The page to set */
  public void setPage(LeanPage page) {
    this.page = page;
  }

  /**
   * Gets gc
   *
   * @return value of gc
   */
  public HopSvgGraphics2D getGc() {
    return gc;
  }

  /** @param gc The gc to set */
  public void setGc(HopSvgGraphics2D gc) {
    this.gc = gc;
  }

  /**
   * Gets layoutResults
   *
   * @return value of layoutResults
   */
  public List<LeanComponentLayoutResult> getLayoutResults() {
    return layoutResults;
  }

  /** @param layoutResults The layoutResults to set */
  public void setLayoutResults(List<LeanComponentLayoutResult> layoutResults) {
    this.layoutResults = layoutResults;
  }

  /**
   * Gets pageNumber
   *
   * @return value of pageNumber
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /** @param pageNumber The pageNumber to set */
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  /**
   * Gets drawnItems
   *
   * @return value of drawnItems
   */
  public List<DrawnItem> getDrawnItems() {
    return drawnItems;
  }

  /** @param drawnItems The drawnItems to set */
  public void setDrawnItems(List<DrawnItem> drawnItems) {
    this.drawnItems = drawnItems;
  }
}
