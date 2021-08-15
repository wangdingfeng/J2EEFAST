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
 * sys_msg_comments
 * @author: ZhouZhou
 * @date 2021-02-28 11:00:00
 */
@Getter
@Setter
@TableName("sys_msg_comments")
public class SysMsgCommentsEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 消息编号 */
    @NotNull(message = "参数值不能为空")
    private Long msgId;

    /** 回复评论ID */
    @NotNull(message = "参数值不能为空")
    private Long replyId;

    /** 内容 */
    @NotBlank(message = "参数值不能为空")
    private String content;

    /** 发送者用户编码 */
    private Long userId;

    /** 发送者用户姓名 */
    private String userName;

    @TableField(exist = false)
    private String avatar;

    /** 评论时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date commentsDate;

    /** 是否回复 */
    private String isReply;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date createTime;
}
