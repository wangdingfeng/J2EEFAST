/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotNull;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * l_member_table
 * @author: polaris_wang
 * @date 2021-12-22 11:57:03
 */
@Getter
@Setter
@TableName("l_member_table")
public class LMemberTableEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 会员ID */
    @NotNull(message = "参数值不能为空")
    private Long memberId;

    /** 桌号ID */
    @NotNull(message = "参数值不能为空")
    private Long tableId;

    /** 开始金额 */
    private Double startAmount;

    /** 开始时长(分钟) */
    private Long startDuration;

    /** 结束金额 */
    private Double endAmount;

    /** 结束时长(分钟) */
    private Long endDuration;

    /** 删除标志（0代表存在 1代表删除） */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

    /** 状态状态（0进行中 1 已完成） */
    private String status;

}
