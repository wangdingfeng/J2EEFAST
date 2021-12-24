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
 * l_table
 * @author: polaris_wang
 * @date 2021-12-21 15:12:50
 */
@Getter
@Setter
@TableName("l_table")
public class LTableEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 桌号 */
    private Integer tableNum;

    /** 桌名 */
    private String tableName;

    /** 流水ID */
    private Long memberTableId;

    /** 状态状态（0空闲 1使用中） */
    private String status;

    /** 删除标志（0代表存在 1代表删除） */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

}
