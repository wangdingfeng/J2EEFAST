/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.license.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目证书检测注解
 * @author zhouzhou
 * @date 2020-03-12 15:40
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastLicense {

    /**
     * 需要检测的名称 online 校验登录最大在线人数 detection 证书有效期校验
     * 默认值校验证书有效期
     * @return
     */
    String[] vertifys() default{"detection"};

}
