package com.tuyou.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.impl.tools.sysinfo.Main;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

import org.apache.hadoop.hbase.client.Table;

import com.tuyou.dao.Base;
import com.tuyou.dao.Basic;
import com.tuyou.dao.ChipCount;
import com.tuyou.dao.ChipDistribution;
import com.tuyou.dao.LtvCount;
import com.tuyou.dao.PayCount;
import com.tuyou.dao.ProdCount;
import com.tuyou.dao.RedpacketCount;
import com.tuyou.dao.Retention;
import com.tuyou.dao.RoomCount;
import com.tuyou.dao.VersionCount;
import com.tuyou.util.HbaseUtil;
	

public class HbaseAction {

	private List<Put> puts;
	private  HTable table;
	public HbaseAction(){
		puts = new ArrayList<Put>();
	
	}
	public Basic getBasic(String tableName){
		switch (tableName) {
		case "bidata_result_base_count":
			return new Base();
		case "bidata_result_retention":
			return new Retention();
		case "bidata_result_pay_count":
			return new PayCount();
		case "bidata_result_version_count":
			return new VersionCount();
		case "bidata_result_chip_distribution":
			return new ChipDistribution();
		case "bidata_result_chip_count":
			return new ChipCount();
		case "bidata_result_room_count":
			return new RoomCount();
		case "bidata_result_prod_count":
			return new ProdCount();
		case "bidata_result_ltv_count":
			return new LtvCount();
		case "bidata_result_redpacket_count":
			return new RedpacketCount();
			
		}
		return null;
		
	}
	public void setBase(Basic Basic) {
		byte[] key=Basic.getKey().getBytes();
		byte[] type=Basic.getType().getBytes();
		byte[] value=Basic.getValue().getBytes();
		Put put=new Put(key);
		put.addColumn(HbaseUtil.DEFAULT_FAMILY, type, value);
		puts.add(put);
	}
	public List<Put> getPuts() {
		return puts;
	}
	public void setPuts(List<Put> puts) {
		this.puts = puts;
	}
	public HTable getTable() {
		return table;
	}
	public void setTable(HTable table) {
		this.table = table;
		//table=;
		table.setAutoFlush(false, false);;
	}
	public void commintPuts(){
	try {
		table.put(puts);
		table.flushCommits();
		table.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
    public static void main(String[] args) {
		HbaseAction bd=new HbaseAction();
		bd.setTable((HTable) HbaseUtil.getTable("bidata_result_base"));
		Base base=new Base();
		base.setAttribute("type", "2");
		base.setAttribute("value", "Hello");
		System.out.println(base.getKey());
		bd.setBase(base);
		bd.commintPuts();
	}
}
