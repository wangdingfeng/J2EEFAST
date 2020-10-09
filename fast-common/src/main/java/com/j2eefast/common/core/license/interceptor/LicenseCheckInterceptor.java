/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 */
package com.j2eefast.common.core.license.interceptor;

import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.config.LicenseCheckListener;
import com.j2eefast.common.core.license.LicenseVerify;
import com.j2eefast.common.core.license.annotation.FastLicense;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.common.core.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * <p>证书连接器</p>
 *
 * @author: zhouzhou Emall:18774995071@163.com
 * @date: 2020-03-18 09:15
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Component
public class LicenseCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private LicenseCheckListener listener;
    @Lazy
    @Resource
    private RedisSessionDAO redisSessionDAO;

    public LicenseCheckInterceptor(){
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            FastLicense annotation = method.getAnnotation(FastLicense.class);
            if (annotation != null) {
               String[] vertifys =  annotation.vertifys();
                LicenseVerify licenseVerify = new LicenseVerify();
               for(String vertify: vertifys){
                   if(vertify.equals("detection")){
                       //校验证书是否有效
                       boolean verifyResult = licenseVerify.verify(listener.getVerifyParam());
                       if (!verifyResult) {
                           response.setCharacterEncoding("utf-8");
                           response.setContentType("text/html;charset=utf-8");
                           response.getWriter().write(JSONUtil.parse(ResponseData.error("99998","您的证书无效，请核查服务器是否取得授权或重新申请证书!")).toString());
                           return false;
                       }
                   }
                   if(vertify.equals("online")){
                       int online = licenseVerify.onlineNumVerify(listener.getVerifyParam());
                       if (!redisSessionDAO.checkNumber(online)) {
                           response.setCharacterEncoding("utf-8");
                           response.setContentType("text/html;charset=utf-8");
                           response.getWriter().write(JSONUtil.parse(ResponseData.error("99997","在线用户已达最大,限制登录!!")).toString());
                           return false;
                       }
                   }
               }
            }
        }
        return true;
    }
}
