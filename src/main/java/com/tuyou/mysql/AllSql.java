package com.tuyou.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

import com.tuyou.dao.ResultBean;
import com.tuyou.util.Util;

public class AllSql  extends Sqls{
	public static String allSql="select exe_sql,type,table_name,id,relate_table from ? where is_exe=1";
	@Override
	public Queue<ResultBean> getSqls(String sid, String date, boolean isPositiveSeq, String selectTable) {
		Statement stmt1;
		ResultSet sqlset=null;
		Queue<ResultBean> queue= new LinkedList<ResultBean>();
		Connection conn=null;
		allSql=setQMask(allSql, selectTable);
		try {
			    conn= Util.getMysqlConn(); 
				stmt1 = conn.createStatement();
			    sqlset = stmt1.executeQuery(allSql);
			    while(sqlset.next()){
			    	queue=getResultBean(queue, sqlset, date, isPositiveSeq);
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
