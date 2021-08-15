/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Web客户端工具
 * @author zhouzhou
 * @date 2020-03-12 14:56
 */
public class ServletUtil {
	
	
	/**
     * 获取请求的ip地址
     */
    public static String getIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "127.0.0.1";
        } else {
        	return NetUtil.getMultistageReverseProxyIp(request.getRemoteAddr());
        }
    }
	
	/**
     * 获取所有请求的值
     */
    public static Map<String, String> getRequestParameters() {
        HashMap<String, String> values = new HashMap<>();
        HttpServletRequest request = getRequest();
        if (request == null) {
            return values;
        }
        @SuppressWarnings("rawtypes")
		Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);
            values.put(paramName, paramValue);
        }
        return values;
    }
	
	/**
     * 获取String参数
     */
    public static String getParameter(String name){
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue){
        return StrUtil.blankToDefault(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name){
        return NumberUtil.parseNumber((getRequest().getParameter(name))).intValue();
    }


    /**
     * 获取request
     */
    public static HttpServletRequest getRequest(){
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse(){
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession(){
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes(){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     */
    public static String renderString(HttpServletResponse response, String string){
    	String utf8			  = "utf-8";
    	String application	  = "application/json";
    	
        try{
            response.setContentType(application);
            response.setCharacterEncoding(utf8);
            response.getWriter().print(string);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request){
    	
    	String acc 				= "accept";
    	String application		= "application/json";
    	String requested 		= "X-Requested-With";
    	String xmlHttpRequest   = "XMLHttpRequest";
    	String json		 		= ".json";
    	String xml		 		= ".xml";
    	String json1		 	= "json";
    	String xml1		 		= "xml";
    	String ajax1			= "__ajax";
    	
        String accept = request.getHeader(acc);
        if (accept != null && accept.indexOf(application) != -1){
            return true;
        }

        String xRequestedWith = request.getHeader(requested);
        if (xRequestedWith != null && xRequestedWith.indexOf(xmlHttpRequest) != -1){
            return true;
        }

        String uri = request.getRequestURI();
        if (StrUtil.containsAny(uri, json, xml)){
            return true;
        }

        String ajax = request.getParameter(ajax1);
        if (StrUtil.containsAny(ajax, json1, xml1)){
            return true;
        }

        return false;
    }
}
