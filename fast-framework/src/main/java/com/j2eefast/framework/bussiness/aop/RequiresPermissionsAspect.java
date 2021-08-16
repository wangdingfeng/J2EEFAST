/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.bussiness.aop;

import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.core.xss.SQLFilter;
import com.j2eefast.framework.utils.Constant;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import java.util.ArrayList;
import java.util.List;
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

    @Value("#{ @environment['fast.xss.excludes'] ?: null }")
    private String excludes;

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
                List<String> tempExcludes = new ArrayList<>();
                if(ToolUtil.isNotEmpty(excludes)){
                    String[] url = excludes.split(",");
                    for (int i = 0; url != null && i < url.length; i++) {
                        tempExcludes.add(url[i]);
                    }
                }
                String path = ServletUtil.getRequest().getServletPath();
                //排除特例
                if(ToolUtil.isNotEmpty(tempExcludes)){
                    boolean flag = false;
                    for(String pattern: tempExcludes){
                        AntPathMatcher matcher = new AntPathMatcher();
                        if(matcher.match(pattern,path) ||
                                matcher.matchStart(pattern,path)){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        for (Object key : map.keySet()) {
                            if(map.keySet() !=null && map.get(key) instanceof  String){
                                SQLFilter.paramsVerify((String)map.get(key));
                            }
                        }
                    }
                }else{
                    for (Object key : map.keySet()) {
                        if(map.keySet() !=null && map.get(key) instanceof  String){
                            SQLFilter.paramsVerify((String)map.get(key));
                        }
                    }
                }
                break;
            }
        }
    }
}
