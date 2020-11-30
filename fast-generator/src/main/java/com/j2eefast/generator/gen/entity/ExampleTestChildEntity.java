package com.j2eefast.generator.gen.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * example_test_child
 * @author: ZhouZhou
 * @date 2020-11-24 20:40
 */
@Data
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
    private String jobs;}
