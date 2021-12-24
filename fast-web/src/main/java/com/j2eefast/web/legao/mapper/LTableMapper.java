/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.mapper;

import com.j2eefast.web.legao.entity.LTableEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.j2eefast.web.legao.vo.LTableVo;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * l_table Mapper接口
 * @author: polaris_wang
 * @date 2021-12-21 15:12:50
 */
public interface LTableMapper extends BaseMapper<LTableEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  lTableEntity 实体类
     */
    Page<LTableVo> findPage(IPage<LTableEntity> page,
                            @Param("lTable") LTableEntity lTableEntity,
                            @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    LTableEntity findLTableById(@Param("id") Long id);

    /**
     * 查询列表
     * @param lTableEntity 查询条件对象
     * @return
     */
    List<LTableEntity> findList(LTableEntity lTableEntity);

    /**
     * 通过桌号查询数据
     * @param num
     * @return
     */
    @Select("SELECT count(id) FROM l_table WHERE del_flag !=0 AND table_num=#{num}")
    int findByNum(@Param("num") Integer num);

}
