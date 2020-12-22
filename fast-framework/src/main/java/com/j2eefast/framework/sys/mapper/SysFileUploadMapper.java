/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysFileUploadEntity;
import java.io.Serializable;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/**
 *
 * sys_file_uploadDAO接口
 * @author: ZhouZhou
 * @date 2020-07-29 18:06
 */
public interface SysFileUploadMapper extends BaseMapper<SysFileUploadEntity> {

    /**
     * 自定义分页查询
     * @param page
     * @param sysFileUploadEntity
     * @param sql_filter
     * @return
     */
     Page<SysFileUploadEntity> findPage(IPage<SysFileUploadEntity> page,
                                       @Param("sysFileUpload") SysFileUploadEntity sysFileUploadEntity,
                                       @Param("sql_filter") String sql_filter);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
     SysFileUploadEntity selectSysFileUploadById(Serializable id);


    /**
     * 返回List对象
     * @param sysFileUploadEntity
     * @return
     */
     List<SysFileUploadEntity> findList(SysFileUploadEntity sysFileUploadEntity);
}
