/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import com.j2eefast.framework.sys.mapper.SysUreportFileMapper;
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
 * ureport2报配置存储Service接口
 * @author: ZhouZhou
 * @date 2021-08-14 19:57:51
 */
@Service
public class SysUreportFileService extends ServiceImpl<SysUreportFileMapper,SysUreportFileEntity> {

	@Resource
	private SysUreportFileMapper sysUreportFileMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysUreportFileEntity> queryWrapper = new QueryWrapper<SysUreportFileEntity>();
		String name = (String) params.get("name");
        queryWrapper.like(ToolUtil.isNotEmpty(name), "name", name);
		Page<SysUreportFileEntity> page = sysUreportFileMapper.selectPage(new Query<SysUreportFileEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysUreportFileEntity sysUreportFileEntity) {
		Page<SysUreportFileEntity> page = sysUreportFileMapper.findPage(new Query<SysUreportFileEntity>(params).getPage(),
					sysUreportFileEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysUreportFileEntity> findList(SysUreportFileEntity sysUreportFileEntity){
		return sysUreportFileMapper.findList(sysUreportFileEntity);
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
	public boolean delSysUreportFileById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysUreportFile(SysUreportFileEntity sysUreportFile){
		if(this.save(sysUreportFile)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysUreportFileById(SysUreportFileEntity sysUreportFile) {
		if(this.updateById(sysUreportFile)){
			return true;
		}
		return false;
	}

	/**
	 * 通过文件名称获取
	 * @param fileName
	 * @return
	 */
	public  SysUreportFileEntity getUreportFileByFileName(String fileName){
		return this.getOne(new QueryWrapper<SysUreportFileEntity>().eq("name",fileName));
	}

	/**
	 * 通过文件名称删除
	 * @param fileName
	 * @return
	 */
	public boolean delUreportFileByFileName(String fileName){
		return this.remove(new QueryWrapper<SysUreportFileEntity>().eq("name",fileName));
	}

	/**
     * 根居ID获取对象
     */
	public SysUreportFileEntity findSysUreportFileById(Long id){
		return sysUreportFileMapper.findSysUreportFileById(id);
	}


}
