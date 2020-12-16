/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 */
package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.j2eefast.common.core.base.entity.BaseEntity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 文件实体表类
 * @author huanzhou
 * @date 2020-10-23
 */
@Data
@TableName("sys_file")
public class SysFilesEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件相对路径
     */
    private String filePath;

    /**
     * 文件存储的对象类型
     * 0 本地
     * 1 阿里
     * 2 腾讯
     * 3 七牛
     */
    private String ossType;

    /**
     * 文件归档分类
     * 0 上传组件
     * 1 图片剪切
     * 2 百度富文本
     * */
    private String classify;

    /**
     * 文件大小(字节)
     */
    private BigDecimal fileSize;

    
    @TableField(exist = false)
    private SysFileUploadEntity fileUpload;

    /**
     * 状态
     */
    @TableField(exist = false)
    private String status;
}
