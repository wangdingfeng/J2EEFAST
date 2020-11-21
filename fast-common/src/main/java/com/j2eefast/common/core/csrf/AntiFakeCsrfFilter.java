/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.csrf;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.*;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * POST CSRF攻击 拦截
 * @author huanzhou
 */
@Configuration
@ConditionalOnProperty(prefix = "fast.csrf", name = "enabled", havingValue="true")
public class AntiFakeCsrfFilter {

    @Lazy
    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 防盗链URL
     */
    @Value("${fast.csrf.urlReferer: }")
    private String urlReferer;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public FilterRegistrationBean csrfFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CsrfFilter());
        registration.setOrder(Integer.MAX_VALUE -2);
        registration.addUrlPatterns("/*");
        registration.setName("csrfFilter");
        return registration;
    }

    /**
     *  拦截过滤器
     * @author zhouzhou
     * @date 2020-03-13 14:44
     */
    class CsrfFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                             FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String path = request.getServletPath();
            // 如果是静态文件，直接放开
            if (isStaticFile(path)){
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            if("POST".equals(request.getMethod())) {

                String referer = request.getHeader("Referer");
                if(ToolUtil.isNotEmpty(urlReferer)){
                   boolean flag = false;
                   String[] urlReferers =  StrUtil.split(urlReferer,",");
                   for(String s: urlReferers){
                       if(StrUtil.startWith(referer,s)){
                           flag = true;
                           break;
                       }
                   }
                   if(!flag){
                       ResponseData r = ResponseData.error("70001", ConfigConstant.REQUEST_PROMPT);
                       String json = JSONUtil.toJsonStr(r);
                       response.setStatus(406);
                       response.setContentType("application/json;charset=utf-8");
                       response.getWriter().print(json);
                       return;
                   }
                }
                String csrfToken= request.getHeader("X-CSRF-Token");
                String ipnutCsrfToken = request.getParameter("X-CSRF-Token");
                String sysCsrfToken = (String) redisUtil.getSession("sys_csrfToken:shiro:session:"+CookieUtil.getCookie(request,ConfigConstant.FAST_SESSION_ID));

                if(ToolUtil.isEmpty(csrfToken) &&  ToolUtil.isEmpty(ipnutCsrfToken)){
                    ResponseData r = ResponseData.error("70001", ConfigConstant.REQUEST_PROMPT);
                    String json = JSONUtil.toJsonStr(r);
                    response.setStatus(406);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().print(json);
                    return;
                }

                if(sysCsrfToken.equals(csrfToken)
                        || sysCsrfToken.equals(ipnutCsrfToken)){
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
                ResponseData r = ResponseData.error("70001", ConfigConstant.REQUEST_PROMPT);
                String json = JSONUtil.toJsonStr(r);
                response.setStatus(406);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(json);
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        @Override
        public void destroy() {
        }


        private boolean isStaticFile(String path) {
            Map<String, String> filterMap = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            filterMap.put("/profile/**","anon");
            filterMap.put("/druid","anon");
            for(String key:filterMap.keySet()){
                String value = filterMap.get(key);
                PathMatcher matcher = new AntPathMatcher();
                if(value.equals("anon") && matcher.match(key,path)){
                    return true;
                }
            }
            return false;
        }
    }
}
