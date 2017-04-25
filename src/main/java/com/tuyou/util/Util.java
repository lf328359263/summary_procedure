package com.tuyou.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Util {
	public static final String DEFAULT_VALUE = "-1";
	public static final String SEPARATOR = "_";
	private static Logger logger = Logger.getLogger(Util.class);
	public static final int RETENTION_TIME[] = { 1, 2, 3, 4, 5, 6, 14, 29, 44, 59, 74, 89 };

	public static Connection getMysqlConn() throws ClassNotFoundException, SQLException {
		 Class.forName("com.mysql.jdbc.Driver");
		 return
		 DriverManager.getConnection("jdbc:mysql://10.3.0.50:3307/result_test",
		 "tuyoo", "tuyougame");
//		Class.forName(pro1.getProperty("database.driver"));
//		return DriverManager.getConnection(pro1.getProperty("database.url"), pro1.getProperty("database.user"), pro1.getProperty("database.passwd"));
	}

//	public static Connection getMysqlConn() throws ClassNotFoundException, SQLException {
//		 Class.forName("com.mysql.jdbc.Driver");
//		 return
//		 DriverManager.getConnection("jdbc:mysql://10.3.0.51:3306/datacenter_db_new",
//		 "tuyou", "tuyoogame");
////		Class.forName(pro1.getProperty("database.driver"));
////		return DriverManager.getConnection(pro1.getProperty("database.url"), pro1.getProperty("database.user"), pro1.getProperty("database.passwd"));
//	}


	public static Connection getHiveConn() throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.hive.jdbc.HiveDriver");
		return DriverManager.getConnection("jdbc:hive2://10.3.0.51:21050/;auth=noSasl");
	}

	public static void refreshMata() {
		Connection hive = null;
		try {
			hive = Util.getHiveConn();
			hive.createStatement().execute("invalidate metadata");
			logger.info("刷新元数据成功");
		} catch (ClassNotFoundException | SQLException e1) {
			logger.info("刷新元数据成功失败");
			e1.printStackTrace();
		} finally {
			connClose(hive);
		}
	}

	public static void connClose(Connection conn) {
		try {
			if (null != conn)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static int update(Connection conn, String sql, List<String> values) {
		int re = 0;

		try {
			if (null == conn)
				conn = Util.getHiveConn();
			if (conn.isClosed())
				conn = Util.getHiveConn();
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception e) {
			Util.exeException(values.get(0), "重新执行错误\n" + e.getMessage(), values.get(1));
			re = 1;
			e.printStackTrace();
		}finally{
			connClose(conn);
		}
		return re;
	}

	public static void deleteDate(int sqlId, String table, String day) {

		String sql = "delete from " + table + "  where sql_id =" + sqlId + " and day='" + day + "'";
		logger.info("执行：" + sql);
		Connection conn = null;
		System.out.println(sql);
		try {
			conn = Util.getMysqlConn();
			Statement st;
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception e) {
			logger.info("******删除没有成功*****");
			e.printStackTrace();
		} finally {
			connClose(conn);
		}

		logger.info("删除成功！！！！");

	}

	public static void retentionDelete(String day, int sqlId, int interval) {
		String sql = "delete from retention  where sql_id =" + sqlId + " and intervals = " + interval + " and day='"
				+ day + "'";
		logger.info("执行：" + sql);
		Connection conn = null;
		// System.out.println(sql);
		try {
			conn = Util.getMysqlConn();
			Statement st;
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception e) {
			logger.info("******删除没有成功*****");
			e.printStackTrace();
		} finally {
			connClose(conn);
		}

		logger.info("删除成功！！！！");
	}

	public static PreparedStatement sqlPs(Connection conn, String sql, List<String> values) {
		PreparedStatement ps = null;
		try {
			int num = sql.split("\\?").length - 1;
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < num; i++) {
				ps.setObject(i + 1, values.get(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}

	public static String getDate(String origin, String startDay, int interval) {
		String endDay = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sf.parse(startDay.trim());
			Date originDay = sf.parse(origin);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -interval);
			date = cal.getTime();
			if (!originDay.equals(date) && originDay.before(date)) {
				endDay = sf.format(date);
			} else {
				endDay = "end";
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endDay;
	}

	public static String getDate(String origin, String startDay, int interval, boolean isPositiveSeq) {
		String endDay = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sf.parse(startDay.trim());
			Date originDay = null;
			if (origin.equals("")) {
				originDay = new Date();
			} else {
				originDay = sf.parse(origin);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			if (isPositiveSeq) {
				cal.add(Calendar.DATE, interval);
				date = cal.getTime();
				if (!originDay.equals(date) && originDay.after(date)) {
					endDay = sf.format(date);
				} else {
					endDay = "end";
				}
			} else {
				cal.add(Calendar.DATE, -interval);
				date = cal.getTime();
				if (!originDay.equals(date) && originDay.before(date)) {
					endDay = sf.format(date);
				} else {
					endDay = "end";
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endDay;
	}

	public static void exeException(String id, String message, String sqlDay) {

		try {
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String day = dateFormat.format(now);
			List<String> values = new ArrayList<String>();
			values.add(id);
			values.add(day);
			values.add(sqlDay);
			values.add("no");
			values.add(message);
			PreparedStatement ps;
			ps = sqlPs(getMysqlConn(),
					"insert into sql_exe_moni(exe_id,exe_time,sql_day,is_success,exception) values(?,?,?,?,?)", values);
			ps.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

//	public static void main(String[] args) {
//		System.out.println(getDate("2016-06-11", "2016-06-01", 9, true));
//	}

	public static String getDate(String date, int i, boolean isPositiveSeq) {
		if (isPositiveSeq) {
			return getDate("", date, i, isPositiveSeq);
		} else {
			return getDate("2016-05-31", date, i, isPositiveSeq);
		}

	}
}
