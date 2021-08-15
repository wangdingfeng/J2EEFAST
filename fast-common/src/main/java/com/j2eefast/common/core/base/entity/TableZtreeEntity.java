package com.j2eefast.common.core.base.entity;

import lombok.Data;

/**
 * 表组件前端Table渲染 查询字段带树对象
 * @author huanzhou
 */
@Data
public class TableZtreeEntity {
    /**
     * 访问URL
     */
    private String url;

    /**
     * 指定获取树数据值
     */
    private String keyId;

    /**
     * 是否展开树
     */
    private boolean expandLevel;

    /**
     * 弹出是否出现清除按钮
     */
    private boolean isClear;

    /**
     * 控件名称
     */
    private String name;

    /**
     * 是否只能选中叶子节点
     */
    private boolean isSelectParent;

    private boolean checked;


}
