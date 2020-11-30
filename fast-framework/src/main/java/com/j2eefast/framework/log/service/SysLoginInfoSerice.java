/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.log.service;

import java.util.Arrays;
import java.util.Map;
import com.j2eefast.framework.annotation.DataFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.framework.log.entity.SysLoginInfoEntity;
import com.j2eefast.framework.log.mapper.SysLoginInfoMapper;
import com.j2eefast.framework.utils.Constant;

/**
 * 日志查询
 * @author huanzhou
 * @date 2020-03-24 17:40
 */
@Service
public class SysLoginInfoSerice extends ServiceImpl<SysLoginInfoMapper, SysLoginInfoEntity>{
	
	/**
	 * 页面展示查询翻页
	 * @param params 页面Page数据
	 * @return
	 */
	@DataFilter(deptAlias="d")
	public PageUtil findPage(Map<String, Object> params) {
		String username = (String) params.get("username");
		String ipaddr = (String) params.get("ipaddr");
		String status =  (String) params.get("status");
		String beginTime = (String) params.get("beginTime");
		String endTime = (String) params.get("endTime");
		String deptId = (String) params.get("deptId");
		Page<SysLoginInfoEntity> page = this.baseMapper.findPage(new Query<SysLoginInfoEntity>(params).getPage(),
																 username,
																 ipaddr,
																 status,
																 beginTime,
															     endTime,
											                     deptId,
											                     (String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}
	
	/**
	 *  根居主键批量删除
	 * @param logIds
	 * @author zhouzhou
	 * @date 2020-03-08 20:45
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] logIds) {
		return this.removeByIds(Arrays.asList(logIds));
	}

	/**
	 * 查询用户最近一条登录日志信息
	 * @param username 用户账号
	 * @return 登录日志信息
	 * @author ZhouZhou
	 * @date 2020-03-08 17:36
	 */
	public SysLoginInfoEntity queryLoginByName(String username) {
		return this.getOne(new QueryWrapper<SysLoginInfoEntity>().eq("username", username)
				.eq("status", "00000").groupBy("login_time"));
	}
	
	/**
	 * 清空日志表
	 * @author ZhouZhou
	 * @date 2020-03-08 20:37
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean cleanLog() {
		 this.baseMapper.cleanLog();
		 return true;
	}
}
