/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.auth;


import com.j2eefast.common.core.base.entity.LoginUserEntity;

import java.util.List;

/**
 * <p>授权数据库操作</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-01 15:08
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
public interface AuthService {


	/**
	 * 登陆前校验
	 * @param username
	 * @param password
	 * @return
	 */
	Integer loginBeforeVerify(String username, String password);

	/**
	 * 登陆校验账号密码
	 * @param username
	 * @param password
	 * @return
	 */
	LoginUserEntity loginVerify(String username, String password);

	/**
	 * 免密授权登录
	 * @param openId
	 * @return
	 */
	LoginUserEntity freeLoginVerify(String openId);

	/**
	 * 获取权限列表通过角色id
	 * @param roleId 角色id
	 * @return
	 */
	List<String> findPermissionsByRoleId(Long roleId);


	/**
	 * 获取相应权限
	 * @param loginUser
	 * @param userId
	 * @return
	 */
	void authorization(LoginUserEntity loginUser, Long userId);


	/**
	 * 设置登陆详情
	 * @param loginUser
	 * @param userId
	 */
	void setLoginDetails(LoginUserEntity loginUser, Long userId, String source);

}
