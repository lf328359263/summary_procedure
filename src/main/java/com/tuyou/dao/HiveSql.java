package com.tuyou.dao;

import java.util.List;
import java.util.Map;

public class HiveSql {
	
	private int sid;
	private String exe_sql;
	private String table_name;
	private String date;
	private int intervals;
	private int type;
	private List<Map<Integer,Object>> result;
	private String inserSql;
	public String getInserSql() {
		return inserSql;
	}
	public void setInserSql(String inserSql) {
		this.inserSql = inserSql;
	}
	public List<Map<Integer, Object>> getResult() {
		return result;
	}
	public void setResult(List<Map<Integer, Object>> result) {
		this.result = result;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getExe_sql() {
		return exe_sql;
	}
	public void setExe_sql(String exe_sql) {
		this.exe_sql = exe_sql;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getIntervals() {
		return intervals;
	}
	public void setIntervals(int intervals) {
		this.intervals = intervals;
	}
	
	
}
