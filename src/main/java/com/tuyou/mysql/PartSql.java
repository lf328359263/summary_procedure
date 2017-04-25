package com.tuyou.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.tuyou.dao.ResultBean;
import com.tuyou.util.Util;



public class PartSql  extends Sqls{
	
	private static Logger logger = Logger.getLogger(PartSql.class);
	private  static String partSql="select exe_sql,type,table_name,id from ? where id=";

	@Override
	public Queue<ResultBean> getSqls(String sid, String date, boolean isPositiveSeq, String selectTable) {
		String[] ids=sid.split(",");
		Statement stmt1;
		ResultSet sqlset=null;
		Queue<ResultBean> queue= new LinkedList<ResultBean>();
		Connection conn=null;
		partSql=setQMask(partSql, selectTable);
		try {
			    conn= Util.getMysqlConn();
			    for(String id:ids){
			    	stmt1 = conn.createStatement();
				    sqlset = stmt1.executeQuery(partSql+id);
				    if(sqlset.next()){
				    	queue=getResultBean(queue, sqlset, date, isPositiveSeq);
				    }
			    }
		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			Util.connClose(conn);
		}
		
		return queue;
	}
	
}
