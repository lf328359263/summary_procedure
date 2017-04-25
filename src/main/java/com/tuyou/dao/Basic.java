package com.tuyou.dao;


public interface Basic {

	public void setAttribute(String name, String value);

	public String getType();

	public String getValue();

	public String getKey();
	
	public void init();

}
