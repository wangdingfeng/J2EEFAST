/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.j2eefast.common.core.config.properties.DruidProperties;
import com.j2eefast.common.core.mutidatasource.annotaion.aop.MultiSourceAop;
import com.j2eefast.common.db.context.DataSourceContext;
import com.j2eefast.common.db.factory.AtomikosFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

/**
 * <p>基于数据库多数据源配置</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-15 13:52
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Configuration
public class DataSourceConfig {

	@Value("#{ @environment['fast.jta.enabled'] ?: false }")
	private boolean enabled;

	/**
	 * 默认数据库配置
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.master")
    public DruidProperties defaultProperties() {
        return new DruidProperties();
    }


	/**
	 * 主数据源实例
	 */
	@Primary
	@Bean("dataSourcePrimary")
	public DataSource dataSourcePrimary(@Qualifier("defaultProperties") DruidProperties druidProperties) {
		if(enabled){
			return AtomikosFactory.create(DataSourceContext.MASTER_DATASOURCE_NAME, druidProperties);
		}else {
			DruidDataSource dataSource = new DruidDataSource();
			druidProperties.config(dataSource);
			return dataSource;
		}
	}


	/**
	 * flowable 数据库
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "fast.flowable", name = "enabled", havingValue = "true")
	@ConfigurationProperties(prefix = "spring.datasource.flowable")
	public DruidProperties flowableProperties() {
		return new DruidProperties();
	}


	/**
	 * flowable数据源实例
	 */
	@Bean("flowableSourcePrimary")
	@ConditionalOnProperty(prefix = "fast.flowable", name = "enabled", havingValue = "true")
	public DataSource flowableSourcePrimary(@Qualifier("flowableProperties") DruidProperties flowableProperties) {
		if(enabled){
			return AtomikosFactory.create(DataSourceContext.FLOWABLE_DATASOURCE_NAME, flowableProperties);
		}else{
			DruidDataSource dataSource = new DruidDataSource();
			flowableProperties.config(dataSource);
			return dataSource;
		}
	}


	/**
	 * 多数据源切换的aop
	 */
	@Bean
	public MultiSourceAop multiSourceExAop() {
		return new MultiSourceAop();
	}

}
