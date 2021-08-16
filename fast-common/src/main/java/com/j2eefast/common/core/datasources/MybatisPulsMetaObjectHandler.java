/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.datasources;

import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * 自定义SQL填充器
 * @author zhouzhou
 * @date 2020-03-11 16:10
 */
public class MybatisPulsMetaObjectHandler  implements MetaObjectHandler {

    @Value("#{ @environment['mybatis-plus.global-config.db-config.logic-not-delete-value'] ?: '0' }")
    private String logicDeleteValue;

	/**
	 * Mybatis-Plus 自带save方法自动填充默认值
	 */
	@Override
	public void insertFill(MetaObject metaObject) {
		//检查逻辑删除字段
		Object delFlag = getFieldValByName(getDeleteFlagFieldName(), metaObject);
        if (ToolUtil.isEmpty(delFlag)) {
            setFieldValByName(getDeleteFlagFieldName(), getDefaultDelFlagValue(), metaObject);
        }
        //检查创建时间
        Object createTime = getFieldValByName(getCreateTimeFieldName(), metaObject);
        if (ToolUtil.isEmpty(createTime)) {
            setFieldValByName(getCreateTimeFieldName(), new Date(), metaObject);
        }
        //检查创建者
        Object createUser = getFieldValByName(getCreateByFieldName(), metaObject);
        if (ToolUtil.isEmpty(createUser)) {

            //获取当前登录用户
            Object accountId = getUserUniqueId();

            setFieldValByName(getCreateByFieldName(), accountId, metaObject);
        }
	}

	/**
	 *  Mybatis-Plus 自带update方法自动填充默认值
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		
		Object updateTime = getFieldValByName(getUpdateTimeFieldName(),metaObject);
		if(ToolUtil.isEmpty(updateTime)) {
			setFieldValByName(getUpdateTimeFieldName(), new Date(), metaObject);
		}
		
        Object updateBy = getFieldValByName(getUpdateByFiledName(), metaObject);
        if (ToolUtil.isEmpty(updateBy)) {

            //获取当前登录用户
            Object accountId = getUserUniqueId();

            setFieldValByName(getUpdateByFiledName(), accountId, metaObject);
        }
	}
	
	/**
     * 获取逻辑删除字段的名称（非数据库中字段名称）
     */
    protected String getDeleteFlagFieldName() {
        return "delFlag";
    }

    /**
     * 获取逻辑删除字段的默认值
     */
    protected Object getDefaultDelFlagValue() {
        return logicDeleteValue ;
    }
    
    /**
     * 获取创建时间字段的名称（非数据库中字段名称）
     */
    protected String getCreateTimeFieldName() {
        return "createTime";
    }

    /**
     * 获取更新时间字段的名称（非数据库中字段名称）
     */
    protected String getUpdateTimeFieldName() {
        return "updateTime";
    }
    
    /**
     * 更新用户
     */
    protected String getUpdateByFiledName() {
    	return "updateBy";
    }
    
    /**
     * 创建用户
     */
    protected String getCreateByFieldName() {
        return "createBy";
    }
    
    /**
     * 获取用户唯一id（注意默认获取的用户唯一id为空，如果想填写则需要继承本类）
     */
    protected Object getUserUniqueId() {
        return "";
    }
	
}
