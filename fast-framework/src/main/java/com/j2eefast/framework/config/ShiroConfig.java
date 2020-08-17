package com.j2eefast.framework.config;

import java.io.Serializable;
import java.util.*;
import cn.hutool.core.util.IdUtil;
import com.j2eefast.common.core.shiro.RedisCacheManager;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.common.core.shiro.ShiroSessionManager;
import com.j2eefast.framework.shiro.KickoutSessionControlFilter;
import com.j2eefast.framework.shiro.realm.FreeRealm;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.j2eefast.framework.shiro.ShiroSessionListener;
import com.j2eefast.framework.shiro.realm.UserRealm;
import javax.servlet.Filter;

/**
 * Shiro的配置文件
 */
@Configuration
public class ShiroConfig {

	/**
	 * Session超时时间，单位为毫秒（默认30分钟）
	 */
	@Value("${shiro.session.expireTime: 30}")
	private int expireTime;

	/**
	 * 设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 (默认设置5分钟)
	 */
	@Value("${shiro.session.validationTime: 5}")
	private int validationTime;

	/**
	 * 设置Cookie的域名
	 */
	@Value("${shiro.cookie.domain: ''}")
	private String domain;

	/**
	 * 设置cookie的有效访问路径设置与项目路径一直
	 */
	@Value("${server.servlet.contextPath: /fast}")
	private String path;

	/**
	 * 设置HttpOnly属性
	 */
	@Value("${shiro.cookie.httpOnly: true}")
	private boolean httpOnly;

	/**
	 * 设置Cookie的过期时间，秒为单位
	 */
	@Value("${shiro.cookie.maxAge: 30}")
	private int maxAge;

	/**
	 * 设置session cookie属性
	 * @return
	 */
	@Bean
    public Cookie cookieDAO() {
       Cookie cookie= new org.apache.shiro.web.servlet.SimpleCookie();
       cookie.setName("fast.session.id");
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(-1);
       return cookie;
    }

	@Bean("sessionManager")
	public SessionManager sessionManager() {
		ShiroSessionManager sessionManager = new ShiroSessionManager();
		Collection<SessionListener> listeners = new ArrayList<SessionListener>();
		// 配置监听
		listeners.add(sessionListener());
		sessionManager.setSessionListeners(listeners);
		//redis缓存
		sessionManager.setSessionDAO(sessionDAO());
		sessionManager.setCacheManager(getcacheManager());
		// 设置session过期时间为1小时(单位：毫秒)，默认为30分钟
		sessionManager.setGlobalSessionTimeout(1000 * 60 * expireTime);
		// 去掉 JSESSIONID
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		sessionManager.setSessionIdCookie(cookieDAO());

		// 是否开启删除无效的session对象 默认为true
		sessionManager.setDeleteInvalidSessions(true);

		// 是否开启定时调度器进行检测过期session 默认为true
		sessionManager.setSessionValidationSchedulerEnabled(true);

		// 设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
		sessionManager.setSessionValidationInterval(1000 * 60 * validationTime);

		return sessionManager;
	}

	/**
	 * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
	 * MemorySessionDAO 直接在内存中进行会话维护
	 * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
	 * @return
	 */
	@Bean
	public RedisSessionDAO sessionDAO() {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		//session在redis中的保存时间,最好大于session会话超时时间
		redisSessionDAO.setExpire(1000 * 60 * expireTime + 100);
		redisSessionDAO.setSessionIdGenerator(new CustomSessionIdGenerator());
		return redisSessionDAO;
	}

