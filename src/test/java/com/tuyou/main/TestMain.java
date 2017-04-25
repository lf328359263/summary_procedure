package com.tuyou.main;

import java.util.HashMap;
import java.util.Map;

import com.tuyou.engine.Engine;
import com.tuyou.util.Util;

public class TestMain {
	public static void main(String[] args) {
		String day;
		//String arg[] = { "", "1", "2016-06-01", "-1", "-1", "hive_sql", "hbase" };
//		String arg[] = { "", "-1", "2016-09-26", "-1", "-1", "logic_sql.hive_sql_pay", "mysql" };
		String arg[] = { "", "631", "2016-10-16", "-1", "-1", "hive_sql_test", "mysql" };
		//forExe(arg,arg[2],184);
		exe(arg);
//		Map<String,Object>l =new HashMap<String,Object>();
//		l.put("d", "d");
//		System.out.println(l.get("d"));

	}

	public static void exe(String arg[]) {
		Engine template = new Engine(arg);
		template.exe();
	}
	public static void forExe(String arg[],String endDay,int inteval){
		
		for (int i = 0; i < inteval; i++) {
			String tmp = Util.getDate("2016-01-00",endDay, i);
			System.out.println(tmp);
			if(tmp.equals("end")){
				break;
			}
			arg[2] = tmp;
			exe(arg);
		}
	}
}
