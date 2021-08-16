package com.j2eefast.common.core.datasources;

public interface TableNameHandler {
	
	/**
	 * 生成动态表之后的SQL
	 * @author ZhouZhou
	 * @date 2021-07-30 19:50
	 * @param sql 原生SQL 
	 * @param tableName 新表名
	 * @param parameter 参数
	 * @return 
	 */
    String dynamicTableName(String sql, String tableName, Object parameter);
}
