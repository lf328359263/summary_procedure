package com.tuyou.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;


public class HbaseUtil {
	private static final String QUORUM = "HY-10-3-0-49,HY-10-3-0-50,HY-10-3-0-51";
    private static final String CLIENTPORT = "2181";
    public static final byte[] DEFAULT_FAMILY="cf".getBytes();
//    private static final String TABLENAME = "rd_ns:itable";
    private static Configuration conf = null;
   
	private static Connection conn = null;
	static {
		conf=HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", CLIENTPORT);
		conf.set("hbase.zookeeper.quorum", QUORUM);
		try {
			conn= ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Table getTable(String tablename ){
		Table table=null;
		try {
			 table = conn.getTable(TableName.valueOf(tablename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table;
	}
	
}
