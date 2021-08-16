/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.core.auth.AuthService;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.log.entity.SysLoginInfoEntity;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysModuleEntity;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.sys.entity.SysTenantEntity;
import com.j2eefast.framework.sys.factory.UserFactory;
import com.j2eefast.framework.sys.mapper.SysMenuMapper;
import com.j2eefast.framework.sys.mapper.SysModuleMapper;
import com.j2eefast.framework.sys.mapper.SysTenantMapper;
import com.j2eefast.framework.sys.mapper.SysUserMapper;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.manager.AsyncManager;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.Global;
import com.j2eefast.framework.utils.RedisKeys;
import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @Description:登陆验证
 * @author zhouzhou loveingowp@163.com
 * @time 2019-04-09 15:10
 * @version V1.0 
 *
 */
@Component
public class SysLoginService implements AuthService {

	@Resource
	private RedisUtil redisUtil;

	@Resource
	private SysUserMapper sysUserMapper;

	@Resource
	private SysModuleMapper sysModuleMapper;

	@Resource
	private SysMenuMapper sysMenuMapper;

	@Resource
	private SysTenantMapper sysTenantMapper;
	/**
	 * 系统是否开启多租户模式
	 */
	@Value("#{ @environment['fast.tenantModel.enabled'] ?: false }")
	private boolean enabled;

	@Override
	public Integer loginBeforeVerify(String username, String password) {
		//获取登陆错误次数
		Integer number = redisUtil.get(RedisKeys.getUserLoginKey(username),Integer.class);

		//用户错误次数大于设定数值直接拒绝
		if( ToolUtil.isNotEmpty(number) && number >= Global.getLoginMaxCount()) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50003","账户被锁定,"+Global.getLockTime()+" 分钟后解锁!"));
			throw new RxcException(ToolUtil.message("sys.login.failedNumLock",Global.getLockTime()),"50003");
		}

		//获取验证码
		String captcha = ServletUtil.getRequest().getParameter("captcha");
		boolean verityFlag = (number != null  && number >= Global.getLoginNumCode()) || ToolUtil.isNotEmpty(captcha);

		//如果错误次数大于设定次数必须验证 验证码
		if(verityFlag){
			//获取系统验证码
			String sysCaptcha = UserUtils.getKaptcha(Constant.KAPTCHA_SESSION_KEY);
			if (ToolUtil.isEmpty(captcha) || !captcha.equalsIgnoreCase(sysCaptcha)) {
				throw new RxcException(ToolUtil.message("sys.login.code.error"),"50004");
			}
		}

		//租户是否开启
		if(enabled){
			String tenantId = ServletUtil.getRequest().getParameter("tenantId");
			if(ToolUtil.isEmpty(tenantId)){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
			SysTenantEntity sysTenantEntity = sysTenantMapper.findSysTenantByIdTenantId(tenantId);
			if(ToolUtil.isEmpty(sysTenantEntity)){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
			if(sysTenantEntity.getStatus().equals("1")){
				throw new RxcException(ToolUtil.message("sys.login.tenant.error"),"50004");
			}
		}

		//用户名或者密码为空
		if(StrUtil.isBlankOrUndefined(username) || StrUtil.isBlankOrUndefined(password) ) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50005","账号或密码错误,请重试."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"50005");
		}

		return number;
	}

