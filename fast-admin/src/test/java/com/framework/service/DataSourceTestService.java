package com.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.framework.annotation.DataSource;
import com.fast.framework.sys.entity.SysUserEntity;
import com.fast.framework.sys.service.SysUserService;


/**
 * 测试多数据源
 */
@Service
public class DataSourceTestService {
	@Autowired
	private SysUserService sysUserService;

	public SysUserEntity queryUser(Long userId) {
		return sysUserService.getById(userId);
	}

	@DataSource(name = "db1")
	public SysUserEntity queryUser2(Long userId) {
		return sysUserService.getById(userId);
	}
}