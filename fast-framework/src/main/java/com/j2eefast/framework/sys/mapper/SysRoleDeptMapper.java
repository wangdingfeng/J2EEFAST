/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.j2eefast.framework.sys.entity.SysRoleDeptEntity;

import java.util.List;

public interface SysRoleDeptMapper extends BaseMapper<SysRoleDeptEntity> {

	List<Long> findDeptIdList(Long[] roleIds);

}
