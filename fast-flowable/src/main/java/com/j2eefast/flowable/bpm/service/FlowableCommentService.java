/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.service;

import com.j2eefast.flowable.bpm.entity.CommentEntity;

import java.util.List;

/**
 * 审批意见
 */
public interface FlowableCommentService {

	/**
	 * 添加备注
	 * @param comment 参数
	 */
	void addComment(CommentEntity comment) ;

	/**
	 * 通过流程实例id获取审批意见列表
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	List<CommentEntity> getFlowCommentVosByProcessInstanceId(String processInstanceId) ;

	String findTaskInstId(String ProcessInstanceId);
}
