/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.constants;

import com.j2eefast.common.core.utils.HexUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统常量
 * @author zhouzhou
 * @date 2020-03-15 11:12
 */
public final class ConfigConstant {

	public static final String 					TIPS_END 							= "，若想忽略此提示，请在系统工具->参数管理，设置相关参数！";
	public static final String 					DEOM_MODE_PROMPT 					= "演示模式,禁止操作!";
	public static final String 					REQUEST_PROMPT 						= "客户端请求异常拦截!";
	public static final String					DEOM_MODE_SHOW						= "*** 演示模式，不展示数据 ***";
	public static String 						FAST_OS_SN							= "";
	public static Boolean						FAST_IPIS							= true;
	public static List<String> 					FAST_IPS							= null;
	public static byte[] 						FAST_MAC_KEY						= HexUtil.decodeHex("BF8F83A656BD75925379384E454DD174");
	public static byte[] 						FAST_KEY							= HexUtil.decodeHex("62D95F5BA8E44064231018DF8A9EE027");
	public static String 						KEY									= "D605C20574179E9F3526BC9076D77AE9";
	public static byte[] 						FAST_VERIFY_KEY						= HexUtil.decodeHex("00000000000000000000000000000000");
	public static String 						AUTHORIZATION_TIME					= "";
	public static final String 					CONSTANT 							= "CONSTANT";
	public static final String 					CTX_STATIC							= "ctxStatic";
	public static final String 					BASE_PATH							= "basePath";
	public static final String 					FAST_SESSION_ID						= "fast.session.id";
	public static final String 					JTA_ENABLED_YML 					= "fast.jta.enabled";
	public static final String 					TRUE 								= "true";
	public static final String 					FLOWABLE_ENABLED_YML 				= "fast.flowable.enabled";

	/**
	 * 登录安全key
	 */
	public static final String 					SECRETKEY 							= "_secretKey";
	public static final String 					LANGUAGE							= "_lang";

	/**
	 * 数据源容器
	 */
	public static Map<Object, Object> DATA_SOURCES = new ConcurrentHashMap<>();
	/**
	 * 资源映射路径 前缀
	 */
	public static final String 					RESOURCE_URLPREFIX 					= "/profile";
	
	
	/**********************************系统参数配置常量**********************************************************************/
	/**
	 * 云存储配置KEY
	 */
	public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
	/**
	 * API服务器
	 */
	public final static String CLOUD_CONFIG_APP = "CLOUD_CONFIG_APP";
	
	public final static String SYS_CONFIG_TITLE = "SYS_CONFIG_TITLE" ;
	
	public final static String SYS_CONFIG_ROOT_PATH ="SYS_CONFIG_ROOT_PATH" ;

	public final static String SYS_CONFIG_EDITOR_PATH =  "SYS_CONFIG_EDITOR_PATH" ;
	
	public final static String SYS_CONFIG_ATTACH_PATH = "SYS_CONFIG_ATTACH_PATH"; 
	
	public final static String SYS_CONFIG_TEMP_PATH = "SYS_CONFIG_TEMP_PATH"; 
	
	/**
	 * 登录安全认证
	 */
	public final static String SYS_LOGIN_VERIFICATION = "SYS_LOGIN_VERIFICATION";
	
	/**
	 * 登录默认页面风格
	 */
	public final static String SYS_LOGIN_DEFAULT_VIEW = "SYS_LOGIN_DEFAULT_VIEW";
	
	/**
	 * 登录验证码类型
	 */
	public final static String  SYS_LOGIN_CAPTACHA_TYPE = "SYS_LOGIN_CAPTACHA_TYPE";

	/**
	 * 输出前端页面是否压缩
	 */
	public final static String SYS_COMPRESS = "SYS_COMPRESS";

	/**
	 * 版本号
	 */
	public final static String SYS_VERSION = "version";

	public final static String CONFIG_KEY  = "sys:cfg:version";
	
	/**
	 * JS CSS版本
	 */
	public final static String SYS_CONFIG_DV_VERSION = "SYS_CONFIG_DV_VERSION";
	/**
	 * 系统参数
	 */
	
	public final static String SYS_CONFIG_KEY = "SYS_CONFIG_KEY";
	
	/**
	 * 支付参数
	 */
	public final static String PAY_CONFIG_KEY = "PAY_CONFIG_KEY";

	/**
	 * 登录排挤
	 */
	public final static String SYS_IS_LOGIN = "SYS_IS_LOGIN";

}
