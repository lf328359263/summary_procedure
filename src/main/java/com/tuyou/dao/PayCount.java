package com.tuyou.dao;

import com.tuyou.util.Util;

public class PayCount implements Basic {
	private String type;
	private String platformId;
	private String gameId;
	private String productId;
	private String clientId;
	private String channelId;
	private String productNickname;
	private String eventId;
	private String payType;
	private String prodId;
	private String day;
	private String value;
	private StringBuffer sb;

	public PayCount() {
		this.sb = new StringBuffer();
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
		case "event_id":
			eventId = value;
			break;
		case "pay_type":
			payType = value;
			break;
		case "client_id":
			clientId = value;
			break;
		case "prod_id":
			prodId = value;
			break;
		case "product_nick_name":
			productNickname = value;
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
		sb.append(day).append(Util.SEPARATOR).append(platformId).append(Util.SEPARATOR).append(productId)
				.append(Util.SEPARATOR).append(gameId).append(Util.SEPARATOR).append(channelId).append(Util.SEPARATOR)
				.append(productNickname).append(Util.SEPARATOR).append(clientId).append(Util.SEPARATOR).append(eventId)
				.append(Util.SEPARATOR).append(payType).append(Util.SEPARATOR).append(prodId);
		// day platformId productId gameId clientId channelId productNickname
		// eventId payType prodId
		return sb.toString();
	}

	@Override
	public void init() {
		type = Util.DEFAULT_VALUE;
		platformId = Util.DEFAULT_VALUE;
		gameId = Util.DEFAULT_VALUE;
		productId = Util.DEFAULT_VALUE;
		clientId = Util.DEFAULT_VALUE;
		channelId = Util.DEFAULT_VALUE;
		productNickname = Util.DEFAULT_VALUE;
		eventId = Util.DEFAULT_VALUE;
		prodId = Util.DEFAULT_VALUE;
		payType = Util.DEFAULT_VALUE;
		sb.setLength(0);
	}
}
