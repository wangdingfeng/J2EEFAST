/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.j2eefast.common.core.base.entity.LoginTenantEntity;
import com.j2eefast.common.core.base.entity.Ztree;
import com.j2eefast.framework.sys.entity.SysTenantEntity;
import com.j2eefast.framework.sys.mapper.SysTenantMapper;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * 租户Service接口
 * @author: ZhouZhou
 * @date 2021-07-23 13:39:18
 */
@Service
public class SysTenantService extends ServiceImpl<SysTenantMapper,SysTenantEntity> {

	@Resource
	private SysTenantMapper sysTenantMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysTenantEntity> queryWrapper = new QueryWrapper<SysTenantEntity>();
		String tenantId = (String) params.get("tenantId");
        queryWrapper.eq(ToolUtil.isNotEmpty(tenantId), "tenant_id", tenantId);
		String tenantName = (String) params.get("tenantName");
        queryWrapper.like(ToolUtil.isNotEmpty(tenantName), "tenant_name", tenantName);
		String linkman = (String) params.get("linkman");
        queryWrapper.eq(ToolUtil.isNotEmpty(linkman), "linkman", linkman);
		String link = (String) params.get("link");
        queryWrapper.eq(ToolUtil.isNotEmpty(link), "link", link);
		String status = (String) params.get("status");
        queryWrapper.eq(ToolUtil.isNotEmpty(status), "status", status);
		Page<SysTenantEntity> page = sysTenantMapper.selectPage(new Query<SysTenantEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysTenantEntity sysTenantEntity) {
		Page<SysTenantEntity> page = sysTenantMapper.findPage(new Query<SysTenantEntity>(params).getPage(),
					sysTenantEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysTenantEntity> findList(SysTenantEntity sysTenantEntity){
		return sysTenantMapper.findList(sysTenantEntity);
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
	public boolean delSysTenantById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysTenant(SysTenantEntity sysTenant){
		if(this.save(sysTenant)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysTenantById(SysTenantEntity sysTenant) {
		if(this.updateById(sysTenant)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public SysTenantEntity findSysTenantById(Long id){
		return sysTenantMapper.findSysTenantById(id);
	}


	public boolean isTenantNull(String tenantId){
		return this.count(new QueryWrapper<SysTenantEntity>().eq("status","0").eq("tenant_id",tenantId)) > 0;
	}

	/**
	 * 唯一性校验
	 * @return
	 */
	public boolean checkTenantIdUnique(SysTenantEntity sysTenant) {
		Long pkId =  ToolUtil.isEmpty(sysTenant.getId()) ? null : sysTenant.getId();
		SysTenantEntity entity = this.getOne(new QueryWrapper<SysTenantEntity>()
				.eq("tenant_id",sysTenant.getTenantId()));
		if (!ToolUtil.isEmpty(entity) && !entity.getId().equals(pkId)){
			return  false;
		}
		return true;
	}

	/**
	 * 拉取登录租户
	 * @return
	 */
	public List<LoginTenantEntity> getLoginTenantList(){
		List<LoginTenantEntity> outList = new ArrayList<>();
		List<SysTenantEntity> list = this.list(new QueryWrapper<SysTenantEntity>().eq("status","0"));
		if(ToolUtil.isNotEmpty(list)){
			for(SysTenantEntity tenant :list){
				LoginTenantEntity loginTenantEntity = new LoginTenantEntity();
				loginTenantEntity.setTenantId(tenant.getTenantId());
				loginTenantEntity.setTenantName(tenant.getTenantName());
				outList.add(loginTenantEntity);
			}
		}
		return outList;
	}

	/**
	 * 获取所有正常租户树
	 * @return
	 */
	public  List<Ztree> findTenantTree(){
		List<LoginTenantEntity> list = this.getLoginTenantList();
		List<Ztree> ztrees = new ArrayList<>();
		for(LoginTenantEntity tenant:list){
			Ztree ztree = new Ztree();
			ztree.setId(tenant.getTenantId());
			ztree.setpId("0");
			ztree.setName(tenant.getTenantName());
			ztree.setTitle(tenant.getTenantName());
			ztree.setIsParent(true);
			ztrees.add(ztree);
		}
		return ztrees;
	}

}
