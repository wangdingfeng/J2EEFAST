/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * l_member
 * @author: polaris_wang
 * @date 2021-12-21 14:31:55
 */
@Getter
@Setter
@TableName("l_member")
public class LMemberEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
	@TableId(type = IdType.AUTO)
    private Long id;

    /** 名称 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 性别 */
    private String gender;

    /** 邮件 */
    private String email;

    /** 金额 */
    private Double amount;

    /** 时长(分钟) */
    private Long duration;

    /** 删除标志（0代表存在 1代表删除） */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

    /** 状态状态（0正常 1停用） */
    private String status;

    /** 租户ID */
    private String tenantId;

}
