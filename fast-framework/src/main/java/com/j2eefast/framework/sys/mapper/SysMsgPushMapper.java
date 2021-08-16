/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysMsgPushEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_msg_push Mapper接口
 * @author: ZhouZhou
 * @date 2021-02-21 22:43:07
 */
public interface SysMsgPushMapper extends BaseMapper<SysMsgPushEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysMsgPushEntity 实体类
     */
    Page<SysMsgPushEntity> findPage(IPage<SysMsgPushEntity> page,
                                      @Param("sysMsgPush") SysMsgPushEntity sysMsgPushEntity,
                                      @Param("sql_filter") String sql_filter);

    Page<SysMsgPushEntity> findUserPage(IPage<SysMsgPushEntity> page,
                                    @Param("sysMsgPush") SysMsgPushEntity sysMsgPushEntity,
                                    @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysMsgPushEntity findSysMsgPushById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysMsgPushEntity 查询条件对象
     * @return
     */
    List<SysMsgPushEntity> findList(SysMsgPushEntity sysMsgPushEntity);

    List<SysMsgPushEntity> findUserList(SysMsgPushEntity sysMsgPushEntity);

}
