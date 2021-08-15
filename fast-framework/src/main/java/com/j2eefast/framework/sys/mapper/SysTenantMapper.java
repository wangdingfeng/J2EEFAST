/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.j2eefast.framework.sys.entity.SysTenantEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * sys_tenant Mapper接口
 * @author: ZhouZhou
 * @date 2021-07-23 13:39:18
 */
public interface SysTenantMapper extends BaseMapper<SysTenantEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysTenantEntity 实体类
     */
    Page<SysTenantEntity> findPage(IPage<SysTenantEntity> page,
                                      @Param("sysTenant") SysTenantEntity sysTenantEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysTenantEntity findSysTenantById(@Param("id") Long id);

    /**
     * 通过租户ID获取租户
     * @param tenantId
     * @return
     */
    SysTenantEntity findSysTenantByIdTenantId(@Param("tenantId") String tenantId);

    /**
     * 查询列表
     * @param sysTenantEntity 查询条件对象
     * @return
     */
    List<SysTenantEntity> findList(SysTenantEntity sysTenantEntity);

}
