package com.j2eefast.common.core.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论树实体类
 * @author huanzhou
 */
@Data
public class CommentTree implements Serializable {

    private static final long 					serialVersionUID 					= 1L;

    /** 评论ID */
    private Long id;

    /** 回复ID*/
    private Long replyId;

    /** 回复内容*/
    private String content;

    /** 回复名字*/
    private String userName;

    /** 回复者编号*/
    private Long userId;

    private String avatar;

    /** 回复时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date commentsDate;

    /** 是否有回复*/
    private boolean isReply;

    /** 子元素*/
    private List<CommentTree> children;
}
