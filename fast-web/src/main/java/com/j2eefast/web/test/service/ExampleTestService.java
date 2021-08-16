/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.service;

import com.j2eefast.web.test.entity.ExampleTestEntity;
import com.j2eefast.web.test.mapper.ExampleTestMapper;
import com.j2eefast.web.test.entity.ExampleTestChildEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.j2eefast.common.db.utils.DbUtil;
import com.j2eefast.framework.utils.FileUploadUtils;

/**
 * 单范例Service接口
 * @author: ZhouZhou
 * @date 2021-08-15 21:35:17
 */
@Service
public class ExampleTestService extends ServiceImpl<ExampleTestMapper,ExampleTestEntity> {

	@Resource
	private ExampleTestMapper exampleTestMapper;

	@Autowired
	private ExampleTestChildService exampleTestChildService;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<ExampleTestEntity> queryWrapper = new QueryWrapper<ExampleTestEntity>();
		String code = (String) params.get("code");
        queryWrapper.eq(ToolUtil.isNotEmpty(code), "code", code);
		String name = (String) params.get("name");
        queryWrapper.like(ToolUtil.isNotEmpty(name), "name", name);
		String email = (String) params.get("email");
        queryWrapper.eq(ToolUtil.isNotEmpty(email), "email", email);
		String phone = (String) params.get("phone");
        queryWrapper.eq(ToolUtil.isNotEmpty(phone), "phone", phone);
		String sex = (String) params.get("sex");
        queryWrapper.eq(ToolUtil.isNotEmpty(sex), "sex", sex);
		String age = (String) params.get("age");
        queryWrapper.eq(ToolUtil.isNotEmpty(age), "age", age);
		String compId = (String) params.get("compId");
        queryWrapper.eq(ToolUtil.isNotEmpty(compId), "comp_id", compId);
		Page<ExampleTestEntity> page = exampleTestMapper.selectPage(new Query<ExampleTestEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,ExampleTestEntity exampleTestEntity) {
		Page<ExampleTestEntity> page = exampleTestMapper.findPage(new Query<ExampleTestEntity>(params).getPage(),
					exampleTestEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<ExampleTestEntity> findList(ExampleTestEntity exampleTestEntity){
		return exampleTestMapper.findList(exampleTestEntity);
	}

	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		for (Long id : ids) {
			FileUploadUtils.me().removeFileUpload(id,"example_test_file");
			FileUploadUtils.me().removeFileUpload(id,"example_test_img");
			FileUploadUtils.me().removeFileUpload(id,"example_test_avatar");
		}
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delExampleTestById(Long id) {
		FileUploadUtils.me().removeFileUpload(id,"example_test_file");
		FileUploadUtils.me().removeFileUpload(id,"example_test_img");
		FileUploadUtils.me().removeFileUpload(id,"example_test_avatar");
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addExampleTest(ExampleTestEntity exampleTest){
		//事先生成id
		exampleTest.setId(DbUtil.getDbId());
		//图片剪切数据转换
		exampleTest.setAvatar(FileUploadUtils.saveImgBase64(exampleTest.getAvatar(),"example_test_avatar",exampleTest.getId()));
		if(this.save(exampleTest)){
			//更新关联附件信息
			Long pkId =  exampleTest.getId();
			FileUploadUtils.saveFileUpload(pkId,"example_test_file");
			FileUploadUtils.saveFileUpload(pkId,"example_test_img");
			//保存子表信息
			if(ToolUtil.isNotEmpty(exampleTest.getExampleTestChild())){
				for(ExampleTestChildEntity exampleTestChild :exampleTest.getExampleTestChild()){
					exampleTestChild.setTestId(pkId);
					exampleTestChildService.addExampleTestChild(exampleTestChild);
				}
			}
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateExampleTestById(ExampleTestEntity exampleTest) {
		//图片剪切数据转换
		exampleTest.setAvatar(FileUploadUtils.saveImgBase64(exampleTest.getAvatar(),"example_test_avatar",exampleTest.getId()));
		if(this.updateById(exampleTest)){
			//更新关联附件信息
			Long pkId =  exampleTest.getId();
			FileUploadUtils.saveFileUpload(pkId,"example_test_file");
			FileUploadUtils.saveFileUpload(pkId,"example_test_img");
			//保存子表信息
			if(exampleTestChildService.delExampleTestChildByPkId(pkId)){
				if(ToolUtil.isNotEmpty(exampleTest.getExampleTestChild())){
					for(ExampleTestChildEntity exampleTestChild :exampleTest.getExampleTestChild()){
						exampleTestChild.setTestId(pkId);
						exampleTestChildService.addExampleTestChild(exampleTestChild);
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public ExampleTestEntity findExampleTestById(Long id){
		return exampleTestMapper.findExampleTestById(id);
	}

	/**
	 * 唯一性校验
	 * @return
	 */
	public boolean checkCodeUnique(ExampleTestEntity exampleTest) {
		Long pkId =  ToolUtil.isEmpty(exampleTest.getId()) ? null : exampleTest.getId();
		ExampleTestEntity entity = this.getOne(new QueryWrapper<ExampleTestEntity>()
				.eq("code",exampleTest.getCode()));
		if (!ToolUtil.isEmpty(entity) && !entity.getId().equals(pkId)){
			return  false;
		}
		return true;
	}

}
