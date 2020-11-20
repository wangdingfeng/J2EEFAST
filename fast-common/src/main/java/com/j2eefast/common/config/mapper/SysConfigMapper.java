/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.j2eefast.common.config.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>系统配置Mapper 接口</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-01 16:04
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
public interface SysConfigMapper extends BaseMapper<SysConfigEntity> {

    /**
     * 根据key，查询value
     * @param paramKey 参数Key
     * @return 参数对象
     */
    SysConfigEntity queryByKey(@Param("paramKey") String paramKey);

    /**
     * 根据key，更新value
     * @param paramKey 参数key
     * @param paramValue 参数值
     * @return int
     */
    int updateValueByKey(@Param("paramKey") String paramKey, @Param("paramValue") String paramValue);
}
