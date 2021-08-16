/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.service;

import com.j2eefast.web.test.entity.ExampleSynchTreeEntity;
import com.j2eefast.web.test.mapper.ExampleSynchTreeMapper;
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
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.xss.SQLFilter;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.MapUtil;

/**
 * 测试同步树Service接口
 * @author: ZhouZhou
 * @date 2020-12-21 10:58:12
 */
@Service
public class ExampleSynchTreeService extends ServiceImpl<ExampleSynchTreeMapper,ExampleSynchTreeEntity> {

	@Resource
	private ExampleSynchTreeMapper exampleSynchTreeMapper;

	/**
	* 查列表
	*/
	public List<ExampleSynchTreeEntity> findList(ExampleSynchTreeEntity exampleSynchTreeEntity){
		return exampleSynchTreeMapper.findList(exampleSynchTreeEntity);
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
	public boolean delExampleSynchTreeById(Long id) {
		// 先判断是否有子元素
		List<ExampleSynchTreeEntity> list = this.listByMap(new MapUtil().put("parent_id", id));
		if (ToolUtil.isNotEmpty(list)) {
			throw new RxcException("请先删除子元素","50001");
		}
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addExampleSynchTree(ExampleSynchTreeEntity exampleSynchTree){
		if(this.save(exampleSynchTree)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateExampleSynchTreeById(ExampleSynchTreeEntity exampleSynchTree) {
		if(this.updateById(exampleSynchTree)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public ExampleSynchTreeEntity findExampleSynchTreeById(Long id){
		return exampleSynchTreeMapper.findExampleSynchTreeById(id);
	}

	/**
     * 页面树查询
     */
	public List<ExampleSynchTreeEntity> findTreeList(Map<String, Object> params,ExampleSynchTreeEntity exampleSynchTreeEntity){
		String sidx = SQLFilter.sqlInject((String) params.get("__sidx"));
		String order = SQLFilter.sqlInject((String) params.get("__order"));
		List<ExampleSynchTreeEntity>  list = exampleSynchTreeMapper.findTreeList(exampleSynchTreeEntity,
																	   	StrUtil.toUnderlineCase(sidx),
															           	order.toUpperCase(),
																	   	(String) params.get(Constant.SQL_FILTER));
		return list;
	}

	public List<Ztree> findTree(){
		List<ExampleSynchTreeEntity> exampleSynchTreeList = exampleSynchTreeMapper.findList(new ExampleSynchTreeEntity());
		List<Ztree> ztrees = new ArrayList<Ztree>();
		for (ExampleSynchTreeEntity exampleSynchTree : exampleSynchTreeList){
			Ztree ztree = new Ztree();
			ztree.setId(String.valueOf(exampleSynchTree.getId()));
			ztree.setpId(String.valueOf(exampleSynchTree.getParentId()));
			ztree.setName(exampleSynchTree.getName());
			ztree.setTitle(exampleSynchTree.getName());
			ztrees.add(ztree);
		}
		return ztrees;
	}


}
