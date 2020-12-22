/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.entity;

import lombok.Data;

import java.util.Map;

/**
 * <p>完整审批</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-26 10:21
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Data
public class CompleteTaskEntity extends BaseProcessEntity{
	/**
	 * 任务参数
	 */
	private Map<String, Object> variables;
}
