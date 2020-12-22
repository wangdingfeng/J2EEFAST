/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.base.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>数据权限控制字段</p>
 *
 * @author: zhouzhou
 * @date: 2020-06-05 13:05
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Data
public class DataEntity implements Serializable {

	private static final long 					serialVersionUID 					= 1L;

	private String deptId;
	private String tenantId;
}
