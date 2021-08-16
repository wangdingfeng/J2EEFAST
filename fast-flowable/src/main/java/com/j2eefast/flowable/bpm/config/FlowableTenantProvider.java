/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.config;

import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.common.tenant.TenantProvider;
import org.springframework.stereotype.Component;

/**
 * 工作流多租户插件
 * @author huanzhou
 * @date 2021-05-06 20:44:44
 */
@Slf4j
@Component
public class FlowableTenantProvider implements TenantProvider {

    @Override
    public String getTenantId() {
        if((Boolean) PropertiesUtils.getInstance().get(ConfigConstant.TENANTMODEL)){
            try{
                //获取系统租户号
                if(UserUtils.isLogin()){
                    return UserUtils.getTenantId();
                }

                String tenantId = ServletUtil.getRequest().getParameter("tenantId");
                if(ToolUtil.isNotEmpty(tenantId)){
                    return  tenantId;
                }

                tenantId = ServletUtil.getRequest().getHeader("tenantId");
                if(ToolUtil.isNotEmpty(tenantId)){
                    return  tenantId;
                }
            }catch (Exception e){
                log.error("异常",e);
            }
        }
        return null;
    }
}
