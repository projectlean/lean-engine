package org.lean.presentation.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.lean.core.exception.LeanException;
import org.lean.presentation.component.LeanComponent;

import java.util.ArrayList;
import java.util.List;

/** This represents one page in a presentation. */
public class LeanPage {

  @HopMetadataProperty private int pageNumber;

  @HopMetadataProperty private int width;

  @HopMetadataProperty private int height;

  @HopMetadataProperty private int leftMargin;

  @HopMetadataProperty private int rightMargin;

  @HopMetadataProperty private int topMargin;

  @HopMetadataProperty private int bottomMargin;

  @HopMetadataProperty private List<LeanComponent> components;

  @HopMetadataProperty private boolean header;

  @HopMetadataProperty private boolean footer;

  public LeanPage() {
    this.components = new ArrayList<>();
  }

  public LeanPage(
      int pageNumber,
      int width,
      int height,
      int leftMargin,
      int rightMargin,
      int topMargin,
      int bottomMargin) {
    this();
    this.pageNumber = pageNumber;
    this.width = width;
    this.height = height;
    this.leftMargin = leftMargin;
    this.rightMargin = rightMargin;
    this.topMargin = topMargin;
    this.bottomMargin = bottomMargin;
  }

  /**
   * Create a copy of the given page with everything on it.
   *
   * @param p
   */
  public LeanPage(LeanPage p) {
    this();
    this.pageNumber = p.pageNumber;
    this.width = p.width;
    this.height = p.height;
    this.leftMargin = p.leftMargin;
    this.rightMargin = p.rightMargin;
    this.topMargin = p.topMargin;
    this.bottomMargin = p.bottomMargin;
    this.header = p.header;
    this.footer = p.footer;
    for (LeanComponent c : p.components) {
      this.components.add(new LeanComponent(c));
    }
  }

  public static LeanPage getA4(int pageNumber, boolean portrait) {
    int width = 794;
    int height = 1123;
    if (portrait) {
      return new LeanPage(pageNumber, width, height, 25, 25, 25, 25);
    } else {
      return new LeanPage(pageNumber, height, width, 25, 25, 25, 25);
    }
  }

  public static LeanPage getHeaderFooter(boolean header, boolean portrait, int size) {
    // Exclude the margins of parent page!
    //
    int width = 794 - 25 - 25;
    int height = 1123 - 25 - 25;
    LeanPage page;
    if (portrait) {
      page = new LeanPage(0, width, size, 0, 0, 0, 0);
    } else {
      page = new LeanPage(0, height, size, 0, 0, 0, 0);
    }
    page.setHeader(header);
    page.setFooter(!header);
    page.setPageNumber(page.getFirstPageNumber());
    return page;
  }

  @JsonIgnore
  public int getFirstPageNumber() {
    if (isHeader()) {
      return -1;
    }
    if (isFooter()) {
      return -2;
    }
    return 1;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof LeanPage)) {
      return false;
    }
    if (o == this) {
      return true;
    }
    LeanPage page = (LeanPage) o;

    return page.getPageNumber() == pageNumber;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(pageNumber);
  }

  @JsonIgnore
  public int getWidthBetweenMargins() {
    return width - leftMargin - rightMargin;
  }

  /**
   * Find a component with a given name
   *
   * @param componentName
   * @return the component or null in case we can't find the component with the given name
   * @throws
   */
  public LeanComponent findComponent(String componentName) throws LeanException {
    for (LeanComponent component : components) {
      if (component.getName().equalsIgnoreCase(componentName)) {
        return component;
      }
    }
    return null;
  }

  /**
   * TODO: perform cocktail sort
   *
   * @return a sorted copy of the components
   */
  @JsonIgnore
  public List<LeanComponent> getSortedComponents() {
    return new ArrayList<>(components);
  }

  /** @return the components */
  public List<LeanComponent> getComponents() {
    return components;
  }

  /** @param components the components to set */
  public void setComponents(List<LeanComponent> components) {
    this.components = components;
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
   * Gets width
   *
   * @return value of width
   */
  public int getWidth() {
    return width;
  }

  /** @param width The width to set */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Gets height
   *
   * @return value of height
   */
  public int getHeight() {
    return height;
  }

  /** @param height The height to set */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Gets leftMargin
   *
   * @return value of leftMargin
   */
  public int getLeftMargin() {
    return leftMargin;
  }

  /** @param leftMargin The leftMargin to set */
  public void setLeftMargin(int leftMargin) {
    this.leftMargin = leftMargin;
  }

  /**
   * Gets rightMargin
   *
   * @return value of rightMargin
   */
  public int getRightMargin() {
    return rightMargin;
  }

  /** @param rightMargin The rightMargin to set */
  public void setRightMargin(int rightMargin) {
    this.rightMargin = rightMargin;
  }

  /**
   * Gets topMargin
   *
   * @return value of topMargin
   */
  public int getTopMargin() {
    return topMargin;
  }

  /** @param topMargin The topMargin to set */
  public void setTopMargin(int topMargin) {
    this.topMargin = topMargin;
  }

  /**
   * Gets bottomMargin
   *
   * @return value of bottomMargin
   */
  public int getBottomMargin() {
    return bottomMargin;
  }

  /** @param bottomMargin The bottomMargin to set */
  public void setBottomMargin(int bottomMargin) {
    this.bottomMargin = bottomMargin;
  }

  /**
   * Gets header
   *
   * @return value of header
   */
  public boolean isHeader() {
    return header;
  }

  /** @param header The header to set */
  public void setHeader(boolean header) {
    this.header = header;
  }

  /**
   * Gets footer
   *
   * @return value of footer
   */
  public boolean isFooter() {
    return footer;
  }

  /** @param footer The footer to set */
  public void setFooter(boolean footer) {
    this.footer = footer;
  }
}
