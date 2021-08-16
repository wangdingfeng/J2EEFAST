/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.mapper;

import com.j2eefast.web.test.entity.ExampleTestChildEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * example_test_child Mapper接口
 * @author: ZhouZhou
 * @date 2021-08-16 20:41:57
 */
public interface ExampleTestChildMapper extends BaseMapper<ExampleTestChildEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  exampleTestChildEntity 实体类
     */
    Page<ExampleTestChildEntity> findPage(IPage<ExampleTestChildEntity> page,
                                      @Param("exampleTestChild") ExampleTestChildEntity exampleTestChildEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    ExampleTestChildEntity findExampleTestChildById(@Param("id") Long id);

    /**
     * 查询列表
     * @param exampleTestChildEntity 查询条件对象
     * @return
     */
    List<ExampleTestChildEntity> findList(ExampleTestChildEntity exampleTestChildEntity);

}
