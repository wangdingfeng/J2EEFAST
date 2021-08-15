/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * sys_tenant
 * @author: ZhouZhou
 * @date 2021-07-23 13:39:18
 */
@Getter
@Setter
@TableName("sys_tenant")
public class SysTenantEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 租户ID */
    @NotBlank(message = "参数值不能为空")
    private String tenantId;

    /** 租户名称 */
    @NotBlank(message = "参数值不能为空")
    private String tenantName;

    /** 联系人 */
    private String linkman;

    /** 联系电话 */
    private String link;

    /** 联系地址 */
    private String address;

    /** 状态 */
    private String status;

    /** 是否已删除 */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

}
