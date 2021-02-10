/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.entity.SysFilesEntity;
import com.j2eefast.framework.sys.mapper.SysFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

@Service
public class SysFileService extends ServiceImpl<SysFileMapper, SysFilesEntity> {

	
	
	@Resource
	private SysFileMapper sysFileMapper;
		/**
	 * mybaitis-plus   单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
        QueryWrapper<SysFilesEntity> queryWrapper = new QueryWrapper<SysFilesEntity>();
           String fileMd5 = (String) params.get("fileMd5");
         queryWrapper.eq(ToolUtil.isNotEmpty(fileMd5), "file_md5", fileMd5);
          String filePath = (String) params.get("filePath");
         queryWrapper.eq(ToolUtil.isNotEmpty(filePath), "file_path", filePath);
          String fileName = (String) params.get("fileName");
        queryWrapper.like(ToolUtil.isNotEmpty(fileName), "file_name", fileName);
          String fileSize = (String) params.get("fileSize");
         queryWrapper.eq(ToolUtil.isNotEmpty(fileSize), "file_size", fileSize);
  		Page<SysFilesEntity> page = sysFileMapper.selectPage(new Query<SysFilesEntity>(params).getPage(), queryWrapper);
				return new PageUtil(page);
    }

    /**
     * 自定义分页查询，含关联实体对像
     */
	public PageUtil findPage(Map<String, Object> params,SysFilesEntity sysFilesEntity) {
		Page<SysFilesEntity> page = sysFileMapper.findPage(new Query<SysFilesEntity>(params).getPage(),sysFilesEntity);
		return new PageUtil(page);
	}
	
	
	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delSysFilesByIds(Long[] ids) {
		
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
	public boolean delSysFilesById(Long id) {
		return this.removeById(id);
	}

	public boolean delSysFielsByMd5(String md5){
		return this.remove(new QueryWrapper<SysFilesEntity>().eq("file_md5",md5));
	}

	/**
	 * 通过文件MD5判断文件是否存在
	 * @param md5
	 * @return
	 */
	public SysFilesEntity findSysFileByMd5(String md5){
		return this.getOne(new QueryWrapper<SysFilesEntity>().eq("file_md5",md5));
	}
	/**
	
    /**
     * 通过业务ID,类型查询 文件列表
     * @param bizId
     * @param bizType
     * @return
     */
    public List<SysFilesEntity> selectSysFilesList(String bizId, String bizType){
        return this.baseMapper.selectSysFilesList(bizId,bizType);
    }

    public SysFilesEntity getSysFileById(Long id){
        return this.getById(id);
    }
}
