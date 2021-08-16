/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * sys_msg_push
 * @author: ZhouZhou
 * @date 2021-02-21 22:43:07
 */
@Getter
@Setter
@TableName("sys_msg_push")
public class SysMsgPushEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 消息标题 */
    @NotBlank(message = "参数值不能为空")
    private String msgTitle;

    /** 消息内容 */
    @NotBlank(message = "参数值不能为空")
    private String msgContent;

    /** 消息等级 */
    @NotBlank(message = "参数值不能为空")
    private String msgLevel;

    /** 是否开放评论 */
    @NotBlank(message = "参数值不能为空")
    private String isComments;

    /** 发送者用户编码 */
    private Long sendUserId;

    /** 发送用户 */
    private String sendUserName;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date pushDate;

    @TableField(exist = false)
    private Long[] userCode;

    @TableField(exist = false)
    private Long userId;

    @TableField(exist = false)
    private String isRead;

    @TableField(exist = false)
    private Long[] ccUserCode;

    @TableField(exist = false)
    private String[] userCodeName;

    @TableField(exist = false)
    private String[] ccUserCodeName;

}
