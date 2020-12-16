/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.base.entity.annotaion;

import java.lang.annotation.*;

/**
 * 分页控制输出实体类基类字段控制
 * @date 2020-09-29
 * @author zhouzhou
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonListBaselgonre {

    /**
     * 基类需要输出的字段  默认就全部输出
     * @return
     */
    String[] fileds() default {};
}
