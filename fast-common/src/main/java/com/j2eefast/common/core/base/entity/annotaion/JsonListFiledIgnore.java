package com.j2eefast.common.core.base.entity.annotaion;

import com.j2eefast.common.core.enums.ConvertType;

import java.lang.annotation.*;

/**
 * @author huanzhou
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonListFiledIgnore {

    ConvertType convert() default ConvertType.EMPTY;


    boolean exist() default true;

    /**
     * 字段对象需要输出的字段
     * @return
     */
    String[] objectFiled() default {};
}
