/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.framework.quartz.entity.SysJobLogEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 定时任务日志 Mapper 接口
 * @author zhouzhou
 * @date 2020-03-08 15:36
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLogEntity>{
	
	/**
	 * 清空定时任务日志表
	 */
	int cleanJobLog();


	/**
	 * 查询分页
	 * @param page
	 * @param sysJobLog
	 * @return
	 */
	Page<SysJobLogEntity> findPage(IPage<SysJobLogEntity> page,
								   @Param("sysJobLog") SysJobLogEntity sysJobLog);

}
