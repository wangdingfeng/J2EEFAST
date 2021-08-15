/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.datasources.MybatisPulsMetaObjectHandler;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.UserUtils;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * mybatis-plus 配置
 * @author: zhouzhou Emall:loveingowp@163.com
 * @time 2020/2/14 18:32
 * @version V1.0
 * EnableAspectJAutoProxy 此处作用是使框架暴露代理对象 使其SpringUtil.getAopProxy 能够获取
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MybatisPlusConfig {

	/**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 
     * 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * @author ZhouZhou
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //-------------------------- 动态表名 数据量大的时候可以做分表处理----------------------------------//
        /*
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        // map的key就是原始表名,value就是表名替换规则
        HashMap<String, TableNameHandler> map = new HashMap<String, TableNameHandler>(1) {{
            //整个函数返回的结果就是替换后的新表名，这个生成的表名的规则可以自己随便指定
            put("sys_comp", (sql, tableName, obj) -> {
            	return tableName + "_" + DateUtil.thisYear();
            });
        }};

        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        */
        //------------------------------------------------------------------------------------------//
        
		//多租户模式
		if((Boolean) PropertiesUtils.getInstance().get(ConfigConstant.TENANTMODEL)){
			interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new SysTenantHanler()));
		}

        PaginationInnerInterceptor innerInterceptor =  new PaginationInnerInterceptor();
        //设置最大分页数量
        innerInterceptor.setMaxLimit(200L);
        //对于单一数据库类型来说,都建议配置该值,避免每次分页都去抓取数据库类型
        //innerInterceptor.setDbType(DbType.MYSQL);
        interceptor.addInnerInterceptor(innerInterceptor);

        //控制是否在count时对sql的join进行优化
		//innerInterceptor.setOptimizeJoin(false);

		//防止全表更新与删除插件: BlockAttackInnerInterceptor
		BlockAttackInnerInterceptor blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
		interceptor.addInnerInterceptor(blockAttackInnerInterceptor);

        return interceptor;
    }

    /**
     * 自动填充
     * @author ZhouZhou
     * @date 2020-12-23 17:20
     * @return
     */
	@Bean
	public MybatisPulsMetaObjectHandler gunsMpFieldHandler() {
		return new MybatisPulsMetaObjectHandler() {

			@Override
			protected String getUserUniqueId() {
				try {
					return UserUtils.getLoginName();
				} catch (Exception e) {
					//如果获取不到当前用户就存空id
					return "/";
				}
			}
		};
	}

	/**
	 * 动态配置 mapper 的扫描路径
	 * @author: zhouzhou Emall:loveingowp@163.com
	 */
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(){
		MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
		String mapperPackage = PropertiesUtils.getInstance().getProperty("mybatis-plus.mapper-package","com.j2eefast.*.*.mapper");
		if(ToolUtil.isNotEmpty(mapperPackage)){
			scannerConfigurer.setBasePackage(mapperPackage);
		}else{
			scannerConfigurer.setBasePackage("com.j2eefast.*.*.mapper");
		}
		return scannerConfigurer;
	}
}
