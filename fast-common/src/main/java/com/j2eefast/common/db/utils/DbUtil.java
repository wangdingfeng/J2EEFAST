/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.db.utils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.db.dao.sql.AllTableListSql;
import com.j2eefast.common.db.entity.SysDatabaseEntity;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.*;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * <p>多源数据工具类</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-20 11:34
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Slf4j
public class DbUtil{

	/**
	 * 查询某个数据库连接的所有表
	 *
	 * @author zhouzhou
	 * @Date 2019-05-04 20:30
	 */
	public static List<Map<String, Object>> selectTables(SysDatabaseEntity dbInfo) {
		List<Map<String, Object>> tables = new ArrayList<>();
		try {
			Class.forName(dbInfo.getJdbcDriver());
			Connection conn = DriverManager.getConnection(
					dbInfo.getJdbcUrl(), dbInfo.getUserName(), dbInfo.getPassword());

			//获取数据库名称
			String dbName = getDbName(dbInfo);

			//构造查询语句
			PreparedStatement preparedStatement = conn.prepareStatement(new AllTableListSql().getSql(dbInfo.getJdbcUrl()));

			//拼接设置数据库名称
			if (!dbInfo.getJdbcUrl().contains("sqlserver") && !dbInfo.getJdbcUrl().contains("postgresql")) {
				preparedStatement.setString(1, dbName);
			}

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				HashMap<String, Object> map = new HashMap<>();
				String tableName = resultSet.getString("tableName");
				String tableComment = resultSet.getString("tableComment");
				Date updateTime = resultSet.getDate("updateTime");
				Date createTime = resultSet.getDate("createTime");
				map.put("tableName", tableName);
				map.put("tableComment", tableComment);
				map.put("updateTime", updateTime);
				map.put("createTime", createTime);
				tables.add(map);
			}
			return tables;
		} catch (Exception ex) {
			log.error("查询所有表错误！", ex);
			throw new RxcException("查询所有表错误！","60001");
		}
	}

	/**
	 * 获取数据库名称
	 *
	 * @author zhouzhou
	 * @Date 2019-06-18 15:25
	 */
	private static String getDbName(SysDatabaseEntity dbInfo) {

		if (dbInfo.getJdbcUrl().contains("oracle")) {

			//如果是oracle，直接返回username
			return dbInfo.getUserName();

		} else if (dbInfo.getJdbcUrl().contains("postgresql")) {

			//postgresql，直接返回最后一个/后边的字符
			int first = dbInfo.getJdbcUrl().lastIndexOf("/") + 1;
			return dbInfo.getJdbcUrl().substring(first);

		} else if (dbInfo.getJdbcUrl().contains("sqlserver")) {

			//sqlserver，直接返回最后一个=后边的字符
			int first = dbInfo.getJdbcUrl().lastIndexOf("=") + 1;
			return dbInfo.getJdbcUrl().substring(first);

		} else {

			//mysql，返回/和?之间的字符
			String jdbcUrl = dbInfo.getJdbcUrl();
			int first = jdbcUrl.lastIndexOf("/") + 1;
			int last = jdbcUrl.indexOf("?");
			return jdbcUrl.substring(first, last);
		}
	}
	
	/**
	* @Title: getDbType 
	* @Description: 返回数据库的类型
	* @return  String
	* @author mfksn001@163.com
	* @Date: 2020年5月29日
	 */
	public static String getDbType(String jdbcUrl) {

		if (StringUtils.isBlank(jdbcUrl)) {
			return "";
		}
		if (jdbcUrl.contains("oracle")) {
			return "oracle";
		} else if (jdbcUrl.contains("postgresql")) {
			return "postgresql";
		} else if (jdbcUrl.contains("sqlserver")) {
			return "sqlserver";
		} else if (jdbcUrl.contains("mysql")) {
			return "mysql";
		}
		return "";
	}


	/**
	 * 初始化数据库
	 */
	public static void initDb(String dbType){
		String driverClassName = PropertiesUtils.getInstance().getProperty("spring.datasource."+dbType+".driverClassName");
		String url = PropertiesUtils.getInstance().getProperty("spring.datasource."+dbType+".url");
		String username = PropertiesUtils.getInstance().getProperty("spring.datasource."+dbType+".username");
		String password = PropertiesUtils.getInstance().getProperty("spring.datasource."+dbType+".password");
		String dbName = JdbcUtils.getDbType(url).getDb();
		Connection conn = null;
		try {
			Class.forName(driverClassName);
			conn = DriverManager.getConnection(
					url, username, password);
			try {
				log.info("---此功能主要用于初次创建数据库或者全部清理数据库时用,若为升级不能使用此操作,如果您此库有数据请做好备份.");
				log.info("---J2eeFAST 建立数据库表结构与数据库初始数据<<数据无价•慎重考虑>>.");
				log.info("---开始初始化数据库[请勿操作、耐心等待会],请稍后....");
				Thread.sleep(1000*3);
				FileSystemResource classPathResource = dbType.equals("master")?new FileSystemResource(FileUtil.file(ToolUtil.getLocalCoreDbPath(dbName,"initDb"))) :
						new FileSystemResource(FileUtil.file(ToolUtil.getLocalBpmDbPath(dbName,"initDb")));
				EncodedResource encodedResource = new EncodedResource(classPathResource, "UTF-8");
				ScriptUtils.executeSqlScript(conn, encodedResource);

				classPathResource = dbType.equals("master")? new FileSystemResource(FileUtil.file(ToolUtil.getLocalCoreDbPath(dbName,"test"))):
						new FileSystemResource(FileUtil.file(ToolUtil.getLocalBpmDbPath(dbName,"test")));
				encodedResource = new EncodedResource(classPathResource, "UTF-8");
				ScriptUtils.executeSqlScript(conn, encodedResource);
				log.info("---恭喜!!!!初始化数据库完成!!!");
				//---
			} catch (Exception e) {
				log.error("执行sql错误！", e);
				throw new RuntimeException("执行sql错误！");
			}finally {
				cn.hutool.db.DbUtil.close(conn);
			}
		} catch (Exception e) {
			throw new RxcException("连接数据库失败!请检查参数是否配置有误!");
		}
	}
}
