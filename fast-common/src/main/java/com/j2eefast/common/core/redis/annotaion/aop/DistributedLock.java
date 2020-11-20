package com.j2eefast.common.core.redis.annotaion.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
  * @ClassName DistributedLock
  * @Description 
  * @author mfksn001@163.com
  * @date 2020年9月9日
*/
@Target({ElementType.METHOD })
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    //锁名称
    String value() default "";
   
    //exmpleEntity.id 
    //方法中对像参数 属性名 格式为： 对像.属性名 或都  属性名  
    String classAttrName() default "";
    
    //释放时间
    long releaseTime() default 20;
    
    //try wait time
    long waitTime() default 0L;

    //时间单位
    TimeUnit timeUnit() default TimeUnit.SECONDS;  
}