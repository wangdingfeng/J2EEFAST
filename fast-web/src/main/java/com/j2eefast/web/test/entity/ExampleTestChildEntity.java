/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * example_test_child
 * @author: ZhouZhou
 * @date 2021-08-15 21:35:18
 */
@Getter
@Setter
@TableName("example_test_child")
public class ExampleTestChildEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 注解 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 主表ID */
    private Long testId;

    /** 工作公司 */
    private String workCompany;

    /** 起始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date startTime;

    /** 辞职时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date endTime;

    /** 在职岗位 */
    private String jobs;

}
