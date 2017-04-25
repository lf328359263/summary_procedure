package com.tuyou.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;


import com.tuyou.dao.ResultBean;

import com.tuyou.util.Util;


public abstract class Sqls {
	
	public abstract Queue<ResultBean> getSqls(String sid, String date, boolean isPositiveSeq,String selectTable);
	
	public Queue<ResultBean> getResultBean(Queue<ResultBean> queue,ResultSet sqlset,String date,boolean isPositiveSeq) throws SQLException{
		ResultBean rb=new ResultBean();
		String sqlTmp=sqlset.getString(1);
    	if(sqlTmp.endsWith(";")){
    		sqlTmp=sqlTmp.replace(";","");
    	}
    	rb.setType(sqlset.getInt(2));
    	rb.setExe_sql(sqlTmp);
    	rb.setTable_name(sqlset.getString(3));
    	rb.setSid(sqlset.getInt(4));
    	rb.setDate(date);
    	rb.setExe_sql(sqlTmp);
    	if(rb.getTable_name().contains("retention")){
    		//logger.info("获取retention的执行sql");
    		queue=getRetentionResultBean(queue,rb,isPositiveSeq);
    		return queue;
    	}
    	if(sqlTmp.contains("$")){
    		rb.setDate(firstDays(sqlTmp,date));
    		rb.setExe_sql(setDays(sqlTmp,date));
    	}else{
    		rb.setExe_sql(putDate(sqlTmp,date));
    	}
    	queue.offer(rb);
    	return queue;
    	
	}
	public Queue<ResultBean> getRetentionResultBean(Queue<ResultBean> queue,ResultBean rb,boolean isPositiveSeq){
		String date=rb.getDate();
		String otherDay=null;
		int interval=0;
		boolean flag=rb.getExe_sql().endsWith("*");
		if(flag){
			rb.setExe_sql(rb.getExe_sql().substring(0, rb.getExe_sql().length()-1));
		}
		
		//int len=sqlTmp.split("[?]").length;
		for(int i=0;i<Util.RETENTION_TIME.length;i++){
			String sqlTmp=rb.getExe_sql();
			interval=Util.RETENTION_TIME[i];
			otherDay=Util.getDate(date,interval,isPositiveSeq);
			if(otherDay.equals("end")){
				break;
			}
			ResultBean tmp=new ResultBean();
			tmp.setSid(rb.getSid());
			tmp.setIntervals(interval);
			if(flag){
				if(isPositiveSeq){
					tmp.setDate(date);
					sqlTmp=setDay(sqlTmp, date);
					sqlTmp=setDay(sqlTmp, date);
					sqlTmp=setDay(sqlTmp, otherDay);
					}else{
					tmp.setDate(otherDay);
					sqlTmp=setDay(sqlTmp, otherDay);
					sqlTmp=setDay(sqlTmp, otherDay);
					sqlTmp=setDay(sqlTmp, date);
				}
			}else{
				if(isPositiveSeq){
					tmp.setDate(date);
					sqlTmp=setDay(sqlTmp, date);
					sqlTmp=setDay(sqlTmp, otherDay);
				}else{
					tmp.setDate(otherDay);
					sqlTmp=setDay(sqlTmp, otherDay);
					sqlTmp=setDay(sqlTmp, date);
				}
			}
	//		System.out.println(sqlTmp);
			tmp.setExe_sql(sqlTmp);
			tmp.setTable_name(rb.getTable_name());
			tmp.setType(rb.getType());
			queue.add(tmp);
		}
		//logger.info("获取执行retention 的queue！！！！");
		
		return queue;
	}
	private String setDay(String sql,String day){
		sql=sql.replaceFirst("\\?", "'"+day+"'");
		return sql;
	}
	public String setQMask(String sql,String value){
		sql=sql.replaceFirst("\\?",value);
		return sql;
	}
	public String putDate(String sql,String day){
		int len=sql.split("[?]").length;
		for(int i=0;i<len;i++){
			sql=setDay(sql,day);
		}
		return sql;
	}

	public  String setDays(String sql,String day){	
		if(sql.indexOf("$")>=0){
			int len1=sql.indexOf("$");
			sql=sql.replaceFirst("\\$", "");
		    int len2=sql.indexOf("$");
		    sql=sql.replaceFirst("\\$", "");
		    String intDay=sql.substring(len1, len2);
		    String sqlHead=sql.substring(0,len1);
		    String sqlTail=sql.substring(len2,sql.length());
		    int interval =Integer.parseInt(sql.substring(len1, len2));
		    String iday=Util.getDate("2016-01-01", day, interval);
		    return setDays(sqlHead+iday+sqlTail,day);
		}else{
			return sql;
		}
	}
	public String firstDays(String sql,String day){
		int len1=sql.indexOf("$");
		sql=sql.replaceFirst("\\$", "");
	    int len2=sql.indexOf("$");
	    sql=sql.replaceFirst("\\$", "");
	    int interval =Integer.parseInt(sql.substring(len1, len2));
	    String iday=Util.getDate("2016-01-01", day, interval);
	    return iday;
	}
//	public static void main(String[] args) {
//		String sql="select * from hh where day betwenn '$7$' and '$0$'";
//		System.out.println(setDays(sql,"2016-07-05"));
//	}
}
