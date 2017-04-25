package com.tuyou.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.tuyou.dao.ResultBean;
import com.tuyou.util.Util;

public class Delete {
	private final static String RETENTION_SQL = "delete from ?  where sql_id = ? and day=? and intervals = ";
	private final static String NORMAL_SQL = "delete from ?  where sql_id = ? and day= ? ";
	private static Logger logger = Logger.getLogger(Delete.class);
	private Map<String, HashMap<String, String>> retention;

	public Delete() {
		retention = new HashMap<String, HashMap<String, String>>();
	}

	public void deleteSql(ResultBean rb) {
		Connection conn = null;
		String sql = null;
		try {
			conn = Util.getMysqlConn();
			if (rb.getTable_name().equals("retention")) {
				logger.info("删除retention的sql_id:" + rb.getSid() + ";日期" + rb.getDate() + ";interval is "
						+ rb.getIntervals());
				sql = RETENTION_SQL + rb.getIntervals();
			} else {
				logger.info("删除普通sql_id:" + rb.getSid() + ";日期" + rb.getDate());
				sql = NORMAL_SQL;
			}
			sql = sql.replaceFirst("\\?", rb.getTable_name()).replaceFirst("\\?", rb.getSid() + "")
					.replaceFirst("\\?", "'" + rb.getDate() + "'").replaceFirst("\\?", rb.getType() + "");
			logger.info("执行删除sql:" + sql);
			conn.createStatement().executeUpdate(sql);
			logger.info("-----------执行删除成功！--------------");
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("----------执行删除失败！---------------");
			e.printStackTrace();
		} finally {
			Util.connClose(conn);
		}
	}

	public void deleteSql(Queue<ResultBean> sqlQueue) {
		Connection conn = null;
		String sql = null;
		try {
			conn = Util.getMysqlConn();
			conn.setAutoCommit(false);
			Statement statement =  conn.createStatement();
			for (ResultBean rb : sqlQueue) {
				if (rb.getTable_name().equals("retention")) {
					logger.info("删除retention的sql_id:" + rb.getSid() + ";日期" + rb.getDate() + ";interval is "
							+ rb.getIntervals());
					sql = RETENTION_SQL + rb.getIntervals();
				} else {
					logger.info("删除普通sql_id:" + rb.getSid() + ";日期" + rb.getDate());
					sql = NORMAL_SQL;
				}
				sql = sql.replaceFirst("\\?", rb.getTable_name()).replaceFirst("\\?", rb.getSid() + "")
						.replaceFirst("\\?", "'" + rb.getDate() + "'").replaceFirst("\\?", rb.getType() + "");
				logger.info("添加删除sql到执行过程:" + sql);
				statement.addBatch(sql);
			}
			statement.executeBatch();
			statement.clearBatch();
			conn.commit();
			logger.info("-----------执行删除成功！--------------");
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("----------执行删除失败！---------------");
			e.printStackTrace();
		} finally {
			Util.connClose(conn);
		}
	}

}
