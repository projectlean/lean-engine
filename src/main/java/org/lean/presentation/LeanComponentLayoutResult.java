package org.lean.presentation;

import org.lean.core.LeanDataSet;
import org.lean.core.LeanGeometry;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;

import java.util.HashMap;
import java.util.Map;

/**
 * The result of doing a layout on a component with all the data read
 */
public class LeanComponentLayoutResult {
  /**
   * The component for which we did the layout
   */
  private LeanComponent component;

  /**
   * The part number for components split into multiple parts over multiple pages
   */
  private int partNumber;

  /**
   * The page from which this result originally came
   */
  private LeanPage sourcePage;

  /**
   * The page on which we render
   */
  private LeanRenderPage renderPage;

  /**
   * All the data read by the component, cached in memory
   */
  private LeanDataSet dataSet;

  /**
   * The resulting location and imageSize after the layout
   */
  private LeanGeometry geometry;

  /**
   * All extra data a component might want to store between doing a layout and the actual rendering of the component
   */
  private Map<String, Object> dataMap;

  public LeanComponentLayoutResult() {
    dataMap = new HashMap<>();
  }

  public LeanComponentLayoutResult( LeanComponentLayoutResult layoutResult ) {
    this.component = layoutResult.component;
    this.partNumber = layoutResult.partNumber;
    this.sourcePage = layoutResult.sourcePage;
    this.renderPage = layoutResult.renderPage;
    this.dataSet = layoutResult.dataSet;
    this.geometry = layoutResult.geometry;
    this.dataMap = layoutResult.dataMap;
  }

  /**
   * Gets component
   *
   * @return value of component
   */
  public LeanComponent getComponent() {
    return component;
  }

  /**
   * @param component The component to set
   */
  public void setComponent( LeanComponent component ) {
    this.component = component;
  }

  /**
   * Gets partNumber
   *
   * @return value of partNumber
   */
  public int getPartNumber() {
    return partNumber;
  }

  /**
   * @param partNumber The partNumber to set
   */
  public void setPartNumber( int partNumber ) {
    this.partNumber = partNumber;
  }

  /**
   * Gets sourcePage
   *
   * @return value of sourcePage
   */
  public LeanPage getSourcePage() {
    return sourcePage;
  }

  /**
   * @param sourcePage The sourcePage to set
   */
  public void setSourcePage( LeanPage sourcePage ) {
    this.sourcePage = sourcePage;
  }

  /**
   * Gets dataSet
   *
   * @return value of dataSet
   */
  public LeanDataSet getDataSet() {
    return dataSet;
  }

  /**
   * @param dataSet The dataSet to set
   */
  public void setDataSet( LeanDataSet dataSet ) {
    this.dataSet = dataSet;
  }

  /**
   * Gets geometry
   *
   * @return value of geometry
   */
  public LeanGeometry getGeometry() {
    return geometry;
  }

  /**
   * @param geometry The geometry to set
   */
  public void setGeometry( LeanGeometry geometry ) {
    this.geometry = geometry;
  }

  /**
   * Gets dataMap
   *
   * @return value of dataMap
   */
  public Map<String, Object> getDataMap() {
    return dataMap;
  }

  /**
   * @param dataMap The dataMap to set
   */
  public void setDataMap( Map<String, Object> dataMap ) {
    this.dataMap = dataMap;
  }

  /**
   * Gets renderPage
   *
   * @return value of renderPage
   */
  public LeanRenderPage getRenderPage() {
    return renderPage;
  }

  /**
   * @param renderPage The renderPage to set
   */
  public void setRenderPage( LeanRenderPage renderPage ) {
    this.renderPage = renderPage;
  }
}
