package com.tuyou.dao;

import com.tuyou.util.Util;

public class ChipDistribution implements Basic {
	private String type;
	private String platformId ;

	private String productId;
	private String level ;

	private String day;
	private String value;
    private StringBuffer sb;
    
    public ChipDistribution() {
		sb=new StringBuffer();
	}
	
	@Override
	public void setAttribute(String name, String value) {
		switch (name) {
		case "type":
			type = value;
			break;
		case "platform_id":
			platformId = value;
			break;
		case "level":
			level = value;
			break;
		case "product_id":
			productId = value;
			break;
		case "value":
			this.value = value;
			break;
		case "day":
			this.day = value;
			break;
		default:
			break;
		}
	}
	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public String getValue() {
		return this.value;
	}
	@Override
	public String getKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(day).append(Util.SEPARATOR).append(platformId).append(Util.SEPARATOR).append(productId).append(Util.SEPARATOR).append(level);
		//day platformId productId level
		return sb.toString();
	}
	@Override
	public void init() {
		sb.setLength(0);
		type = Util.DEFAULT_VALUE;
	    platformId = Util.DEFAULT_VALUE;
	    productId = Util.DEFAULT_VALUE;
	    level = Util.DEFAULT_VALUE;
		
	}

}
