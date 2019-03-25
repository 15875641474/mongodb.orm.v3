package com.jc.mongodb3_4.observice;

import com.jc.mongodb3_4.core.DBHelp;
import com.jc.mongodb3_4.inteface.IDBService;

/**
 * 数据库相关服务
 * 单例
 * @author joncch
 *
 */
public class DBOBService extends OBService<IDBService> {
	
	private static DBOBService dbobService;
	private DBOBService(){}
	
	/**
	 * 实例化
	 * @return
	 */
	public static DBOBService getInstandce(){
		if(dbobService == null){
			dbobService = new DBOBService();
		}
		return dbobService;
	}
	
	public static void close(){
		dbobService.unRegisterAll();
		dbobService = null;
	}
	
	/**
	 * 数据库链接成功
	 */
	public synchronized void dbConnectionSuccess(){
		getObservicelist().forEach(o -> o.dbConnectionSuccess(DBHelp.DB_NAME));
	}
	
	/**
	 * 数据库链接失败
	 */
	public synchronized void dbConnectionError(){
		getObservicelist().forEach(o -> o.dbConnectionError(DBHelp.DB_NAME));
	}
}
