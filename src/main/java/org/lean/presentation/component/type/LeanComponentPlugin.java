package org.lean.presentation.component.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LeanComponentPlugin {

  /** @return The ID of the component */
  String id();

  /** @return The name of the component */
  String name();

  /** @return The description of the component */
  String description() default "";
}
