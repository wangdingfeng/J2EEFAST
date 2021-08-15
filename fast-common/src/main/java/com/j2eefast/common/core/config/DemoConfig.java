/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import java.io.IOException;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.j2eefast.common.core.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.ResponseData;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * 演示模式禁止修改 删除 新增 操作
 * @author zhouzhou
 * @date 2020-03-13 14:42
 */
@Configuration
@ConditionalOnProperty(prefix = "fast.demoMode", name = "enabled", havingValue="true")
public class DemoConfig {

	/**
	 * 拦截规则
	 */
	@Value("#{ @environment['fast.demoMode.post'] ?: 'edit,del,remove,clean,updateAvatar,updateUser,resetPwd,updatePass,genCode,uploadLic' }")
	private String post;
	@Value("#{ @environment['fast.demoMode.get'] ?: 'del,remove,clean,dirTreeData' }")
	private String get;
	@Value("#{ @environment['fast.demoMode.urlPatterns'] ?: '/sys/*,/tool/*' }")
	private String urlPatterns;
	@Value("#{ @environment['fast.demoMode.ip'] ?: '192.168.1.1' }")
	private String ip;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public FilterRegistrationBean demoFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new DemoFilter());
		registration.setOrder(Integer.MAX_VALUE-100);
        registration.addUrlPatterns(urlPatterns.split(","));
        registration.setName("demoFilter");
        return registration;
    }

	/**
	 *  拦截过滤器
	 * @author zhouzhou
	 * @date 2020-03-13 14:44
	 */
    class DemoFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        		FilterChain filterChain) throws IOException, ServletException {
        	HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String url = request.getRequestURI();
			String rip = ServletUtil.getIp();
			boolean flag = false;
			List<String> s = StrUtil.split(ip,StrUtil.COMMA);
			for(String c: s){
				if(StrUtil.trimToEmpty(c).equalsIgnoreCase(rip)){
					flag = true;
					break;
				}
			}
			if(!flag) {
				if(ConfigConstant.POST.equals(request.getMethod())) {
					String[] filters = post.split(StrUtil.COMMA);
					//判断是否包含
					for(String filter : filters){
						if(url.indexOf(filter) != -1){
							ResponseData r = ResponseData.error("10001", ConfigConstant.DEOM_MODE_PROMPT);
							String json = JSONUtil.toJsonStr(r);
							response.setContentType("application/json;charset=utf-8");
							response.getWriter().print(json);
							return;
						}
					}
				}

				if(ConfigConstant.GET.equals(request.getMethod())) {
					String queryString = request.getQueryString();
					url = url+"?"+queryString;
					String[] filters = get.split(StrUtil.COMMA);
					//判断是否包含
					for(String filter : filters){
						if(url.indexOf(filter) != -1 && !(url.indexOf("model") != -1)){
							ResponseData r = ResponseData.error("10001", ConfigConstant.DEOM_MODE_PROMPT);
							String json = JSONUtil.toJsonStr(r);
							response.setContentType("application/json;charset=utf-8");
							response.getWriter().print(json);
							return;
						}
					}
				}
			}
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
        }
    }
}
