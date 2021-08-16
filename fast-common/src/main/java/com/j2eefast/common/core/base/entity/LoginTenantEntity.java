package com.j2eefast.common.core.base.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录页面租户信息
 * @author huanzhou
 */
@Data
public class LoginTenantEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;
}
