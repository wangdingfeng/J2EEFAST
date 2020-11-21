/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.config.entity;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>系统配置参数</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-01 15:54
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Getter
@Setter
@TableName("sys_config")
public class SysConfigEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     *参数Key
     */
    @NotBlank(message = "参数名不能为空")
    private String paramKey;

    /**
     * 参数值
     */
    @NotBlank(message = "参数值不能为空")
    private String paramValue;

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空")
    private String paramName;

    /**
     * 系统内置(Y 是 N不是)
     */
    @NotBlank(message = "系统默认值不能为空")
    private String configType;

    /**
     * 逻辑删除 是否删除 0 否 1 删除
     */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

}
