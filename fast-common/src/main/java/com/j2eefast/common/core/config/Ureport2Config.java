package com.j2eefast.common.core.config;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.j2eefast.common.db.context.DataSourceContext;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import com.bstek.ureport.console.UReportServlet;
import java.sql.Connection;

/**
 * 集成ureport2 报表功能
 * @author huanzhou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "fast.ureport", name = "enabled", havingValue="true")
@ImportResource("classpath:ureport-console-context.xml")
public class Ureport2Config {

    @Bean
    public ServletRegistrationBean initUReport() {
        return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
    }


    /**
     * 配置数据库
     * @return
     */
    @Bean
    public BuildinDatasource getBuildinDatasource(){
        return new BuildinDatasource(){

            @Override
            public String name() {
                return DataSourceContext.MASTER_DATASOURCE_NAME;
            }

            @SneakyThrows
            @Override
            public Connection getConnection() {
                return DataSourceContext.getDataSources().get(DataSourceContext.MASTER_DATASOURCE_NAME).getConnection();
            }
        };
    }
}