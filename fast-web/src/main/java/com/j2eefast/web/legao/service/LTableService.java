/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.web.legao.entity.LMemberEntity;
import com.j2eefast.web.legao.entity.LMemberTableEntity;
import com.j2eefast.web.legao.entity.LTableEntity;
import com.j2eefast.web.legao.mapper.LMemberMapper;
import com.j2eefast.web.legao.mapper.LTableMapper;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.web.legao.vo.LTableVo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * 乐高店桌号Service接口
 * @author: polaris_wang
 * @date 2021-12-21 15:12:50
 */
@Service
public class LTableService extends ServiceImpl<LTableMapper,LTableEntity> {

	@Resource
	private LTableMapper lTableMapper;
	@Resource
	private LMemberMapper memberMapper;
	@Resource
	private LMemberTableService memberTableService;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<LTableEntity> queryWrapper = new QueryWrapper<LTableEntity>();
		String tableNum = (String) params.get("tableNum");
        queryWrapper.eq(ToolUtil.isNotEmpty(tableNum), "table_num", tableNum);
		String tableName = (String) params.get("tableName");
        queryWrapper.like(ToolUtil.isNotEmpty(tableName), "table_name", tableName);
		String status = (String) params.get("status");
        queryWrapper.eq(ToolUtil.isNotEmpty(status), "status", status);
		Page<LTableEntity> page = lTableMapper.selectPage(new Query<LTableEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,LTableEntity lTableEntity) {
		Page<LTableVo> page = lTableMapper.findPage(new Query<LTableEntity>(params).getPage(),
					lTableEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<LTableEntity> findList(LTableEntity lTableEntity){
		return lTableMapper.findList(lTableEntity);
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
	public boolean delLTableById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addLTable(LTableEntity lTable){
		if(lTableMapper.findByNum(lTable.getTableNum()) > 0){
			throw new RxcException("当前桌号已经存在!");
		}
		lTable.setStatus("0");
		if(this.save(lTable)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateLTableById(LTableEntity lTable) {
		if(this.updateById(lTable)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public LTableEntity findLTableById(Long id){
		return lTableMapper.findLTableById(id);
	}

	/**
	 * 分配桌号
	 * @param lMemberTable
	 */
	@Transactional(rollbackFor = Exception.class)
	public void distMemberTable(LMemberTableEntity lMemberTable){
		// 保存账务流水
		lMemberTable.setMemberId(lMemberTable.getMemberId());
		lMemberTable.setTableId(lMemberTable.getTableId());
		// 查询用户信息
		LMemberEntity lMember = memberMapper.findLMemberById(lMemberTable.getMemberId());
		if(null == lMember){
			throw new RxcException("当前用户不存在!");
		}
		if(lMember.getDuration() < 1){
			throw new RxcException("当前用户剩余时长为0!");
		}
		// 查询当前用户是否在使用中
		int count = memberTableService.count(Wrappers.<LMemberTableEntity>lambdaQuery()
				.eq(LMemberTableEntity::getMemberId,lMemberTable.getMemberId()).eq(LMemberTableEntity::getStatus,"0"));
		if(count > 0){
			throw new RxcException("当前用户已在其他的桌子上!");
		}
		lMemberTable.setStartAmount(lMember.getAmount());
		lMemberTable.setStartDuration(lMember.getDuration());
		lMemberTable.setStatus("0");
		// 添加一条流水信息
		memberTableService.save(lMemberTable);
		//更新桌号状态 已使用
		LTableEntity lTableEntity = new LTableEntity();
		lTableEntity.setId(lMemberTable.getTableId());
		lTableEntity.setMemberTableId(lMemberTable.getId());
		lTableEntity.setStatus("1");
		this.updateLTableById(lTableEntity);

	}

	/**
	 * 停止桌子使用
	 * @param memberTableId
	 */
	public void stopMemberTable(LMemberTableEntity memberTableId){
		LTableEntity lTableById = this.findLTableById(memberTableId.getTableId());
		if(null == lTableById){
			throw new RxcException("当前桌号信息查询不到！");
		}else if(!"1".equals(lTableById.getStatus())){
			throw new RxcException("当前桌子状态不是已使用状态，不能停用！");
		}
		lTableById.setStatus("0");
		// 查询使用桌子用户信息
		LMemberEntity lMember = memberMapper.findLMemberById(memberTableId.getMemberId());
		// 查询桌子流水
		LMemberTableEntity lMemberTable = memberTableService.findLMemberTableById(lTableById.getMemberTableId());
		lMemberTable.setEndAmount(memberTableId.getEndAmount());
		lMemberTable.setEndDuration(memberTableId.getEndDuration());
		lMemberTable.setStatus("1");
		lMember.setAmount(memberTableId.getEndAmount());
		lMember.setDuration(memberTableId.getEndDuration());
		// 更新用户金额和时长
		memberMapper.updateById(lMember);
		// 更新流水
		memberTableService.updateLMemberTableById(lMemberTable);

	}
}
