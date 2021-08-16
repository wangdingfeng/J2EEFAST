/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.test;

import com.j2eefast.FastApplication;
import com.j2eefast.common.db.utils.DbUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ¢¢¢必看¢¢¢
 * 初始化导入核心数据库表结构与初始数据
 * 运行前须知:
 * 1. 请确认数据库环境没问题
 * 2. 确保已经创建用户创建了库
 * 3. 请确认admin模块下application-Dev.yml master 核心数据库, 配置无误.
 * 4. 以上没问题请执行运行此类
 */
@SpringBootTest(classes= FastApplication.class)
public class InitDb {

    @Test
    public void initDbMain(){
        DbUtil.initDb("master");
    }

}
