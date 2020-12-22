/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.framework.sys.entity.SysFilesEntity;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface SysFileMapper extends BaseMapper<SysFilesEntity> {
	
	/**
     * 自定义分页查询
     * @param  page
     * @param  sysFilesEntity 实体类
     */
     Page<SysFilesEntity> findPage(IPage<SysFilesEntity> page, @Param("sysFiles") SysFilesEntity sysFilesEntity);

     SysFilesEntity findSysFilesById(Serializable id);


     List<SysFilesEntity> findList(SysFilesEntity sysFilesEntity);

    /**
     * 通过业务ID 类型 查询文件列表
     * @param bizId 业务主键
     * @param bizType 业务类型
     * @return
     */
    List<SysFilesEntity> selectSysFilesList(@Param("bizId") String bizId,
                                            @Param("bizType") String bizType);
}
