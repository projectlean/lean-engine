package org.lean.core;

public class Constants {

  public static final String VARIABLE_PRESENTATION_NAME = "PRESENTATION_NAME";
  public static final String VARIABLE_PRESENTATION_DESCRIPTION = "PRESENTATION_DESCRIPTION";
  public static final String VARIABLE_PAGE_NUMBER = "PAGE_NUMBER";
  public static final String VARIABLE_SYSTEM_DATE = "SYSTEM_DATE";

  public static String NAMESPACE = "Lean";

  /** The name of the standard components to load into the Lean environment */
  public static final String XML_FILE_LEAN_COMPONENT_PLUGINS = "lean_components.xml";

  /** The system variable which points to the component plugins XML file */
  public static final String LEAN_CORE_COMPONENTS_FILE = "LEAN_CORE_COMPONENTS_FILE";

  /** The name of the standard data sources to load into the Lean environment */
  public static final String XML_FILE_LEAN_CONNECTOR_PLUGINS = "lean_connectors.xml";

  /** The system variable which points to the component plugins XML file */
  public static final String LEAN_CORE_CONNECTORS_FILE = "LEAN_CORE_CONNECTORS_FILE";




  /** The name of the metastore component factory */
  public static final String METASTORE_FACTORY_NAME_COMPONENTS = "METASTORE_FACTORY_NAME_COMPONENTS";

  /** The name of the metastore connectors factory */
  public static final String METASTORE_FACTORY_NAME_CONNECTORS = "METASTORE_FACTORY_NAME_CONNECTORS";

  /** The name of the metastore database factory */
  public static final String METASTORE_FACTORY_NAME_DATABASES = "METASTORE_FACTORY_NAME_DATABASES";

  /** The name of the metastore them factory */
  public static final String METASTORE_FACTORY_NAME_THEMES = "METASTORE_FACTORY_NAME_THEMES";

  /** The name of the metastore presentation factory */
  public static final String METASTORE_FACTORY_NAME_PRESENTATION = "METASTORE_FACTORY_NAME_PRESENTATION";


  /** The maximum number of remembered user history actions */
  public static final int USER_ACTION_HISTORY_SIZE = 10;


  public static final String DEFAULT_THEME_NAME = "Default";
  public static final String DEFAULT_THEME_DESCRIPTION = "Default test theme scheme";


}
