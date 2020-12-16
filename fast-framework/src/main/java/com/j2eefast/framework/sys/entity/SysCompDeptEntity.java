/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @Description:公司与地区对应关系
 * @author zhouzhou loveingowp@163.com
 * @time 2018-12-05 15:46
 *
 */
@Data
@TableName("sys_comp_dept")
public class SysCompDeptEntity implements Serializable {

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	/**
	 * 公司ID
	 */
	private Long compId;
	/**
	 * 地区ID
	 */
	private Long deptId;
}
