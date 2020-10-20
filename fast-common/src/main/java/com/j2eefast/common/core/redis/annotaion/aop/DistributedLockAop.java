package com.j2eefast.common.core.redis.annotaion.aop;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.ReflectUtils;
import com.j2eefast.common.core.utils.ToolUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DistributedLockAop
 * @Description
 * @author mfksn001@163.com
 * @date 2020年9月9日
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAop {

	@Autowired
	private RedissonClient redisson;

	// 这里需要修改对应的包名
	@Pointcut("@annotation(com.j2eefast.common.core.redis.annotaion.aop.DistributedLock)")
	public void RlockAspect() {
	}

	@Around("RlockAspect()")
	public Object arround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object object = null;
		RLock lock = null;
		try {
			MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
			DistributedLock rlockInfo = methodSignature.getMethod().getAnnotation(DistributedLock.class);
			String lockKey = getLocalKey(proceedingJoinPoint, rlockInfo);
			lock = redisson.getLock(lockKey);
			if (lock != null) {
			    final boolean status = lock.tryLock(rlockInfo.waitTime(),rlockInfo.releaseTime(), rlockInfo.timeUnit());
				if (status) {
					object = proceedingJoinPoint.proceed();
				}else {
					log.error("lock failed...", lockKey);
					throw new RxcException(ToolUtil.message("the data is locked. Please try again later..."));
				}
			} else {
				log.error("can't get lock：{}", lockKey);
				throw new RxcException(ToolUtil.message("the data is locked. Please try again later..."));
			}
		}catch (Exception e) {
			throw new RxcException(ToolUtil.message("the data is locked. Please try again later..."));
		} finally {
			if (lock != null && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		return object;
	}
	
	

	/**
	* @Title: getLocalKey 
	* @Description: 获取Lock Name, 参数名+id的方式作为锁名
	* @param proceedingJoinPoint
	* @param rlockInfo
	* @return  String 
	* @author mfksn001@163.com
	* @Date: 2020年9月9日
	 */
	public String getLocalKey(ProceedingJoinPoint proceedingJoinPoint, DistributedLock rlockInfo) {
		String businessNo = "";		
		if (StringUtils.isNoneBlank(rlockInfo.value())) {   //自定义lock name  用在方法上，表示该方法在锁定时间内,缺省20秒，其它线程不能使用
			businessNo = rlockInfo.value();
		}else {
			final Object[] args = proceedingJoinPoint.getArgs();
			MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
			Class<?>[] parameters = methodSignature.getParameterTypes();
			String[] parameterNames = methodSignature.getParameterNames();
			Method mothod = methodSignature.getMethod();
			//类名+方法名
			StringBuilder sb = new StringBuilder("Rlock_").append(mothod.getDeclaringClass().getName()).append(".").append(mothod.getName());			
			if (parameters != null) {
				String clazzAttrName = rlockInfo.classAttrName();
				String[] temp = clazzAttrName.split("\\.") ;
	          if (StringUtils.isNotBlank(clazzAttrName)) {   //空注解
	        	   String keyValue = "";
	        	   String parameterName = "" ;
	        	   String attrName = "" ;
	        	   if (temp.length == 2) {  //对像+属性名 注解
	        		   parameterName = temp[0] ;
	        	       attrName = temp[1] ;
	                }else {                //属性名注解
	            	   attrName = temp[0] ;
	               }
	        	   for (int i = 0; i < parameterNames.length; i++) {
	        		        Object obj = args[i];
	        		        Class<?> parameterClazz = parameters[i];
	        		        //非基础类型或包装类型
	        		        if (ReflectUtils.isWrapClass(parameterClazz) || parameterClazz.isPrimitive()) {  
	        		        	if (attrName.equals(parameterNames[i])) {
	        		        		keyValue = String.valueOf(obj);
	        		        		break;
	        		        	}
	        		        	continue;
	        		        }else {  //自己定义对象类型
	        		        	if (StringUtils.isNoneBlank(parameterName)) {
	        		        		if (parameterName.equals(parameterNames[i])) {
	        		        		  keyValue = String.valueOf(ReflectUtils.getFieldValue(obj, attrName));
	        		        		  break;
	        		        		}
	        		        		continue;
	        		        	}else {
	        		        		keyValue = String.valueOf(ReflectUtils.getFieldValue(obj, attrName));  
	        		        	    break;
	        		        	}
	        		        }
	        	   }
	        	   if (StringUtils.isNotBlank(keyValue)) {
	        		   sb.append(parameterName).append("_").append(keyValue);
   				   }	        	 
	          }	else {
				for (int i = 0; i < parameters.length; i++) {
					Class<?> parameter = parameters[i];
					sb.append("_").append(parameter.getSimpleName());				
				}	
	          }	
			}
			businessNo = sb.toString();			
		}
		return businessNo;
	}
	
    
	
}