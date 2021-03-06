/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.j2eefast.common.core.config.RabbitmqProducer;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.CheckPassWord;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.rabbit.constant.RabbitInfo;
import com.j2eefast.framework.annotation.DataFilter;
import com.j2eefast.framework.log.entity.SysLoginInfoEntity;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.sys.mapper.SysUserMapper;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.Global;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 系统用户
 * @author zhouzhou
 */
@Service
public class SysUserService  extends ServiceImpl<SysUserMapper,SysUserEntity> {

	@Resource
	private SysUserMapper sysUserMapper;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Resource
	private SysRoleService sysRoleService;
	@Autowired
	private SysUserPostService sysUserPostService;
	@Autowired
	private RabbitmqProducer rabbitmqProducer;

	/**
	 * 用户页面查询分页
	 * @param params
	 * @return
	 */
	@DataFilter(compAlias="c",deptAlias = "d",userAlias = "u")
	public PageUtil findPage(Map<String, Object> params) {
		String username = (String) params.get("username");
		String name = (String) params.get("name");
		String status = (String) params.get("status");
		String mobile = (String) params.get("mobile");
		String email = (String) params.get("email");
		String compId = (String) params.get("compId");
		String deptId = (String) params.get("deptId");
		String roleKey = (String) params.get("roleKey");
		Page<SysUserEntity> page = sysUserMapper.findPage(	new Query<SysUserEntity>(params).getPage(),
															StrUtil.nullToDefault(username,""),
															StrUtil.nullToDefault(status,""),
															StrUtil.nullToDefault(mobile,""),
															StrUtil.nullToDefault(email,""),
															StrUtil.nullToDefault(compId,""),
															StrUtil.nullToDefault(deptId,""),
															StrUtil.nullToDefault(name,""),
															StrUtil.nullToDefault(roleKey,""),
															(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	@DataFilter(compAlias="c",deptAlias = "d",userAlias = "u")
	public List<SysUserEntity> findList(Map<String, Object> params) {
		String username = (String) params.get("username");
		String status = (String) params.get("status");
		String mobile = (String) params.get("mobile");
		String email = (String) params.get("email");
		String compId = (String) params.get("compId");
		Long[] ids  = (Long[]) params.get("ids");
		return sysUserMapper.findList(StrUtil.nullToDefault(username,""),
				StrUtil.nullToDefault(status,""),
				StrUtil.nullToDefault(mobile,""),
				StrUtil.nullToDefault(email,""),
				StrUtil.nullToDefault(compId,""),
				ids,
				(String) params.get(Constant.SQL_FILTER));
	}


	/**
	 * 根居用户权限查询用户
	 * @param params
	 * @return
	 */
	@DataFilter(compAlias="c",deptAlias = "d")
	public PageUtil findUserByRolePage(Map<String, Object> params) {
		String roleId = (String) params.get("roleId");
		String username = (String) params.get("username");
		String status = (String) params.get("status");
		String mobile = (String) params.get("mobile");
		String email = (String) params.get("email");
		String compId = (String) params.get("compId");
		Page<SysUserEntity> page = sysUserMapper.findUserByRolePage(new Query<SysUserEntity>(params).getPage(),
																	StrUtil.nullToDefault(roleId,""),
																	StrUtil.nullToDefault(username,""),
																	StrUtil.nullToDefault(status,""),
																	StrUtil.nullToDefault(mobile,""),
																	StrUtil.nullToDefault(email,""),
																	StrUtil.nullToDefault(compId,""),
																	(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}


	@DataFilter(compAlias="c",deptAlias = "d")
	public PageUtil findUnallocatedList(Map<String, Object> params) {
		String roleId = (String) params.get("roleId");
		String username = (String) params.get("username");
		String mobile = (String) params.get("mobile");
		String email = (String) params.get("email");
		String compId = (String) params.get("compId");
		Page<SysUserEntity> page = sysUserMapper.findUnallocatedList(new Query<SysUserEntity>(params).getPage(),
																	StrUtil.nullToDefault(roleId,""),
																	StrUtil.nullToDefault(username,""),
																	StrUtil.nullToDefault(mobile,""),
																	StrUtil.nullToDefault(email,""),
																	StrUtil.nullToDefault(compId,""),
																	(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

//	public List<Long> findAllMenuId(Long userId) {
//		return sysUserMapper.findAllMenuId(userId);
//	}

	/**
	 * 保存
	 * @param user
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean add(SysUserEntity user) {

		//检查密码安全级别
		if(user.getPassword().equals(Global.getDbKey("sys.user.initPassword"))){
			user.setPwdSecurityLevel("0");
		}else{
			user.setPwdSecurityLevel(CheckPassWord.getPwdSecurityLevel(user.getPassword()).getValue());
		}
		// sha256加密
		String salt = UserUtils.randomSalt();
		user.setSalt(salt);
		user.setPassword(UserUtils.sha256(user.getPassword(), user.getSalt()));

		if(this.save(user)){

			// 保存用户与角色关系
			sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());

			//保存 用户与岗位 关系
			sysUserPostService.saveOrUpdate(user.getId(), user.getPostCodes());

			// 保存用户与公司地区关系
			// sysUserDeptService.saveOrUpdate(user.getId(), user.getDeptIdList());

			rabbitmqProducer.sendSimpleMessage(RabbitInfo.getAddUserHard(),JSONArray.toJSONString(user),
					IdUtil.fastSimpleUUID(),RabbitInfo.EXCHANGE_NAME, RabbitInfo.KEY);
			return true;
		}

		return false;
	}

	/**
	 * 同步用户到工作流
	 * @return
	 */
	public boolean bpmSynchronizationUser(){
		List<SysUserEntity> userList = this.list();
		userList.forEach(e->{
			e.setRoleIdList(sysUserRoleService.findRoleIdList(e.getId()));
		});
		rabbitmqProducer.sendSimpleMessage(RabbitInfo.synchronizationUser(),JSONArray.toJSONString(userList),
				IdUtil.fastSimpleUUID(),RabbitInfo.EXCHANGE_NAME, RabbitInfo.KEY);
		return true;
	}

	/**
	 * 更新
	 * @param user
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean update(SysUserEntity user) {

		if (ToolUtil.isNotEmpty(user.getPassword())) {
			String salt = UserUtils.randomSalt();
			user.setPwdSecurityLevel(CheckPassWord.getPwdSecurityLevel(user.getPassword()).getValue());
			user.setSalt(salt);
			user.setPassword(UserUtils.sha256(user.getPassword(), user.getSalt()));
		}
		if(this.updateById(user)){

			// 保存用户与角色关系
			sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());

			// 保存用户与公司地区关系
			//sysUserDeptService.saveOrUpdate(user.getId(), user.getDeptIdList());

			//岗位关联
			sysUserPostService.saveOrUpdate(user.getId(), user.getPostCodes());


			rabbitmqProducer.sendSimpleMessage(RabbitInfo.getUpdateUserHard(),JSONArray.toJSONString(user),
					IdUtil.fastSimpleUUID(),RabbitInfo.EXCHANGE_NAME, RabbitInfo.KEY);
			return true;
		}

		return false;
	}

	@Transactional(rollbackFor = Exception.class)
	public  boolean delUser(Long[] ids){
		// 删除 用户与角色 关联表
		sysUserRoleService.deleteBatchByUserIds(ids);
		// 删除 用户与地区 关联表
		//sysUserDeptService.deleteBatchByUserIds(ids);
		//删除 用户与岗位 关联表
		sysUserPostService.deleteBatchByUserIds(ids);

		//删除 用户
		if(this.removeByIds(Arrays.asList(ids))){
			rabbitmqProducer.sendSimpleMessage(RabbitInfo.getDelUserHard(),ToolUtil.conversion(ids,","),
					IdUtil.fastSimpleUUID(),RabbitInfo.EXCHANGE_NAME, RabbitInfo.KEY);
			return true;
		}
		return false;
	}

	/**
	 * 修改头像
	 * @param userId
	 * @param avatar
	 * @return
	 */
	public boolean updateAvatar(Long userId, String avatar) {
		return sysUserMapper.updateAvatar(userId,avatar) > 0;
	}

	public boolean updateUserName(Long userId, String name, String phone, String email) {
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setName(name);
		userEntity.setMobile(phone);
		userEntity.setEmail(email);
		userEntity.setId(userId);
		return this.updateById(userEntity);
	}

	public boolean  updatePassWord(Long userId, String password, String salt, String pwdSecurityLevel){
		return sysUserMapper.updatePassWord(userId,password,salt,pwdSecurityLevel) > 0;
	}

	/**
	* @Title: checkDataScope 
	* @Description: Check whether the (ids) belongs to the current user (loginUser)
	* @param ids  void 
	* @Date: 2020年9月25日
	 */
	public void checkDataScope(Long... ids) {	
		if (!UserUtils.hasAnyRoleKeys(Constant.SU_ADMIN)) {
			//check current user has data scope for the deleted use
			Map<String, Object> params = Maps.newConcurrentMap();
			params.put("ids", ids);
			List<?> listData =SpringUtil.getAopProxy(this).findList(params);
			if (null == listData || listData.size() == 0 || listData.size() != ids.length) {
				throw new RxcException(ToolUtil.message("illegal request"),"50001");
			}
		}		
	}

	public boolean checkUserNameUnique(String username) {
		int count = this.count(new QueryWrapper<SysUserEntity>().eq("username",username));
		if(count > 0){
			return false;
		}
		return  true;
	}

	public boolean checkMobileUnique(SysUserEntity user) {
		Long userId = ToolUtil.isEmpty(user.getId())?-1L:user.getId();
		SysUserEntity info = this.getOne(new QueryWrapper<SysUserEntity>().eq("mobile",user.getMobile()));
		if(ToolUtil.isNotEmpty(info) && !info.getId().equals(userId)){
			return  false;
		}
		return true;
	}

	/**
	 * 根据用户名称查询
	 * @param username
	 * @return
	 */
	public SysUserEntity findUserByUserName(String username,String tenantId){
		return sysUserMapper.findUserByUserName(username,tenantId);
	}

	/**
	 * 手机号码查询
	 * @param mobile
	 * @return
	 */
	public SysUserEntity findUserByMobile(String mobile,String tenantId){
		return sysUserMapper.findUserByMobile(mobile,tenantId);
	}

	/**
	 * 电子邮件查询
	 * @param email
	 * @return
	 */
	public SysUserEntity findUserByEmail(String email,String tenantId){
		return sysUserMapper.findUserByEmail(email,tenantId);
	}

	public boolean changeStatus(SysUserEntity user) {
		return sysUserMapper.setStatus(user.getId(),user.getStatus()) > 0;
	}

	public SysUserEntity findUserByUserId(Long userId){
		SysUserEntity user = sysUserMapper.findUserByUserId(userId);
		SysLoginInfoEntity loginInfo = ConstantFactory.me().getFirstLoginInfo(user.getUsername());
		//上次登陆地点
		user.setLoginLocation(loginInfo.getLoginLocation());
		//上次登陆时间
		user.setLoginTime(loginInfo.getLoginTime());
		return user;
	}
}
