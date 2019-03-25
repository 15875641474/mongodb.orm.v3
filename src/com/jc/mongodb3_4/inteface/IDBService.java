package com.jc.mongodb3_4.inteface;

/**
 * 数据库相关接口类
 * @author joncch
 *
 */
public interface IDBService {
	public void dbConnectionSuccess(String dbname);
	public void dbConnectionError(String dbname);
}
