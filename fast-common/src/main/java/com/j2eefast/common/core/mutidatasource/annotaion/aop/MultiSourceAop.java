/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.mutidatasource.annotaion.aop;

import java.lang.reflect.Method;
import com.j2eefast.common.db.context.DataSourceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import com.j2eefast.common.core.mutidatasource.DataSourceContextHolder;
import com.j2eefast.common.core.mutidatasource.annotaion.DataSource;
import com.j2eefast.common.core.utils.ToolUtil;

/**
 * 多数据源切换AOP
 * 扩增 支持类上注解
 * @author zhouzhou
 * @date 2020-03-12 09:58
 */
@Slf4j
@Aspect
public class MultiSourceAop implements Ordered {

	@SuppressWarnings("unused")
	private String[]  						names					   = null;

	/**
	 * AOP的顺序要早于Spring的事务
	 */
	@Override
	public int getOrder() {
		return 1;
	}
	
	/**
     * 构造函数
     *
     * @param names 数据源的名称们，第一个为默认名称
     */
    public MultiSourceAop(String... names) {
        this.names = names;
    }

    @Pointcut(value = "@annotation(com.j2eefast.common.core.mutidatasource.annotaion.DataSource)"
				+ "|| @within(com.j2eefast.common.core.mutidatasource.annotaion.DataSource)")
    private void cut() {

    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        //支持获取类上注解
		DataSource datasource = getDataSource(point);
		//如果有DataSource注解，则设置DataSourceContextHolder为注解上的名称
        if (ToolUtil.isNotEmpty(datasource)) {
            DataSourceContextHolder.setDataSourceType(datasource.name());
            log.debug("设置数据源为：" + datasource.name());
        } else {
            DataSourceContextHolder.setDataSourceType(DataSourceContext.MASTER_DATASOURCE_NAME);
			log.debug("设置数据源为：dataSourceCurrent");
        }
        try {
            return point.proceed();
        } finally {
			log.debug("清空数据源信息！");
            DataSourceContextHolder.clearDataSourceType();
        }
    }

	/**
	 * 获取需要切换的数据源
	 * 支持类\接口\方法
	 */
	public DataSource getDataSource(ProceedingJoinPoint point){
		MethodSignature signature = (MethodSignature) point.getSignature();
		Class<? extends Object> targetClass = point.getTarget().getClass();
		//获取方法上的DataSource注解
		DataSource dataSource = targetClass.getAnnotation(DataSource.class);
		if (ToolUtil.isNotEmpty(dataSource)) {
			return dataSource;
		}
		//获取方法上的DataSource注解
		Method method = signature.getMethod();
		dataSource = method.getAnnotation(DataSource.class);
		if (ToolUtil.isNotEmpty(dataSource)) {
			return dataSource;
		}
		//获取接口注解
		targetClass = method.getDeclaringClass();
		dataSource = targetClass.getAnnotation(DataSource.class);
		if (ToolUtil.isNotEmpty(dataSource)) {
			return dataSource;
		}
		return dataSource;
	}
	
}
