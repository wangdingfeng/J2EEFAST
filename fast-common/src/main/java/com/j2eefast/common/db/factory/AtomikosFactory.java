/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.db.factory;

import com.alibaba.druid.util.JdbcConstants;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.j2eefast.common.core.config.properties.DruidProperties;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

/**
 * <p>数据源工厂</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-15 11:10
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Slf4j
public class AtomikosFactory {

	public static AtomikosDataSourceBean create(String dataSourceName, DruidProperties druidProperties) {
		AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
		//SQLServerXADataSource
		if(druidProperties.getDriverClassName().equals(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4)){
			SQLServerXADataSource datasource = new SQLServerXADataSource();
			datasource.setURL(druidProperties.getUrl());
			datasource.setUser(druidProperties.getUsername());
			datasource.setPassword(druidProperties.getPassword());
			atomikosDataSourceBean.setXaDataSource(datasource);
			atomikosDataSourceBean.setUniqueResourceName(dataSourceName);
			atomikosDataSourceBean.setMaxPoolSize(100);
			atomikosDataSourceBean.setBorrowConnectionTimeout(60);
		}else{
			atomikosDataSourceBean.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
			Properties properties = druidProperties.createProperties();
			atomikosDataSourceBean.setXaProperties(properties);
			atomikosDataSourceBean.setUniqueResourceName(dataSourceName);
			atomikosDataSourceBean.setMaxPoolSize(100);
			atomikosDataSourceBean.setBorrowConnectionTimeout(60);
			try {
				atomikosDataSourceBean.init();
			} catch (AtomikosSQLException e) {
				log.error("数据库加载失败!",e);
			}
		}
		return atomikosDataSourceBean;
	}
}
