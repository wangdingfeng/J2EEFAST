/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * example_tree
 * @author: ZhouZhou
 * @date 2020-12-21 10:58:12
 */
@Getter
@Setter
@TableName("example_tree")
public class ExampleSynchTreeEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 父级ID */
    @NotNull(message = "参数值不能为空")
    private Long parentId;

    /** 节点名称 */
    @NotBlank(message = "参数值不能为空")
    private String name;

    /** 节点全称 */
    private String fullName;

    /** 状态 */
    private String status;

    /** 删除状态 */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

    /**
     * 上级名称
     */
    @TableField(exist = false)
    private String parentName;

}
