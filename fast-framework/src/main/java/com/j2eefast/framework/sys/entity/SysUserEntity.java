/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.entity;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j2eefast.common.core.base.entity.BaseEntity;
import com.j2eefast.common.core.base.entity.annotaion.JsonListBaselgonre;
import com.j2eefast.common.core.base.entity.annotaion.JsonListFiledIgnore;
import com.j2eefast.common.core.enums.ConvertType;
import lombok.Data;

/**
 * <p>系统用户</p>
 *
 * @author: zhouzhou Emall:loveingowp@163.com
 * @date: 2019-03-20 16:40
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Data
@TableName("sys_user")
//设置头高
@HeadRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@JsonListBaselgonre(fileds = {"createTime"})
public class SysUserEntity extends BaseEntity {

	/**
	 * 用户ID
	 */
	@TableId
	@ExcelProperty("用户ID")
	private Long id;

	/**
	 * 用户名
	 */
	@NotBlank(message = "登陆账号不能为空")
	@ExcelProperty("账号")
	private String username;

	/**
	 * 用户名称
	 */
	@NotBlank(message = "姓名不能为空")
	@ExcelProperty("姓名")
	private String name;

	/**
	 * 密码
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	/**
	 * 密码安全级别（0初始 1很弱 2弱 3安全 4很安全）
	 */
	private String pwdSecurityLevel;

	/**
	 * 盐
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String salt;

	/**
	 * 邮箱
	 */
	@ExcelProperty("邮箱")
	@ColumnWidth(25)
	@JsonListFiledIgnore(exist = false, convert = ConvertType.EMAIL)
	private String email;

	/**
	 * 手机号
	 */
	@ExcelProperty("手机号码")
	@ColumnWidth(25)
	@JsonListFiledIgnore(exist = false, convert = ConvertType.PHONE)
	private String mobile;

	/**
	 * 状态 0：禁用 1：正常
	 */
	@ExcelProperty("状态")
	private String status;

	@TableLogic
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@TableField(value = "del_flag", fill = FieldFill.INSERT)
	private String delFlag;

	/**
	 * 用户头像
	 */
	private String avatar;

	/**
	 * 密码修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	@JsonListFiledIgnore
	private Date pwdUpdateDate;

	/**
	 * 用户对应卡号
	 */
	@JsonListFiledIgnore
	private String cardId;

	/**
	 * 用户是否在线
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private Integer loginStatus;

	/**
	 * 角色权限字符列表
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private List<String> roleKeys;


	/**
	 * 第三方登录源
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String source;

	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private List<Long> roleIdList;

	/**
	 * 地区ID列表
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private List<Long> deptIdList;

	/**
	 * 岗位ID列表
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private List<String> postCodes;

	/**
	 * 上次登陆时间
	 */
	@TableField(exist = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	@JsonListFiledIgnore
	private Date loginTime;
	
	/**
	 *上次登陆地点
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String loginLocation;

	/**
	 * 当前登陆时间
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String nowLoginTime;

	/**
	 * 当前登陆地点
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String nowLoginLocation;

	/**
	 * 角色名称
	 */
	@TableField(exist = false)
	@ExcelProperty("角色名称")
	@ColumnWidth(25)
	private String roleName;

	/**
	 * 岗位名称
	 */
	@TableField(exist = false)
	private String postName;

	/**
	 * 岗位编码
	 */
	@TableField(exist = false)
	private String postCode;

	/**
	 * 部门ID
	 */
	private Long deptId;

	/**
	 * 部门名称 --暂时不用
	 */
	@TableField(exist = false)
	private String deptName;

	/**
	 * 公司ID
	 */
	@NotNull(message = "公司不能为空")
	private Long compId;

	/**
	 * 公司名称
	 */
	@TableField(exist = false)
	@ExcelProperty("所属公司")
	//列宽
	@ColumnWidth(50)
	private String compName;

	/**
	 * 用户登录IP
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String ip;
	
	/**
	 * 用户会话ID
	 */
	@TableField(exist = false)
	@JsonListFiledIgnore
	private String sid;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String tenantId;

	@TableField(exist = false)
	private String tenantName;
}
