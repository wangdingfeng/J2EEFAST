/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.web.legao.entity.LMemberEntity;
import com.j2eefast.web.legao.mapper.LMemberMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 会员Service接口
 * @author: polaris_wang
 * @date 2021-12-21 14:31:55
 */
@Service
public class LMemberService extends ServiceImpl<LMemberMapper, LMemberEntity> {

	@Resource
	private LMemberMapper lMemberMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<LMemberEntity> queryWrapper = new QueryWrapper<LMemberEntity>();
		String name = (String) params.get("name");
        queryWrapper.like(ToolUtil.isNotEmpty(name), "name", name);
		String phone = (String) params.get("phone");
        queryWrapper.eq(ToolUtil.isNotEmpty(phone), "phone", phone);
		String gender = (String) params.get("gender");
        queryWrapper.eq(ToolUtil.isNotEmpty(gender), "gender", gender);
		String email = (String) params.get("email");
        queryWrapper.eq(ToolUtil.isNotEmpty(email), "email", email);
		String amount = (String) params.get("amount");
        queryWrapper.eq(ToolUtil.isNotEmpty(amount), "amount", amount);
		String duration = (String) params.get("duration");
        queryWrapper.eq(ToolUtil.isNotEmpty(duration), "duration", duration);
		String status = (String) params.get("status");
        queryWrapper.eq(ToolUtil.isNotEmpty(status), "status", status);
		String tenantId = (String) params.get("tenantId");
        queryWrapper.eq(ToolUtil.isNotEmpty(tenantId), "tenant_id", tenantId);
		Page<LMemberEntity> page = lMemberMapper.selectPage(new Query<LMemberEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,LMemberEntity lMemberEntity) {
		Page<LMemberEntity> page = lMemberMapper.findPage(new Query<LMemberEntity>(params).getPage(),
					lMemberEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<LMemberEntity> findList(LMemberEntity lMemberEntity){
		return lMemberMapper.findList(lMemberEntity);
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
	public boolean delLMemberById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addLMember(LMemberEntity lMember){
		if(this.save(lMember)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateLMemberById(LMemberEntity lMember) {
		if(this.updateById(lMember)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public LMemberEntity findLMemberById(Long id){
		return lMemberMapper.findLMemberById(id);
	}

	/**
	 * 查询所有会员
	 * @return
	 */
	public List<LMemberEntity> getAllPhoneSelect(){
		return this.list(Wrappers.<LMemberEntity>lambdaQuery().eq(LMemberEntity::getStatus,"0"));
	}

	/**
	 * 通过手机号查询会员
	 * @param phone
	 * @return
	 */
	public Map<Long,String> getPhoneSelect(String phone){
		return this.list(Wrappers.<LMemberEntity>lambdaQuery().like(LMemberEntity::getPhone,phone))
				.stream().collect(Collectors.toMap(LMemberEntity::getId,LMemberEntity::getPhone));
	}


}
