package com.tuyou.dao;

import com.tuyou.util.Util;

public class RoomCount implements Basic {
	private String type;
	private String platformId ;
	private String gameId ;
	private String productId;
	private String clientId ;
	private String eventId;
	private String chipType ;
	private String roomId ;
	private String day;
	private String value;
    private StringBuffer sb;
    
    public RoomCount() {
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
		case "client_id":
			clientId = value;
			break;
		case "event_id":
			eventId = value;
			break;
		case "chip_type":
			chipType = value;
			break;
		case "room_id":
			roomId = value;
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
				.append(Util.SEPARATOR).append(clientId).append(Util.SEPARATOR).append(eventId).append(Util.SEPARATOR)
				.append(chipType).append(Util.SEPARATOR).append(roomId);
		//day platformId  productId gameId clientId eventId chipType roomId
		return sb.toString();
	}
	@Override
	public void init() {
		sb.setLength(0);
		type = Util.DEFAULT_VALUE;
	    platformId = Util.DEFAULT_VALUE;
		gameId = Util.DEFAULT_VALUE;
	    productId = Util.DEFAULT_VALUE;
		clientId = Util.DEFAULT_VALUE;
		eventId = Util.DEFAULT_VALUE;
		chipType = Util.DEFAULT_VALUE;
		roomId = Util.DEFAULT_VALUE;
		
	}

}
