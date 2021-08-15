/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.j2eefast.framework.sys.entity.SysMsgCcUserEntity;
import com.j2eefast.framework.sys.entity.SysMsgPushUserEntity;
import com.j2eefast.framework.sys.mapper.SysMsgCcUserMapper;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.Constant;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * 抄送用户Service接口
 * @author: ZhouZhou
 * @date 2021-02-22 21:19:11
 */
@Service
public class SysMsgCcUserService extends ServiceImpl<SysMsgCcUserMapper,SysMsgCcUserEntity> {

	@Resource
	private SysMsgCcUserMapper sysMsgCcUserMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysMsgCcUserEntity> queryWrapper = new QueryWrapper<SysMsgCcUserEntity>();
		String msgId = (String) params.get("msgId");
        queryWrapper.eq(ToolUtil.isNotEmpty(msgId), "msg_id", msgId);
		String userCode = (String) params.get("userCode");
        queryWrapper.eq(ToolUtil.isNotEmpty(userCode), "user_code", userCode);
		String userName = (String) params.get("userName");
        queryWrapper.like(ToolUtil.isNotEmpty(userName), "user_name", userName);
		String isRead = (String) params.get("isRead");
        queryWrapper.eq(ToolUtil.isNotEmpty(isRead), "is_read", isRead);
		String readDate = (String) params.get("readDate");
        queryWrapper.eq(ToolUtil.isNotEmpty(readDate), "read_date", readDate);
		Page<SysMsgCcUserEntity> page = sysMsgCcUserMapper.selectPage(new Query<SysMsgCcUserEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysMsgCcUserEntity sysMsgCcUserEntity) {
		Page<SysMsgCcUserEntity> page = sysMsgCcUserMapper.findPage(new Query<SysMsgCcUserEntity>(params).getPage(),
					sysMsgCcUserEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysMsgCcUserEntity> findList(SysMsgCcUserEntity sysMsgCcUserEntity){
		return sysMsgCcUserMapper.findList(sysMsgCcUserEntity);
	}

	public List<SysMsgCcUserEntity> findList(Long msgId){
		return this.list(new QueryWrapper<SysMsgCcUserEntity>().eq("msg_id",msgId));
	}

	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delSysMsgCcUserById(Long id) {
		return this.removeById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean delSysMsgCcUserByMsgId(Long msgId) {
		return this.remove(new QueryWrapper<SysMsgCcUserEntity>().eq("msg_id",msgId));
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysMsgCcUser(SysMsgCcUserEntity sysMsgCcUser){
		if(this.save(sysMsgCcUser)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysMsgCcUserById(SysMsgCcUserEntity sysMsgCcUser) {
		if(this.updateById(sysMsgCcUser)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public SysMsgCcUserEntity findSysMsgCcUserById(Long id){
		return sysMsgCcUserMapper.findSysMsgCcUserById(id);
	}


}
