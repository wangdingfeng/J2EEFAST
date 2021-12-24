/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.mapper;

import com.j2eefast.web.legao.entity.LMemberTableEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * l_member_table Mapper接口
 * @author: polaris_wang
 * @date 2021-12-22 11:57:03
 */
public interface LMemberTableMapper extends BaseMapper<LMemberTableEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  lMemberTableEntity 实体类
     */
    Page<LMemberTableEntity> findPage(IPage<LMemberTableEntity> page,
                                      @Param("lMemberTable") LMemberTableEntity lMemberTableEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    LMemberTableEntity findLMemberTableById(@Param("id") Long id);

    /**
     * 查询列表
     * @param lMemberTableEntity 查询条件对象
     * @return
     */
    List<LMemberTableEntity> findList(LMemberTableEntity lMemberTableEntity);

}
