package com.tuyou.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.tuyou.dao.ResultBean;
import com.tuyou.engine.InsertData;
import com.tuyou.util.Util;

public class MysqlInsertData implements InsertData {
	private static Logger logger = Logger.getLogger(MysqlInsertData.class);

	@Override
	public void insertData(ResultBean rb) {
		PreparedStatement statement = null;
		Connection mysqlConn = null;
		List<Map<Integer, Object>> list = rb.getResult();
		try {
			mysqlConn = Util.getMysqlConn();
			mysqlConn.setAutoCommit(false);
			statement = mysqlConn.prepareStatement(rb.getInserSql());
			for (Map<Integer, Object> map : list) {
				for (int i = 1; i <= map.size(); i++) {
					statement.setObject(i, map.get(i));
				}
				statement.setObject(map.size() + 1, rb.getType());
				statement.setObject(map.size() + 2, rb.getDate());
				statement.setObject(map.size() + 3, rb.getSid());
				if (rb.getTable_name().contains("retention")) {
					statement.setObject(map.size() + 4, rb.getIntervals());
				}

				statement.addBatch();
		
			}
			logger.info("插入数据 sql：" + rb.getInserSql());
			statement.executeBatch();
			statement.clearBatch();
			mysqlConn.commit();
			logger.info("******插入数据库成功******");
		} catch (SQLException | ClassNotFoundException e) {
			logger.info("=====执行sqlid:" + rb.getSid() + "时失败");
			logger.info("******$插入数据库失败$******");
			e.printStackTrace();
		} finally {
			Util.connClose(mysqlConn);
		}
	}
	

}
