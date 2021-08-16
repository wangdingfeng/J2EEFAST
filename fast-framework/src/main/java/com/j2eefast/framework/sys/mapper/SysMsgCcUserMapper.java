/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysMsgCcUserEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_msg_cc_user Mapper接口
 * @author: ZhouZhou
 * @date 2021-02-22 21:19:11
 */
public interface SysMsgCcUserMapper extends BaseMapper<SysMsgCcUserEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysMsgCcUserEntity 实体类
     */
    Page<SysMsgCcUserEntity> findPage(IPage<SysMsgCcUserEntity> page,
                                      @Param("sysMsgCcUser") SysMsgCcUserEntity sysMsgCcUserEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysMsgCcUserEntity findSysMsgCcUserById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysMsgCcUserEntity 查询条件对象
     * @return
     */
    List<SysMsgCcUserEntity> findList(SysMsgCcUserEntity sysMsgCcUserEntity);

}
