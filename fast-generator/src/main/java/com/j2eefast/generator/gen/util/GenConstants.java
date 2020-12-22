package com.j2eefast.generator.gen.util;

public class GenConstants{
    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud";

    /** 单表（查） */
    public static final String TPL_R = "r";

    /** 单表（增） */
    public static final String TPL_U = "u";


    /** 单表（增） */
    public static final String TPL_D = "d";

    /** 单表（增） */
    public static final String TPL_C = "c";

    public static final String TARGET = "tab";

    /** 树表（增删改查） */
    public static final String TPL_TREE = "tree";

    /**主子表-子表*/
    public static final String  TPL_MASTER= "master";

    public static final String  TPL_CHILD= "child";


    public static final String SERVICE = "service";

    public static final String ENTITY = "entity";

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode";

    /** 树父编码字段 */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /** 树名称字段 */
    public static final String TREE_NAME = "treeName";

    /** 数据库字符串类型 */
    public static final String[] COLUMNTYPE_STR = { "bpchar","char", "varchar", "narchar", "varchar2", "tinytext", "text",
            "mediumtext", "longtext" ,"nvarchar","nvarchar2","nchar"};

    /** 数据库时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "datetime","datetime2", "time", "date", "timestamp","timestamptz" };

    /** 数据库数字类型 */
    public static final String[] COLUMNTYPE_NUMBER = { "int4","int2","int8","tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bigint", "float", "float", "double", "decimal" };

    /** 页面不需要编辑字段 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag", "update_by","update_time"};

    /** 页面不需要显示的列表字段 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" };

    /** 页面不需要查询字段 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark" };

    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** Tree基类字段 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors" };

    /** 文本框 */
    public static final String HTML_INPUT = "input";

    /** 文本域 */
    public static final String HTML_TEXTAREA = "textarea";

    /** 下拉框 */
    public static final String HTML_SELECT = "select";

    /** 单选框 */
    public static final String HTML_RADIO = "radio";

    /** 复选框 */
    public static final String HTML_CHECKBOX = "checkbox";

    /** 日期控件 */
    public static final String HTML_DATETIME = "datetime";

    /** 字符串类型 */
    public static final String TYPE_STRING = "String";

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer";

    /** 长整型 */
    public static final String TYPE_LONG = "Long";

    /** 浮点型 */
    public static final String TYPE_DOUBLE = "Double";

    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 时间类型 */
    public static final String TYPE_DATE = "Date";

    /** 模糊查询 */
    public static final String QUERY_LIKE = "LIKE";

    /** 需要 */
    public static final String REQUIRE = "1";
}
