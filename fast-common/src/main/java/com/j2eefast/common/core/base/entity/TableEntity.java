/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.base.entity;

import lombok.Data;

/**
 * 表组件前端Table渲染对象
 * @author huanzhou
 *
 */
@Data
public class TableEntity {

	/**
	 * 表格字段标签
	 */
	private String field;
	
	/**
	 * 列名称
	 */
	private String title;
	
	/**
	 * 是否对应字典信息
	 */
	private String dict;
	
	/**
	 * 是否查询
	 */
	private boolean query;
	
}
