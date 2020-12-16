/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IHisFlowableActinstMapper {
	/**
	 * 删除节点信息
	 * @param ids ids
	 */
	void deleteHisActinstsByIds(List<String> ids) ;


	/**
	 * 通过用户获取ActivityId
	 * @param userId
	 * @param processInstanceId
	 * @return
	 */
	String getActivityId(@Param("userId") String userId,
						 @Param("processInstanceId") String processInstanceId);
}
