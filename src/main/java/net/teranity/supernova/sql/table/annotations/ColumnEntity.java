package net.teranity.supernova.sql.table.annotations;

import net.teranity.supernova.sql.table.ColumnConstraint;
import net.teranity.supernova.sql.table.ColumnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnEntity {

    String name() default "";
    ColumnType type();

    int length() default 50;
    String defaultVal() default "";

    ColumnConstraint[] constraints() default {};

    String comment() default "";

}
