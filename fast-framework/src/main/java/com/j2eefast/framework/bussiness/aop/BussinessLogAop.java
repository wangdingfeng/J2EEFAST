/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.bussiness.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.utils.JSON;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.log.entity.SysOperLogEntity;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.utils.UserUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessStatus;
import com.j2eefast.common.core.manager.AsyncManager;

/**
 * 系统日志，切面处理类AOP实现日志统一处理
 * @author zhouzhou
 * @date 2020-03-12 14:53
 */
@Aspect
@Component
public class BussinessLogAop {

	private static final Logger					LOG 					= LoggerFactory.getLogger(BussinessLogAop.class);

	// 排除敏感参数
    public static final String[] EXCLUDE_PARAMETER = { "password", "oldPassword", "newPassword", "confirmPassword" };
    
	//计算请求时间
	private static final ThreadLocal<Long>     STARTTIMETHREADLOCAL     = new NamedThreadLocal<Long>("BussinessLogAop StartTime");
	
	/**
	 * 配置织入点
	 * @author zhouzhou
	 * @date 2020-03-12 14:55
	 */
	@Pointcut("@annotation(com.j2eefast.common.core.business.annotaion.BussinessLog)")
	public void logCut() {
		
	}

	
	/**
	 * 处理前调用
	 * @author ZhouZhou
	 * @date 2021-07-31 12:56
	 * @param joinPoint
	 */
	@Before(value= "logCut()")
	public void boBefore(JoinPoint joinPoint) {
		// 1、开始时间  
		long beginTime = System.currentTimeMillis();
		
		STARTTIMETHREADLOCAL.set(beginTime);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("请求开始:{} URL:{} IP:{}", DatePattern.NORM_DATETIME_MS_FORMAT.format(new Date()),
					ServletUtil.getRequest().getRequestURI(),UserUtils.getIp());
		}
	}

	/**
	 * 处理完请求后执行
	 *
	 * @param joinPoint 切点
	 */
	@AfterReturning(pointcut = "logCut()", returning = "jsonResult")
	public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
		handleLog(joinPoint, null, jsonResult);
	}

	
	/**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e){
        handleLog(joinPoint, e, null);
    }

	protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult){
		
		try{
			
			// 获得注解
			BussinessLog controllerLog = getAnnotationLog(joinPoint);
			if (controllerLog == null){
				return;
			}
			
			// 获取当前的用户
			LoginUserEntity currentUser = UserUtils.getUserInfo();

			// *========数据库日志=========*//
			SysOperLogEntity operLog = new SysOperLogEntity();
			
			operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
			
			// 请求IP
			String ip = ServletUtil.getIp();
			operLog.setOperIp(ip);
			
			//正常返回
			if(ToolUtil.isNotEmpty(jsonResult)) {
				// 正常返回参数
				operLog.setJsonResult(StringUtils.substring(JSON.marshal(jsonResult),0,2000));
			}
			
			//请求URL
			operLog.setOperUrl(ServletUtil.getRequest().getRequestURI());
			
			//保存用户信息
			if (currentUser != null){
				//当操作用户存在 保存用户相关信息
				operLog.setOperName(currentUser.getName());
				if (ToolUtil.isNotEmpty(currentUser.getCompName())){
					operLog.setCompName(currentUser.getCompName());
				}
				operLog.setCompId(currentUser.getCompId());
				operLog.setDeptId(currentUser.getDeptId());
			}
			
			// 判断是否异常
			if (ToolUtil.isNotEmpty(e)){
				operLog.setStatus(BusinessStatus.FAIL.ordinal());
				//错误信息直接保存
				operLog.setErrorMsg(e.getMessage());
			}
			
			// 设置方法名称
			String className = joinPoint.getTarget().getClass().getName();
			String methodName = joinPoint.getSignature().getName();
			String bUseraget = ServletUtil.getRequest().getHeader("User-Agent");
			operLog.setUserAgent(HtmlUtil.filter(bUseraget));
			operLog.setMethod(className + "." + methodName + "()");
			// 设置请求方式
			operLog.setRequestMethod(ServletUtil.getRequest().getMethod());
			// 处理设置注解上的参数
			getControllerMethodDescription(joinPoint,controllerLog, operLog);
			
			// 2、结束时间
			long endTime = System.currentTimeMillis(); 
			
			// 得到线程绑定的局部变量（开始时间）
			long startTime = 0; 
			if (STARTTIMETHREADLOCAL != null){
				Long time = STARTTIMETHREADLOCAL.get();
				if (time != null){
					startTime = time;
				}
				// 用完之后销毁线程变量数据
				STARTTIMETHREADLOCAL.remove(); 
			}
			if (startTime == 0){
				operLog.setOperTime(new Date());
				// 得到 -1000 方便统计
				startTime = endTime + 1000;	
			}else {
				//请求时间
				operLog.setOperTime(DateUtil.date(startTime));
			}
			
			// 3、获取执行时间
			long executeTime = endTime - startTime;	
			operLog.setTime(executeTime);

			// 异步保存数据库
			AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
			
			//打印日志
			if(LOG.isDebugEnabled()) {
				Runtime runtime = Runtime.getRuntime();
				//总内存	
				long total = runtime.totalMemory();
				//剩余
				long free = runtime.freeMemory();
				//使用
				long used = total-free;
				
				LOG.debug("请求结束:{} 用时:{} URL:{} JVM总内存:{} JVM已使用:{} JVM剩余:{}",DatePattern.NORM_DATETIME_MS_FORMAT.format(new Date()),DateUtil.formatBetween(executeTime),
						ServletUtil.getRequest().getRequestURI(),ToolUtil.convertFileSize(total),ToolUtil.convertFileSize(used),ToolUtil.convertFileSize(free));
			}
		}catch (Exception ex){
			// 记录本地异常日志
			LOG.error("==前置通知异常==");
			LOG.error("异常信息:{}", ex.getMessage());
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 */
	public void getControllerMethodDescription(JoinPoint joinPoin,BussinessLog log, SysOperLogEntity operLog) throws Exception{
		// 设置action动作
		operLog.setBusinessType(log.businessType().ordinal());
		// 设置标题
		operLog.setTitle(log.title());
		// 设置操作人类别
		operLog.setOperatorType(log.operatorType().ordinal());
		// 是否需要保存request，参数和值
		if (log.isSaveRequestData()){
			// 获取参数的信息，传入到数据库中。
			setRequestValue(joinPoin,operLog);
		}
	}
	
	/**
     * 忽略敏感字段
     */
    public PropertyPreFilters.MySimplePropertyPreFilter excludeParameterFilter(){
        return new PropertyPreFilters().addFilter().addExcludes(EXCLUDE_PARAMETER);
    }
	
	
    /**
	 * 获取请求的参数，放到log中
	 */
	private void setRequestValue(JoinPoint joinPoint, SysOperLogEntity operLog) throws Exception{
        Map<String, String[]> map = ServletUtil.getRequest().getParameterMap();
        if (ToolUtil.isNotEmpty(map)){
            String params = JSONObject.toJSONString(map, excludeParameterFilter());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        }else{
            Object args = joinPoint.getArgs();
            if (ToolUtil.isNotEmpty(args)){
                String params = argsArrayToString(joinPoint.getArgs());
                operLog.setOperParam(StringUtils.substring(params, 0, 2000));
            }
        }
    }

	
	/**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray){
        String params = "";
        if (paramsArray != null && paramsArray.length > 0){
            for (int i = 0; i < paramsArray.length; i++){
                if (ToolUtil.isNotEmpty(paramsArray[i]) && !isFilterObject(paramsArray[i])){
                    Object jsonObj = JSONObject.toJSONString(paramsArray[i], excludeParameterFilter());
                    params += jsonObj.toString() + " ";
                }
            }
        }
        return params.trim();
    }
	
	/**
     * 判断是否需要过滤的对象。
     * 
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o){
        Class<?> clazz = o.getClass();
        if (clazz.isArray()){
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz)){
            Collection collection = (Collection) o;
            for (Iterator iter = collection.iterator(); iter.hasNext();)
            {
                return iter.next() instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz)){
            Map map = (Map) o;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();){
                Map.Entry entry = (Map.Entry) iter.next();
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }


	/**
	 * 是否存在注解，如果存在就获取
	 */
	private BussinessLog getAnnotationLog(JoinPoint joinPoint) throws Exception{
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method != null){
			return method.getAnnotation(BussinessLog.class);
		}
		return null;
	}
	

	
}
