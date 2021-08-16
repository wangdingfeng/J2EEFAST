/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_ureport_file Mapper接口
 * @author: ZhouZhou
 * @date 2021-08-14 19:57:51
 */
public interface SysUreportFileMapper extends BaseMapper<SysUreportFileEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysUreportFileEntity 实体类
     */
    Page<SysUreportFileEntity> findPage(IPage<SysUreportFileEntity> page,
                                      @Param("sysUreportFile") SysUreportFileEntity sysUreportFileEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysUreportFileEntity findSysUreportFileById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysUreportFileEntity 查询条件对象
     * @return
     */
    List<SysUreportFileEntity> findList(SysUreportFileEntity sysUreportFileEntity);

}
