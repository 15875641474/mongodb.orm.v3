package com.jc.mongodb3_4.inteface;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据映射注解
 * @author joncch
 *
 */
@Target(ElementType.FIELD) //作用域-仅对参数
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	public boolean value() default false;
	
}