	/**
	 * 验证登陆
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public LoginUserEntity loginVerify(String username, String password) {


		Integer number = this.loginBeforeVerify(username,password);

		//获取请求租户号
		String tenantId = StrUtil.EMPTY;
		if(enabled){
			tenantId  = StrUtil.blankToDefault(ServletUtil.getRequest()
				.getParameter(Constant.TENANT_PARAMETER),StrUtil.EMPTY);
		}

		SysUserEntity user = this.sysUserMapper.findUserByUserName(username,
				tenantId);

		//手机号码登陆
		if(ToolUtil.isEmpty(user) && ReUtil.isMatch(Constant.MOBILE_PHONE_NUMBER_PATTERN, username)) {
			user = this.sysUserMapper.findUserByMobile(username,tenantId);
		}

		//邮箱登陆
		if(ToolUtil.isEmpty(user) && ReUtil.isMatch(Constant.EMAIL_PATTERN,username)) {
			user = this.sysUserMapper.findUserByEmail(username,tenantId);
		}

		if(ToolUtil.isEmpty(user)){
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,-1L,-1L, "50001","账号或密码错误,请重试."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"50001");
		}
		
		//user status : 1 disable
		if ("1".equals(user.getStatus())) {
			throw new RxcException(ToolUtil.message("sys.login.accountDisabled"),"50001");
		}

		//多租户模式判断租户是否正常


		//判断密码是否正确
		if(!UserUtils.sha256(password, user.getSalt()).equals(user.getPassword())) {
			if(number == null) {
				number = 1;
				redisUtil.set(RedisKeys.getUserLoginKey(user.getUsername()), number, RedisUtil.MINUTE * Global.getLockTime());
			}else {
				number++;
				redisUtil.set(RedisKeys.getUserLoginKey(user.getUsername()), number, RedisUtil.MINUTE * Global.getLockTime());
			}
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,user.getCompId(),user.getDeptId(), "50004","账号或密码不正确,输入错误"+number+" 次!"));
			//错误次数大于设定
			if(number >= Global.getLoginNumCode()) {
				throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50004");
			}
			throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50005");
		}

		//清空错误登陆标志
		redisUtil.delete(RedisKeys.getUserLoginKey(user.getUsername()));

		//转换成用户登录信息
		LoginUserEntity loginUser = UserFactory.createLoginUser(user);

		//设置授权
		this.authorization(loginUser,user.getId());

		//设置登陆
		this.setLoginDetails(loginUser,user.getId(),"sys");

		return loginUser;
	}

	/**
	 * 免密授权登录
	 * @param openId
	 * @return
	 */
	@Override
	public LoginUserEntity freeLoginVerify(String openId) {
		//获取请求租户号
		String tenantId = StrUtil.EMPTY;
		if(enabled){
			tenantId  = StrUtil.blankToDefault(ServletUtil.getRequest()
					.getParameter(Constant.TENANT_PARAMETER),StrUtil.EMPTY);
		}
		//检查第三方账号是否有绑定用户ID
		SysUserEntity user = this.sysUserMapper.findUserByUserName(openId,tenantId);

		if(ToolUtil.isEmpty(user)){
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(openId,-1L,-1L, "60001","第三方授权登录,系统没有绑定用户."));
			throw new RxcException(ToolUtil.message("sys.login.failure"),"60001");
		}

		//获取登陆错误次数
		Integer number = redisUtil.get(RedisKeys.getUserLoginKey(user.getUsername()),Integer.class);

