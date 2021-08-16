/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysMsgPushUserEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_msg_push_user Mapper接口
 * @author: ZhouZhou
 * @date 2021-02-22 21:04:41
 */
public interface SysMsgPushUserMapper extends BaseMapper<SysMsgPushUserEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysMsgPushUserEntity 实体类
     */
    Page<SysMsgPushUserEntity> findPage(IPage<SysMsgPushUserEntity> page,
                                      @Param("sysMsgPushUser") SysMsgPushUserEntity sysMsgPushUserEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysMsgPushUserEntity findSysMsgPushUserById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysMsgPushUserEntity 查询条件对象
     * @return
     */
    List<SysMsgPushUserEntity> findList(SysMsgPushUserEntity sysMsgPushUserEntity);


    /**
     * 修改接收者阅读标志
     * @param isRead 阅读标志 1 已阅
     * @param msgId 消息id
     * @param userId 用户id
     * @return
     */
    int setIsRead(@Param("isRead") String isRead,
                  @Param("msgId") Long msgId,
                  @Param("userId") Long userId);
}
