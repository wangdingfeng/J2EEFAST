package com.j2eefast.generator.gen.mapper;

import com.j2eefast.generator.gen.entity.ExampleTestChildEntity;
import java.io.Serializable;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/**
 *
 * example_test_childDAO接口
 * @author: ZhouZhou
 * @date 2020-11-24 20:40
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

     ExampleTestChildEntity findExampleTestChildById(Serializable id);


     List<ExampleTestChildEntity> findList(ExampleTestChildEntity exampleTestChildEntity);

     /**删除相关方法  使用mybatis-plus集成的 **/
}
