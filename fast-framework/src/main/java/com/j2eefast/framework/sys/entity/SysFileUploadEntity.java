/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * sys_file_upload
 * @author: ZhouZhou
 * @date 2020-07-29 18:06
 */
@Data
@TableName("sys_file_upload")
public class SysFileUploadEntity {

      /** 主键 */
 
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

      /** 文件ID */
    private Long fileId;

      /** 文件名称 */
    private String fileName;

      /** 文件类型 0 文件 1图片 2其他 */
    private String fileType;

      /** 业务主键 */
    private Long bizId;

      /** 业务类型 */
    private String bizType;

  
      /** 删除标志 */
    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;
  

}
