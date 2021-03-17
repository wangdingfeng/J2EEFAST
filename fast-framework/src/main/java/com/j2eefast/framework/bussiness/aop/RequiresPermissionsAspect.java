/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.bussiness.aop;

import com.j2eefast.common.core.xss.SQLFilter;
import com.j2eefast.framework.utils.Constant;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 处理请求权限控制字符保存请求Map
 * @author zhouzhou
 * @date 2020-12-09 14:53
 */
@Order(4)
@Aspect
@Component
public class RequiresPermissionsAspect {
	
    @Pointcut("@annotation(org.apache.shiro.authz.annotation.RequiresPermissions)")
    public void dataFilterCut() {

    }

    @SuppressWarnings("unchecked")
    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        RequiresPermissions dataFilter = signature.getMethod().getAnnotation(RequiresPermissions.class);
        Object[] params = point.getArgs();
        for(Object o:  params){
            if (o != null && o instanceof Map) {
                Map map = (Map) o;
                map.put(Constant.REQUIRES_PERMISSIONS,dataFilter.value());
                for (Object key : map.keySet()) {
                    if(map.keySet() !=null && map.get(key) instanceof  String){
                        SQLFilter.sqlInject((String)map.get(key));
                    }
                }
                break;
            }
        }
    }
}
