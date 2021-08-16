/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.flowable.bpm.entity.BpmTaskEntity;
import org.apache.ibatis.annotations.Param;

public interface BpmTaskMapper extends BaseMapper<BpmTaskEntity> {

	/**
	 * 查询待办任务
	 * @param params
	 * @param userId
	 * @param sql_filter
	 * @return
	 */
	@InterceptorIgnore(tenantLine = "true")
	Page<BpmTaskEntity> findApplyingTasksPage(IPage<?> params,
											  @Param("userId") String userId,
											  @Param("tenantId") String tenantId,
											  @Param("sql_filter") String sql_filter);


	/**
	 * 多租户模式 忽略自行处理
	 * 查询已办任务
	 * @param params
	 * @param userId
	 * @param sql_filter
	 * @return
	 */
	@InterceptorIgnore(tenantLine = "true")
	Page<BpmTaskEntity> findApplyedTasksPage(IPage<?> params,
											  @Param("userId") String userId,
											  @Param("tenantId") String tenantId,
											  @Param("sql_filter") String sql_filter);
}
