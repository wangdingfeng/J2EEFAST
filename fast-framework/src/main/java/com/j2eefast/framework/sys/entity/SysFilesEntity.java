package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.j2eefast.common.core.base.entity.BaseEntity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 文件实体表类
 */
@Data
@TableName("sys_file")
public class SysFilesEntity  {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String fileName;

    private String fileMd5;

    private String filePath;

    /*文件归档分类 0 上传组件 1 图片剪切 2 百度富文本*/
    private String classify;

    private BigDecimal fileSize;

    
    @TableField(exist = false)
    private SysFileUploadEntity fileUpload;
    
    @TableField(exist = false)
    private String status;
    
   /* 
    *//** 创建者 *//*
    @TableField(exist = false)
    private String createBy;

    *//** 创建时间 *//*
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date createTime;*/
}
