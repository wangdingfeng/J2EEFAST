/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.mapper;

import com.j2eefast.web.test.entity.ExampleTestEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * example_test Mapper接口
 * @author: ZhouZhou
 * @date 2021-08-15 21:35:17
 */
public interface ExampleTestMapper extends BaseMapper<ExampleTestEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  exampleTestEntity 实体类
     */
    Page<ExampleTestEntity> findPage(IPage<ExampleTestEntity> page,
                                      @Param("exampleTest") ExampleTestEntity exampleTestEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    ExampleTestEntity findExampleTestById(@Param("id") Long id);

    /**
     * 查询列表
     * @param exampleTestEntity 查询条件对象
     * @return
     */
    List<ExampleTestEntity> findList(ExampleTestEntity exampleTestEntity);

}
