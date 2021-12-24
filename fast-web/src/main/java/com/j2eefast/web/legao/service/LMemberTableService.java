/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.service;

import com.j2eefast.web.legao.entity.LMemberTableEntity;
import com.j2eefast.web.legao.mapper.LMemberTableMapper;
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
 * 会员分配桌号流水Service接口
 * @author: polaris_wang
 * @date 2021-12-22 11:57:03
 */
@Service
public class LMemberTableService extends ServiceImpl<LMemberTableMapper,LMemberTableEntity> {

	@Resource
	private LMemberTableMapper lMemberTableMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<LMemberTableEntity> queryWrapper = new QueryWrapper<LMemberTableEntity>();
		String memberId = (String) params.get("memberId");
        queryWrapper.eq(ToolUtil.isNotEmpty(memberId), "member_id", memberId);
		String tableId = (String) params.get("tableId");
        queryWrapper.eq(ToolUtil.isNotEmpty(tableId), "table_id", tableId);
		String startAmount = (String) params.get("startAmount");
        queryWrapper.eq(ToolUtil.isNotEmpty(startAmount), "start_amount", startAmount);
		String startDuration = (String) params.get("startDuration");
        queryWrapper.eq(ToolUtil.isNotEmpty(startDuration), "start_duration", startDuration);
		String endAmount = (String) params.get("endAmount");
        queryWrapper.eq(ToolUtil.isNotEmpty(endAmount), "end_amount", endAmount);
		String endDuration = (String) params.get("endDuration");
        queryWrapper.eq(ToolUtil.isNotEmpty(endDuration), "end_duration", endDuration);
		String status = (String) params.get("status");
        queryWrapper.eq(ToolUtil.isNotEmpty(status), "status", status);
		Page<LMemberTableEntity> page = lMemberTableMapper.selectPage(new Query<LMemberTableEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,LMemberTableEntity lMemberTableEntity) {
		Page<LMemberTableEntity> page = lMemberTableMapper.findPage(new Query<LMemberTableEntity>(params).getPage(),
					lMemberTableEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<LMemberTableEntity> findList(LMemberTableEntity lMemberTableEntity){
		return lMemberTableMapper.findList(lMemberTableEntity);
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
	public boolean delLMemberTableById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addLMemberTable(LMemberTableEntity lMemberTable){
		if(this.save(lMemberTable)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateLMemberTableById(LMemberTableEntity lMemberTable) {
		if(this.updateById(lMemberTable)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public LMemberTableEntity findLMemberTableById(Long id){
		return lMemberTableMapper.findLMemberTableById(id);
	}


}
