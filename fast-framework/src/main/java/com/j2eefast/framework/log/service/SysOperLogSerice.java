/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.framework.annotation.DataFilter;
import com.j2eefast.framework.log.entity.SysOperLogEntity;
import com.j2eefast.framework.log.mapper.SysOperLogMapper;
import com.j2eefast.framework.utils.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

/**
 * 操作日志查询
 * @author huanzhou
 * @date 2019-03-20 16:46
 */
@Service
public class SysOperLogSerice extends ServiceImpl<SysOperLogMapper, SysOperLogEntity> {

	/**
	 * 页面展示查询翻页
	 * @author ZhouZhou
	 * @date 2019-03-20 17:46
	 * @param params 页面参数
	 * @return
	 */
	@DataFilter(deptAlias="d")
	public PageUtil findPage(Map<String, Object> params) {
		String title = (String) params.get("title");
		String pabusinessTypes = (String)params.get("businessTypes");
		String status = (String) params.get("status");
		String operName = (String) params.get("operName");
		String beginTime = (String) params.get("beginTime");
		String endTime = (String) params.get("endTime");
		String deptId = (String) params.get("deptId");
		Page<SysOperLogEntity> page = this.baseMapper.findPage(new Query<SysOperLogEntity>(params).getPage(),
				title,operName,status,beginTime,endTime,pabusinessTypes,deptId,(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	 * 清空日志表
	 * @author zhouzhou
	 * @date 2020-03-08 20:37
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean cleanLog() {
		this.baseMapper.cleanLog();
		return true;
	}
}
