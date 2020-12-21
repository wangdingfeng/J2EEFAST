/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.service;

import com.j2eefast.web.test.entity.ExampleAsyncTreeEntity;
import com.j2eefast.web.test.mapper.ExampleAsyncTreeMapper;
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
import java.util.ArrayList;
import com.j2eefast.common.core.base.entity.Ztree;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.MapUtil;

/**
 * 测试异步树Service接口
 * @author: ZhouZhou
 * @date 2020-12-21 11:03:28
 */
@Service
public class ExampleAsyncTreeService extends ServiceImpl<ExampleAsyncTreeMapper,ExampleAsyncTreeEntity> {

	@Resource
	private ExampleAsyncTreeMapper exampleAsyncTreeMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<ExampleAsyncTreeEntity> queryWrapper = new QueryWrapper<ExampleAsyncTreeEntity>();
		String name = (String) params.get("name");
        queryWrapper.like(ToolUtil.isNotEmpty(name), "name", name);
		String status = (String) params.get("status");
        queryWrapper.eq(ToolUtil.isNotEmpty(status), "status", status);
		Page<ExampleAsyncTreeEntity> page = exampleAsyncTreeMapper.selectPage(new Query<ExampleAsyncTreeEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,ExampleAsyncTreeEntity exampleAsyncTreeEntity) {
		Page<ExampleAsyncTreeEntity> page = exampleAsyncTreeMapper.findPage(new Query<ExampleAsyncTreeEntity>(params).getPage(),
					exampleAsyncTreeEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<ExampleAsyncTreeEntity> findList(ExampleAsyncTreeEntity exampleAsyncTreeEntity){
		return exampleAsyncTreeMapper.findList(exampleAsyncTreeEntity);
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
	public boolean delExampleAsyncTreeById(Long id) {
		// 先判断是否有子元素
		List<ExampleAsyncTreeEntity> list = this.listByMap(new MapUtil().put("parent_id", id));
		if (ToolUtil.isNotEmpty(list)) {
			throw new RxcException("请先删除子元素","50001");
		}
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addExampleAsyncTree(ExampleAsyncTreeEntity exampleAsyncTree){
		if(this.save(exampleAsyncTree)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateExampleAsyncTreeById(ExampleAsyncTreeEntity exampleAsyncTree) {
		if(this.updateById(exampleAsyncTree)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public ExampleAsyncTreeEntity findExampleAsyncTreeById(Long id){
		return exampleAsyncTreeMapper.findExampleAsyncTreeById(id);
	}

	public List<Ztree> findTree(){
		List<ExampleAsyncTreeEntity> exampleAsyncTreeList = exampleAsyncTreeMapper.findList(new ExampleAsyncTreeEntity());
		List<Ztree> ztrees = new ArrayList<Ztree>();
		for (ExampleAsyncTreeEntity exampleAsyncTree : exampleAsyncTreeList){
			Ztree ztree = new Ztree();
			ztree.setId(exampleAsyncTree.getId());
			ztree.setpId(exampleAsyncTree.getParentId());
			ztree.setName(exampleAsyncTree.getName());
			ztree.setTitle(exampleAsyncTree.getName());
			ztrees.add(ztree);
		}
		return ztrees;
	}


}
