/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.mutidatasource.annotaion;

import java.lang.annotation.*;

/**
 * 多数据源标识- 可以注解到类 接口 方法
 * @author zhouzhou
 * @date 2020-03-12 09:55
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSource {
	
	/**
	 * 数据源名称
	 * <p> 
	 * 对应多源数据 数据库名称
	 */
	String name() default "";
	
}
