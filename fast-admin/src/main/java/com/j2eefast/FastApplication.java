/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast;

import com.bstek.ureport.console.UReportServlet;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.framework.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import javax.servlet.Servlet;

/**
 * @Description:项目启动入口
 * @author zhouzhou loveingowp@163.com
 * @date 2018-11-14 23:28
 */
@EnableCaching
@SpringBootApplication (exclude = {
		//SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class})
public class FastApplication extends SpringBootServletInitializer {

	static Logger log = LoggerFactory.getLogger(FastApplication.class);
	
	public static void main(String[] args) {

		try {
			//启动配置文件扫描
			SpringApplication app = new SpringApplication(FastApplication.class);
			app.setDefaultProperties(PropertiesUtils.getInstance().getProperties());
			app.run(args);
			//启动成功打印
			if(!PropertiesUtils.getInstance().
					getProperty(Constant.BANNER).equals(Constant.FALSE)){
				System.out.println("-------------------------------------------------------------------\n"
						+ "//             ┏┓   ┏┓					//\n"
						+ "//            ┏┛┻━━━┛┻┓                  //\n"
						+ "//            ┃   ☃   ┃				//\n"
						+ "//            ┃ ┳┛ ┗┳ ┃                  //\n"
						+ "//            ┃   ┻   ┃                  //\n"
						+ "//            ┗━┓   ┏━┛                  //\n"
						+ "//              ┃   ┗━━━┓				//\n"
						+ "//              ┃神兽保佑┣┓            //\n"
						+ "//              ┃启动成功!┏┛				//\n"
						+ "//              ┗┓┓┏━┳┓┏┛				//\n"
						+ "//               ┃┫┫  ┃┫┫				//\n"
						+ "//               ┗┻┛  ┗┻┛				//\n"
						+ "-------------------------------------------------------------------");
			}
		}catch (Exception e) {
			log.error("项目启动异常:",e);
		}
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		this.setRegisterErrorPageFilter(false);
		application.properties(PropertiesUtils.getInstance().getProperties());
		return application.sources(FastApplication.class);
	}

}
