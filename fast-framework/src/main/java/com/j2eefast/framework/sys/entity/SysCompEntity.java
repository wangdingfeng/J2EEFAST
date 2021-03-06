/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import java.util.List;
import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j2eefast.common.core.base.entity.BaseEntity;
import com.j2eefast.common.core.base.entity.annotaion.JsonListFiledIgnore;
import lombok.Data;

/**
 * 公司表
 * @author zhouzhou loveingowp@163.com
 * @time 2018-12-04 22:16
 */
@Data
@TableName("sys_comp")
public class SysCompEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 公司ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 上级公司ID，一级公司为0
	 */
	private Long parentId;

	/**
	 * 上级部门名称
	 */
	@TableField(exist = false)
	private String parentName;

	/**
	 * 所有父级节点ID集合
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String parentIds;

	/**
	 * 公司所属地区ID集合
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String  areaIds;

	/**
	 * 租户号
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String tenantId;

	/**
	 *排序
	 */
	private Integer orderNum;

	/**
	 * 公司名称
	 */
	@NotBlank(message = "公司名称不能为空")
	private String name;

	/**
	 * 公司全称
	 */
	private String fullName;

	/**
	 * 0：公司 ，  1：部门，
	 */
	@NotBlank(message = "类型不能为空")
	private String type;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 编码代号
	 */
	private String code;

	/**
	 * 是否假删除-改变标志
	 */
	@TableLogic
	@TableField(value = "del_flag", fill = FieldFill.INSERT)
	private String delFlag;

	/**
	 * ztree属性
	 */
	@TableField(exist = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Boolean open;
	@TableField(exist = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<?> list;

	/**
	 * 是否有子元素
	 */
	@TableField(exist = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Boolean isParent;

	/**
	 * 地区名称
	 */
	@TableField(exist = false)
	private String deptName;

	/**
	 * 关联地区List
	 */
	@TableField(exist = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<Long> deptIdList;

}