		//用户错误次数大于设定数值直接拒绝
		if( ToolUtil.isNotEmpty(number) && number >= Global.getLoginMaxCount()) {
			AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getUsername(),-1L,-1L, "50003","账户被锁定,"+Global.getLockTime()+" 分钟后解锁!"));
			throw new RxcException(ToolUtil.message("sys.login.failedNumLock",Global.getLockTime()),"50003");
		}

		//清空错误登陆标志
		redisUtil.delete(RedisKeys.getUserLoginKey(user.getUsername()));

		//转换成用户登录信息
		LoginUserEntity loginUser = UserFactory.createLoginUser(user);

		//设置授权
		this.authorization(loginUser,user.getId());


		//设置登陆
		this.setLoginDetails(loginUser,user.getId(),user.getSource());

		return loginUser;
	}


	@Override
	public List<String> findPermissionsByRoleId(Long roleId) {
		return this.sysMenuMapper.findPermsByRoleId(roleId);
	}

	@Override
	public void authorization(LoginUserEntity loginUser, Long userId) {

		if(!userId.equals(Constant.SUPER_ADMIN)){
			//获取用户角色列表
			List<Long> roleList = ConstantFactory.me().getRoleIds(userId);
			List<String> roleNameList = new ArrayList<>();
			List<String> roleKeyList = new ArrayList<>();

			//根居角色ID获取模块列表
			List<SysModuleEntity> modules = this.sysModuleMapper.findModuleByRoleIds(roleList);
			List<Map<String, Object>>  results = new ArrayList<>(modules.size());
			modules.forEach(module->{
				Map<String, Object> map = BeanUtil.beanToMap(module);
				results.add(map);
			});
			loginUser.setModules(results);
			//设置权限列表
			Set<String> permissionSet = new HashSet<>();
			List<Map<Object,Object>> xzz = new ArrayList<>(roleList.size());

			for (Long roleId : roleList) {
				SysRoleEntity role = ConstantFactory.me().getRoleById(roleId);
				List<String> permissions = this.findPermissionsByRoleId(roleId);
				if (permissions != null) {
					Map<Object, Object> map = new HashMap<>();
					Set<String> tempSet = new HashSet<>();
					for (String permission : permissions) {
						if (ToolUtil.isNotEmpty(permission)) {
							String[] perm = StrUtil.splitToArray(permission,",");
							for(String s: perm){
								permissionSet.add(s);
								tempSet.add(s);
							}
						}
					}
					map.put(role,tempSet);
					xzz.add(map);
				}
				roleNameList.add(role.getRoleName());
				roleKeyList.add(role.getRoleKey());
			}

			loginUser.setRoleList(roleList);
			loginUser.setRoleNames(roleNameList);
			loginUser.setRoleKey(roleKeyList);
			loginUser.setRolePerm(xzz);
			loginUser.setPermissions(permissionSet);
		}else{
			//根居角色ID获取模块列表
			List<SysModuleEntity> modules = this.sysModuleMapper.findModules();
			List<Map<String, Object>>  results = new ArrayList<>(modules.size());
			modules.forEach(module->{
				Map<String, Object> map = BeanUtil.beanToMap(module);
				results.add(map);
			});
			loginUser.setModules(results);
			List<String> roleNameList = new ArrayList<>();
			roleNameList.add("超级管理员");
			loginUser.setRoleNames(roleNameList);
			List<String> roleKeyList = new ArrayList<>();
			roleKeyList.add("ADMIN");
			loginUser.setRoleKey(roleKeyList);
			//设置权限列表
			Set<String> permissionSet = new HashSet<>();
			permissionSet.add("*:*:*");
			loginUser.setPermissions(permissionSet);
//			loginUser.setDataScope("1");
		}

		//多租户模式设置
		if(enabled){
			//如果是最大管理员租户且为ADMIN则多租户显示标志为true
			if(loginUser.getTenantId().equals(Constant.SUPER_TENANT)
					&& loginUser.getRoleKey().contains(Constant.SU_ADMIN)){
				loginUser.setSuperTenant(true);
			}
		}else{
			loginUser.setSuperTenant(false);
		}
	}

	@Override
	public void setLoginDetails(LoginUserEntity loginUser, Long userId, String source) {

		SysLoginInfoEntity loginInfo = ConstantFactory.me().getFirstLoginInfo(loginUser.getUsername());

		//上次登陆地点
		loginUser.setLoginLocation(loginInfo.getLoginLocation());
		//上次登陆时间
		loginUser.setLoginTime(loginInfo.getLoginTime());

		//登陆IP
		loginUser.setLoginIp(ServletUtil.getIp());

		//设置登陆时间
		loginUser.setNowLoginTime(DateUtil.date());

		//插入登陆表
		AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getUsername(),
				loginUser.getCompId(),loginUser.getDeptId(), "00000","登陆成功!",
				loginUser.getNowLoginTime(),
				source));

	}
}
