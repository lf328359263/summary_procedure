package com.tuyou.engine;

import java.sql.Connection;

import com.tuyou.dao.ResultBean;

public interface CountByImpala {

	ResultBean exe(Connection hiveConn, ResultBean rb);
	
}
