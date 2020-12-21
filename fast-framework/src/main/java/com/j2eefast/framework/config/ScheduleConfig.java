/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import java.util.Properties;
import javax.sql.DataSource;
import com.j2eefast.common.db.utils.DbUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时任务配置
 */
@Configuration
public class ScheduleConfig {

	@Value("${spring.datasource.master.url}")
	private String jdbcUrl;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setDataSource(dataSource);

		// quartz参数
		Properties prop = new Properties();
		prop.put("org.quartz.scheduler.instanceName", "FastScheduler");
		prop.put("org.quartz.scheduler.instanceId", "AUTO");
		// 线程池配置
		prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		prop.put("org.quartz.threadPool.threadCount", "20");
		prop.put("org.quartz.threadPool.threadPriority", "5");
		// JobStore配置
		prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
		// 集群配置
		prop.put("org.quartz.jobStore.isClustered", "true");
		prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
		prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");

		prop.put("org.quartz.jobStore.misfireThreshold", "12000");
		prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
		prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");

		// PostgreSQL数据库，需要打开此注释
		if(DbUtil.getDbType(jdbcUrl).equals("postgresql")){
			prop.put("org.quartz.jobStore.driverDelegateClass",
			 "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		}
		//MSSQL 数据库驱动代理
		if(DbUtil.getDbType(jdbcUrl).equals("sqlserver")){
			prop.put("org.quartz.jobStore.driverDelegateClass",
					"org.quartz.impl.jdbcjobstore.MSSQLDelegate");
		}

		factory.setQuartzProperties(prop);

		factory.setSchedulerName("FastScheduler");
		// 延时启动
		factory.setStartupDelay(5);
		factory.setApplicationContextSchedulerContextKey("applicationContextKey");
		// 可选，QuartzScheduler
		// 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
		factory.setOverwriteExistingJobs(true);
		// 设置自动启动，默认为true
		factory.setAutoStartup(true);

		return factory;
	}
}
