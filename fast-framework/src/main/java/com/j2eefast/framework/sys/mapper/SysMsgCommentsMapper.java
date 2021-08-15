/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysMsgCommentsEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_msg_comments Mapper接口
 * @author: ZhouZhou
 * @date 2021-02-28 11:00:00
 */
public interface SysMsgCommentsMapper extends BaseMapper<SysMsgCommentsEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysMsgCommentsEntity 实体类
     */
    Page<SysMsgCommentsEntity> findPage(IPage<SysMsgCommentsEntity> page,
                                      @Param("sysMsgComments") SysMsgCommentsEntity sysMsgCommentsEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysMsgCommentsEntity findSysMsgCommentsById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysMsgCommentsEntity 查询条件对象
     * @return
     */
    List<SysMsgCommentsEntity> findList(SysMsgCommentsEntity sysMsgCommentsEntity);

    /**
     * 修改回复评论是否有子元素
     * @param replyId
     * @param IsReple
     * @return
     */
    int setIsReply(@Param("id") Long replyId,
                  @Param("isReple") String isReple);

}
