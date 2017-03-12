package no.difi.virksert.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to decorate enums used to point towards validation modes.
 *
 * @author erlend
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RecipePath {

    /**
     * Path to recipe file in class path.
     *
     * @return Path to recipe file in class path.
     */
    String value();

}
