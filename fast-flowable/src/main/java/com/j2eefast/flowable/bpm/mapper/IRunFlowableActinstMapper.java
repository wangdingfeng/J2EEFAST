/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IRunFlowableActinstMapper {
	/**
	 * 删除节点信息
	 * @param ids ids
	 */
	void deleteRunActinstsByIds(List<String> ids) ;
}
