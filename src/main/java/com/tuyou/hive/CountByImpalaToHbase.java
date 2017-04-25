package com.tuyou.hive;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;


import com.tuyou.dao.Basic;
import com.tuyou.dao.ResultBean;
import com.tuyou.engine.CountByImpala;
import com.tuyou.hbase.HbaseAction;
import com.tuyou.util.HbaseUtil;
import com.tuyou.util.Util;

public class CountByImpalaToHbase implements CountByImpala {
	private AtomicInteger fails;
	
	private Basic basic;
	private static Logger logger = Logger.getLogger(CountByImpalaToHbase.class);

	public CountByImpalaToHbase(AtomicInteger fails) {
		this.fails = fails;
	}
    

	public ResultBean exe(Connection hiveConn, ResultBean rb) {
	    HbaseAction ha=new HbaseAction();
		String tableName = "bidata_result_" + rb.getTable_name();
		if (tableName.indexOf("test") >= 0) {
			tableName = tableName.replaceAll("test", "count");
		}
		if (null == ha.getTable()) {
			ha.setTable((HTable) HbaseUtil.getTable(tableName));// 表一致

		} else if (!tableName.equals(ha.getTable().getName().toString())) {
			if (ha.getPuts().size() > 0) {
				logger.info("==========提交到Hbase===========");
				ha.commintPuts();
				ha.getPuts().clear();
			}
			ha.setTable((HTable) HbaseUtil.getTable(tableName));
		}
		basic = ha.getBasic(tableName);
		exeHiveSql(hiveConn, rb, ha, basic);
		if (ha.getPuts().size() > 0) {
			logger.info("==========提交到Hbase===========");
			ha.commintPuts();
			ha.getPuts().clear();
		}
		return null;
	}

	public void exeHiveSql(Connection hiveConn, ResultBean br, HbaseAction bd, Basic basic) {
		try {
			if (null == hiveConn || hiveConn.isClosed()) {
				hiveConn = Util.getHiveConn();
			}

			logger.info("执行sql：" + br.getExe_sql());
			ResultSet sqlset = hiveConn.createStatement().executeQuery(br.getExe_sql());
			ResultSetMetaData metaData;
			metaData = sqlset.getMetaData();
			int matelen = metaData.getColumnCount();
			while (sqlset.next()) {
				basic.init();
				for (int i = 1; i <= matelen; i++) {
					String name = metaData.getColumnName(i);
					String value = sqlset.getObject(i) + "";
					basic.setAttribute(name, value);
				}
				if (br.getTable_name().equals("retention")) {
					basic.setAttribute("intervals", br.getIntervals() + "");
				}
				basic.setAttribute("day", br.getDate());
				basic.setAttribute("type", br.getType() + "");
				bd.setBase(basic);
			}
			logger.info("******select for hbase成功******");
		} catch (ClassNotFoundException | SQLException e) {
			fails.incrementAndGet();
			Util.exeException(br.getSid() + "", e.getMessage(), br.getDate());
			logger.info("******select for hbase失败******");
			e.printStackTrace();
		} finally {
			Util.connClose(hiveConn);
		}

	}



}
