package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.j2eefast.common.core.base.entity.BaseEntity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 文件实体表类
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
