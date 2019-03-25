package com.jc.mongodb3_4.inteface;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表注解
 * @author joncch
 *
 */
@Target(ElementType.TYPE) //作用域-任何地方
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	public String value() default "";
}
