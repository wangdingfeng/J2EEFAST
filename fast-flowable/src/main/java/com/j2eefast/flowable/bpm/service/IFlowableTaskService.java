/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.service;

import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.flowable.bpm.entity.CompleteTaskEntity;
import com.j2eefast.flowable.bpm.entity.FlowNodeEntity;

import java.util.List;
import java.util.Map;

/**
 * 运行时任务
 */
public interface IFlowableTaskService {

	/**
	 * 查询待办任务
	 * @param params
	 * @return
	 */
	PageUtil findApplyingTasksPage(Map<String, Object> params);


	/**
	 * 查询已办任务
	 * @param params
	 * @return
	 */
	PageUtil findApplyedTasksPage(Map<String, Object> params);

	/**
	 * 执行任务
	 *
	 * @param params 参数
	 */
	ResponseData complete(CompleteTaskEntity params);


	/**
	 * 获取可驳回节点列表
	 * @param processInstanceId
	 * @param taskId
	 * @return
	 */
	List<FlowNodeEntity> getBackNodesByProcessInstanceId(String processInstanceId, String taskId);

	/**
	 * 判断任务是否挂起
	 * @param processInstanceId
	 * @return
	 */
	boolean isSuspended(String processInstanceId);
}
