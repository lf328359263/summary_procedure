package com.tuyou.dao;

import com.tuyou.util.Util;

public class VersionCount implements Basic{
	private String type;
	private String platformId ;
	private String gameId ;
	private String productId;
	private String version ;
	private String day;
	private String value;
    private StringBuffer sb;
    
    public VersionCount() {
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
		case "game_id":
			gameId = value;
			break;
		case "product_id":
			productId = value;
			break;
		case "version":
			version = value;
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
		sb.append(day).append(Util.SEPARATOR).append(platformId).append(Util.SEPARATOR).append(productId).append(Util.SEPARATOR).append(gameId)
				.append(Util.SEPARATOR).append(version);
		//day platformId productId gameId version
		return sb.toString();
	}
	@Override
	public void init() {
		sb.setLength(0);
		type = Util.DEFAULT_VALUE;
	    platformId = Util.DEFAULT_VALUE;
		gameId = Util.DEFAULT_VALUE;
	    productId = Util.DEFAULT_VALUE;
	    version = Util.DEFAULT_VALUE;
	}
}
