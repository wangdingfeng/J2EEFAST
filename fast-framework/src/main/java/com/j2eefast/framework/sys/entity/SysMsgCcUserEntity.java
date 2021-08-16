/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * sys_msg_cc_user
 * @author: ZhouZhou
 * @date 2021-02-22 21:19:11
 */
@Getter
@Setter
@TableName("sys_msg_cc_user")
public class SysMsgCcUserEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 消息编号 */
    @NotNull(message = "参数值不能为空")
    private Long msgId;

    /** 用户编码 */
    @NotBlank(message = "参数值不能为空")
    private Long userId;

    /** 用户姓名 */
    @NotBlank(message = "参数值不能为空")
    private String userName;

    /** 是否阅读 */
    @NotBlank(message = "参数值不能为空")
    private String isRead;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date readDate;

}
