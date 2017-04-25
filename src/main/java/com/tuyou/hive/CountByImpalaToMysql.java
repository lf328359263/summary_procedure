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

import org.apache.log4j.Logger;

import com.tuyou.dao.ResultBean;
import com.tuyou.engine.CountByImpala;
import com.tuyou.util.Util;



public class CountByImpalaToMysql implements CountByImpala{
	 private	AtomicInteger fails;
		private static Logger logger = Logger.getLogger(CountByImpalaToMysql.class);
		public  CountByImpalaToMysql(AtomicInteger fails){
			this.fails=fails;
		}
		
		public ResultBean exe(Connection hiveConn, ResultBean rb) {
			try {
				if (null == hiveConn || hiveConn.isClosed()) {
					hiveConn = Util.getHiveConn();
				}
				logger.info("执行sql："+rb.getExe_sql());
				ResultSet sqlset = hiveConn.createStatement().executeQuery(rb.getExe_sql());
				
				Map<Integer,Object> re =null;
				List<Map<Integer,Object>> list=new ArrayList<Map<Integer,Object>>();
				ResultSetMetaData metaData;
				metaData = sqlset.getMetaData();
				int matelen = metaData.getColumnCount();
				StringBuffer sb = new StringBuffer("insert into "+rb.getTable_name()+" (");
				for (int i = 1; i <= matelen; i++) {
					if (i < matelen) {
						sb.append(metaData.getColumnName(i) + ",");
					} else if (i == metaData.getColumnCount()) {
						if (rb.getTable_name().contains("retention")) {
							sb.append(metaData.getColumnName(i) + ",type,day,sql_id,intervals) values(");
						} else {
							sb.append(metaData.getColumnName(i) + ",type,day,sql_id) values(");
						}
						for (int j = 1; j <= i; j++) {
							sb.append("?,");
						}
						if (rb.getTable_name().contains("retention")) {
							sb.append("?,?,?,?)");
						} else {
							sb.append("?,?,?)");
						}
					}
				}
				rb.setInserSql(sb.toString());
				while(sqlset.next()){
					re= new HashMap<Integer,Object>();
					for (int i = 1; i <= matelen; i++) {
						re.put(i, sqlset.getObject(i));
					}
					list.add(re);
				}
				rb.setResult(list);
				return rb;
			} catch (ClassNotFoundException |SQLException e) {
				fails.incrementAndGet();
				Util.exeException(rb.getSid()+"", e.getMessage(),rb.getDate());
				logger.info("******执行失败******");
				e.printStackTrace();
			}finally{
				Util.connClose(hiveConn);
			}
			return null;

		}

}