	/**
	 * shiro缓存管理器;
	 * 需要添加到securityManager中
	 * @return
	 */
	@Bean
	public RedisCacheManager getcacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		//redis中针对不同用户缓存
		redisCacheManager.setPrincipalIdFieldName("username");
		//用户权限信息缓存时间
		redisCacheManager.setExpire(200000);
		return redisCacheManager;
	}

	/**
	 * 自定义Realm
	 */
	@Bean
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		userRealm.setCacheManager(getcacheManager());
		return userRealm;
	}

	@Bean
	public FreeRealm freeRealm() {
		FreeRealm freeRealm = new FreeRealm();
		freeRealm.setCacheManager(getcacheManager());
		return freeRealm;
	}


	@Bean("securityManager")
	public SecurityManager securityManager() {
		
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

		securityManager.setAuthenticator(modularRealmAuthenticator());

		List<Realm> realms = new ArrayList<>(2);
		//密码登录realm
		realms.add(userRealm());
		//免密登录realm
		realms.add(freeRealm());

		// 设置realm.
		securityManager.setRealms(realms);
		
		// 记住我
        securityManager.setRememberMeManager(rememberMeManager());

        securityManager.setCacheManager(getcacheManager());

        securityManager.setSessionManager(sessionManager());
		
		//securityManager.setRememberMeManager(null); //取消记住我
		return securityManager;
	}

	/**
	 * 系统自带的Realm管理，主要针对多realm
	 * */
	@Bean()
	public ModularRealmAuthenticator modularRealmAuthenticator(){
		//自己重写的ModularRealmAuthenticator
		UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
		modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		return modularRealmAuthenticator;
	}



	/**
     * cookie 属性设置
     */
    public SimpleCookie rememberMeCookie()
    {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge * 24 * 60 * 60);
        return cookie;
    }
	
	 /**
     * 记住我
     */
    public CookieRememberMeManager rememberMeManager()
    {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey(Base64.decode("fCq+/xW488hMTCD+cmJ3aQ=="));
        return cookieRememberMeManager;
    }

	// Shiro连接约束配置,即过滤链的定义
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);
		shiroFilter.setLoginUrl("/login");
		//这里的/index是后台的接口名,非页面,登录成功后要跳转的链接
		shiroFilter.setSuccessUrl("/index");
		//自定义拦截器限制并发人数,参考博客：
		LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
		//限制同一帐号同时在线的个数
		filtersMap.put("kickout", kickoutSessionControlFilter());

		shiroFilter.setFilters(filtersMap);

		// 权限认证失败，则跳转到指定页面
		shiroFilter.setUnauthorizedUrl("/");
		//授权认证配置
		Map<String, String> filterMap = new LinkedHashMap<>();
		filterMap.put("/swagger/**", "anon");
		filterMap.put("/api/trade.receive/**", "anon");
		filterMap.put("/v2/api-docs", "anon");
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/webjars/**", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/profile/fileUeditor/upload/image/**","anon");
		filterMap.put("/sys/comm/download/**","anon");
		filterMap.put("/static/**", "anon");
		filterMap.put("/login", "anon");
		filterMap.put("/auth/**", "anon");
		filterMap.put("/404.html", "anon");
		filterMap.put("/upbw/**", "anon");
		filterMap.put("/sys/login", "anon");
		filterMap.put("/favicon.ico", "anon");
		filterMap.put("/captcha.gif", "anon");
		filterMap.put("/**", "kickout,user");
		shiroFilter.setFilterChainDefinitionMap(filterMap);
		return shiroFilter;
	}

	/**
	 * 并发登录控制
	 * @return
	 */
	@Bean
	public KickoutSessionControlFilter kickoutSessionControlFilter(){
		KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
		//用于根据会话ID，获取会话进行踢出操作的；
		kickoutSessionControlFilter.setSessionManager(sessionManager());

		//是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
		kickoutSessionControlFilter.setKickoutAfter(false);

		//被踢出后重定向到的地址；
		kickoutSessionControlFilter.setKickoutUrl("/login?kickout=");
		return kickoutSessionControlFilter;
	}


	@Bean("sessionListener")
	public ShiroSessionListener sessionListener() {
		ShiroSessionListener sessionListener = new ShiroSessionListener();
		return sessionListener;
	}

	@Bean("lifecycleBeanPostProcessor")
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
		proxyCreator.setProxyTargetClass(true);
		return proxyCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor( @Qualifier("securityManager")SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	class CustomSessionIdGenerator implements SessionIdGenerator{

		@Override
		public Serializable generateId(Session session) {
			return IdUtil.fastSimpleUUID();
		}
	}
}
