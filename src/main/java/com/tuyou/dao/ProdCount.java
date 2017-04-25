package com.tuyou.dao;

import com.tuyou.util.Util;

public class ProdCount implements Basic {
	private String type;
	private String platformId;
	private String gameId;
	private String productId;
	private String clientId;
	private String channelId;
	private String productNickname;
	private String payType;
	private String prodId;
	private String level;
	private String day;
	private String value;
	private StringBuffer sb;

	public ProdCount() {
		sb = new StringBuffer();
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
		case "pay_type":
			payType = value;
			break;
		case "prod_id":
			prodId = value;
			break;
		case "level":
			level = value;
			break;
		case "client_id":
			clientId = value;
			break;
		case "channel_id":
			channelId = value;
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
		StringBuffer sb = new StringBuffer();
		sb.append(day).append(Util.SEPARATOR).append(platformId).append(Util.SEPARATOR).append(productId)
				.append(Util.SEPARATOR).append(gameId).append(Util.SEPARATOR).append(channelId).append(Util.SEPARATOR)
				.append(productNickname).append(Util.SEPARATOR).append(clientId).append(Util.SEPARATOR).append(payType)
				.append(Util.SEPARATOR).append(prodId).append(Util.SEPARATOR).append(level);
		// day platformId productId gameId clientId channelId productNickname
		// payType prodId level
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
		channelId = Util.DEFAULT_VALUE;
		productNickname = Util.DEFAULT_VALUE;
		payType = Util.DEFAULT_VALUE;
		prodId = Util.DEFAULT_VALUE;
		level = Util.DEFAULT_VALUE;

	}

}
