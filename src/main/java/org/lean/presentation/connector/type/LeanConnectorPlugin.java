package org.lean.presentation.connector.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface LeanConnectorPlugin {

  /**
   * @return The ID of the connector
   */
  String id();

  /**
   * @return The name of the connector
   */
  String name();

  /**
   * @return The description of the connector
   */
  String description();


}
