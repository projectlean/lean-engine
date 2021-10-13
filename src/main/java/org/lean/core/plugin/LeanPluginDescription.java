package org.lean.core.plugin;

import java.util.ArrayList;
import java.util.List;

public class LeanPluginDescription implements Comparable<LeanPluginDescription> {
  private String id;
  private String name;
  private String description;
  private String className;
  private List<String> libraries;

  public LeanPluginDescription() {
    libraries = new ArrayList<String>();
  }

  public LeanPluginDescription(
      String id, String name, String description, String className, List<String> libraries) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.className = className;
    this.libraries = libraries;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<String> getLibraries() {
    return libraries;
  }

  public void setLibraries(List<String> libraries) {
    this.libraries = libraries;
  }

  @Override
  public int compareTo(LeanPluginDescription leanPluginDescription) {
    return id.compareTo(leanPluginDescription.id);
  }
}
