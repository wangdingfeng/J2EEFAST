package com.j2eefast.common.core.base.entity.annotaion;

import com.j2eefast.common.core.enums.ConvertType;

import java.lang.annotation.*;

/**
 * 控制实体类Page分页-字段输出
 * @author huanzhou
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonListFiledIgnore {

    ConvertType convert() default ConvertType.EMPTY;

    /**
     * 默认不输出
     * @return
     */
    boolean exist() default true;

    /**
     * 字段对象需要输出的字段
     * @return
     */
    String[] objectFiled() default {};
}
