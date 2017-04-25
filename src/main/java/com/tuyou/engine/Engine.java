package com.tuyou.engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.tuyou.dao.ResultBean;
import com.tuyou.hbase.HbaseAction;
import com.tuyou.hive.CountByImpalaToHbase;
import com.tuyou.hive.CountByImpalaToMysql;
import com.tuyou.mysql.AllSql;
import com.tuyou.mysql.Delete;
import com.tuyou.mysql.MysqlInsertData;
import com.tuyou.mysql.PartSql;
import com.tuyou.mysql.Sqls;
import com.tuyou.util.Util;

public class Engine {

	private static Logger logger = Logger.getLogger(Engine.class);
	private String[] args;// 1：执行id( -1全部执行；-2 执行错误sql) 2：开始时间 3:顺序（1），倒序（-1）
							// 4:时间间隔 5:是否执行delete
	private String sid;
	private String date;
	private boolean isPositiveSeq;
	private boolean isDelete;
	private String selectTable;
	private Queue<ResultBean> sqlQueue;
	private static Queue<ResultBean> setQueue = new LinkedList<ResultBean>();
	private boolean isExe = true;
	private AtomicInteger aci = new AtomicInteger(0);
	private AtomicInteger conci = new AtomicInteger(0);
	private AtomicInteger fails = new AtomicInteger(0);
	private Sqls sqls;
	private int qsize;
	private String base;
	private InsertData insertData;
	private CountByImpala cbi;
    private Delete d;
	public Engine() {

	}

	public Engine(String[] args) {

		if (args != null && args.length == 7) {
			sid = args[1];
			date = args[2];
			isPositiveSeq = (args[3].equals("1") ? true : false);
			isDelete = (args[4].equals("1") ? true : false);
			if(isDelete){
				 d= new Delete();
			}
			selectTable = args[5];
			base = args[6];
			// System.out.println(args.length + sid + date + isPositiveSeq +
			// isDelete);
			logger.info("------------------执行id:" + sid + ";" + "开始时间:" + date + ";顺序执行：" + isPositiveSeq + ";是否删除："
					+ isDelete + ";sql来源表：" + selectTable + ";放入库：" + base + "---------");
		} else {
			System.out.println(args.length + sid + date + isPositiveSeq + isDelete + selectTable);
			System.out.println(
					"请输入7参数：1：执行id( -1全部执行；-2 执行错误sql,执行id list) 2：开始时间  3:顺序（1），倒序（-1） 4:是否执行delete 1：是，5：sql来源表,6:放入库");
			logger.info(
					"参数错误：1：执行id( -1全部执行；-2 执行错误sql，执行id list) 2：开始时间  3:顺序（1），倒序（-1） 4:是否执行delete 1：是 ；5：sql来源表，6:放入库");
			isExe = false;
		}
	}

	private synchronized ResultBean getResultBean() {
		return sqlQueue.poll();
	}

	private synchronized ResultBean getrbQueue() {
		return getSqlQueue().poll();
	}

	private synchronized Queue<ResultBean> getSqlQueue() {
		return setQueue;
	}

	private synchronized void setSqlQueue(ResultBean set) {
		this.setQueue.offer(set);
	}

	public void exe() {
		if (isExe) {
			long startMili = System.currentTimeMillis();
			if (sid.trim().equals("-1")) {
				sqls = new AllSql();
			}else{
				sqls = new PartSql();
			}
			
			switch (base) {
			case "hbase":
				cbi = new CountByImpalaToHbase(fails);
				break;
			default:
				insertData = new MysqlInsertData();
				cbi = new CountByImpalaToMysql(fails);
			}
			sqlQueue = sqls.getSqls(sid, date, isPositiveSeq, selectTable);
			qsize = sqlQueue.size();
			logger.info("获取执行sql的列队的长度：" + qsize);
			Util.refreshMata();
			startCountToInput();
			double ends = (System.currentTimeMillis() - startMili) / 1000.0;
			logger.info("程序完成 共用：" + ends + "s!!!!");
		}
	}

	public void startCountToInput() {
		Runnable p = new Producter();
		Runnable c = new Consumer();
		List<Thread> list = new ArrayList<Thread>();
		int plen = 1;
		int clen = 1;
		if (qsize > 10) {
			plen = 10;
			clen = 23;
		}
		for (int i = 0; i < plen; i++) {
			Thread th = new Thread(p, "productor-" + i);
			list.add(th);
			th.start();
		}
		if (!base.equals("hbase")) {
			for (int i = 0; i < clen; i++) {
				Thread th = new Thread(c, "comsumer-" + i);
				list.add(th);
				th.start();
			}
		}
		try {
			for (Thread my : list) {
				my.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class Producter implements Runnable {

		@Override
		public void run() {
			logger.info("执行线程name：" + Thread.currentThread().getName());
			ResultBean rb = null;
			Connection hiveConn = null;
			try {
				hiveConn = Util.getHiveConn();
				while ((rb = getResultBean()) != null) {
					if(base.equals("mysql")&&isDelete){
							d.deleteSql(rb);
					}
					logger.info("------共要执行" + qsize + "个sql执行;插入sqlid:" + rb.getSid() +";已经执行：" + aci.incrementAndGet() + "个------");
					setSqlQueue(cbi.exe(hiveConn, rb));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {

				e.printStackTrace();
			} finally {
				try {
					hiveConn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class Consumer implements Runnable {
		@Override
		public void run() {

			ResultBean rb = null;
			while (true) {
				if ((rb = getrbQueue()) != null) {
					insertData.insertData(rb);
					logger.info("=====共要插入" + qsize + "个sql执行;插入sqlid:" + rb.getSid() + ";已经插入：" + getInc() + "个;失败"
							+ fails.get() + "个===");
				} else {
					if (qsize <= (conci.get() + fails.get())) {
						break;
					}
				}
			}

		}

	}

	private synchronized int getInc() {
		return conci.incrementAndGet();
	}
}
