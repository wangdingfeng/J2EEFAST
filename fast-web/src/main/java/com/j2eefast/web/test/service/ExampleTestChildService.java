/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.service;

import com.j2eefast.web.test.entity.ExampleTestChildEntity;
import com.j2eefast.web.test.mapper.ExampleTestChildMapper;
import cn.hutool.core.convert.Convert;
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
 * 工作经历Service接口
 * @author: ZhouZhou
 * @date 2020-12-21 10:42:57
 */
@Service
public class ExampleTestChildService extends ServiceImpl<ExampleTestChildMapper,ExampleTestChildEntity> {

	@Resource
	private ExampleTestChildMapper exampleTestChildMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<ExampleTestChildEntity> queryWrapper = new QueryWrapper<ExampleTestChildEntity>();
		Long testId = Convert.toLong(params.get("testId"));
		queryWrapper.eq(ToolUtil.isNotEmpty(testId), "test_id", testId);
		Page<ExampleTestChildEntity> page = exampleTestChildMapper.selectPage(new Query<ExampleTestChildEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,ExampleTestChildEntity exampleTestChildEntity) {
		Page<ExampleTestChildEntity> page = exampleTestChildMapper.findPage(new Query<ExampleTestChildEntity>(params).getPage(),
					exampleTestChildEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<ExampleTestChildEntity> findList(ExampleTestChildEntity exampleTestChildEntity){
		return exampleTestChildMapper.findList(exampleTestChildEntity);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean delExampleTestChildByPkId(Long testId){
		return this.remove(new QueryWrapper<ExampleTestChildEntity>().eq("test_id", testId));
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
	public boolean delExampleTestChildById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addExampleTestChild(ExampleTestChildEntity exampleTestChild){
		if(this.save(exampleTestChild)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateExampleTestChildById(ExampleTestChildEntity exampleTestChild) {
		if(this.updateById(exampleTestChild)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public ExampleTestChildEntity findExampleTestChildById(Long id){
		return exampleTestChildMapper.findExampleTestChildById(id);
	}


}
