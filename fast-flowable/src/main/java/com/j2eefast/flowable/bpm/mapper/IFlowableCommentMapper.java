/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.j2eefast.flowable.bpm.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IFlowableCommentMapper {
	/**
	 * 通过流程实例id获取审批意见列表
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@InterceptorIgnore(tenantLine = "true")
	List<CommentEntity> getFlowCommentVosByProcessInstanceId(String processInstanceId);


	String findTaskInstId(String processInstanceId);
}
