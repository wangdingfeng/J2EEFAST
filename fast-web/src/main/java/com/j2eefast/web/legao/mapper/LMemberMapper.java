/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.j2eefast.web.legao.entity.LMemberEntity;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * l_member Mapper接口
 * @author: polaris_wang
 * @date 2021-12-21 14:31:55
 */
public interface LMemberMapper extends BaseMapper<LMemberEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  lMemberEntity 实体类
     */
    Page<LMemberEntity> findPage(IPage<LMemberEntity> page,
                                      @Param("lMember") LMemberEntity lMemberEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    LMemberEntity findLMemberById(@Param("id") Long id);

    /**
     * 查询列表
     * @param lMemberEntity 查询条件对象
     * @return
     */
    List<LMemberEntity> findList(LMemberEntity lMemberEntity);

}
