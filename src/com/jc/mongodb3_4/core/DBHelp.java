package com.jc.mongodb3_4.core;

import java.util.ArrayList;
import java.util.List;

import com.jc.mongodb3_4.inteface.IBaseService;
import com.jc.mongodb3_4.inteface.IDBService;
import com.jc.mongodb3_4.observice.DBOBService;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase; 

/**
 * 数据库通道链接类
 * 单例
 * 如果想获取数据库初始化成功，失败状态，可以实现IDBService接口，并通过DBOBService.getInstandce().regiser来注册通知
 * @author joncch
 *
 */
public class DBHelp implements IBaseService {
	/**
	 * 数据库名称
	 */
	public static String DB_NAME = "jonchdb";
	public static String DB_IP = "ds159747.mlab.com";
	public static int DB_PORT = 59747;
	public static String USER_ACCOUNT = "root";
	public static String USER_PWD = "32121321";
	private static String TAG = DBHelp.class.getSimpleName();
	private static DBHelp mongoDatabase;
	private MongoDatabase database = null;
	private MongoClient mongoClient;
	
	/**
	 * 启用数据库
	 */
	public static void enableDB(){
		LogUtil.UPDATE_MLAB_DATABASE = true;
	}
	/**
	 * 禁用数据库
	 */
	public static void disableDB(){
		LogUtil.UPDATE_MLAB_DATABASE = true;
	}
	/**
	 * 监听事件
	 * @param idbService
	 */
	public void setInitListen(IDBService idbService){
		DBOBService.getInstandce().register(idbService);
	}
	
	/**
	 * 初始化数据连接信息
	 * @param DB_NAME 数据库名称
	 * @param DB_IP 链接IP地址
	 * @param DB_PORT 端口
	 * @param USER_ACCOUNT 账号
	 * @param USER_PWD 密码
	 */
	public static void initDBConnection(String DB_NAME,String DB_IP,int DB_PORT,String USER_ACCOUNT,String USER_PWD){
		DBHelp.DB_NAME = DB_NAME;
		DBHelp.DB_NAME = DB_NAME;
		DBHelp.DB_IP = DB_IP;
		DBHelp.DB_PORT = DB_PORT;
		DBHelp.USER_ACCOUNT = USER_ACCOUNT;
		DBHelp.USER_PWD = USER_PWD;
	}

	private DBHelp() {
		
	}

	public synchronized static DBHelp getInstand() {
		if (mongoDatabase == null) {
			mongoDatabase = new DBHelp();
		}
		return mongoDatabase;
	}

	/**
	 * 获得默认数据库
	 * 
	 * @return
	 */
	public MongoDatabase getdb() {
		return database;
	}

	
	/**
	 * 卸载
	 */
	public void unInit(){
		mongoDatabase.database = null;
		mongoDatabase.mongoClient.close();
		mongoDatabase.mongoClient = null;
		database = null;
	}

	/**
	 * 初始化数据库信息
	 */
	public void initDb(){
		if(isBlank(DB_NAME)){
			throw new NullPointerException("DB_NAME is empty,you can call DBHelp.initDBConnection to init ");
		}
		if(isBlank(DB_IP)){
			throw new NullPointerException("DB_IP is empty,you can call DBHelp.initDBConnection to init ");
		}
		if(DB_PORT < 0){
			throw new NullPointerException("DB_PORT is empty,you can call DBHelp.initDBConnection to init ");
		}
		if(isBlank(USER_ACCOUNT)){
			throw new NullPointerException("USER_ACCOUNT is empty,you can call DBHelp.initDBConnection to init ");
		}
		if(isBlank(USER_PWD)){
			throw new NullPointerException("USER_PWD is empty,you can call DBHelp.initDBConnection to init ");
		}
		initDb(DB_NAME, DB_IP, DB_PORT, USER_ACCOUNT, USER_PWD);
	}
	
	private boolean isBlank(String vue){
		if(vue == null || vue.isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * 初始化数据库信息
	 * @param dbname
	 * @param dbip
	 * @param dbport
	 * @param account
	 * @param pwd
	 */
	public void initDb(String dbname,String dbip,int dbport,String account,String pwd) {
		try {
			
			mongoDatabase.DB_NAME = dbname;
			mongoDatabase.DB_IP = dbip;
			mongoDatabase.DB_PORT = dbport;
			mongoDatabase.USER_ACCOUNT = account;
			mongoDatabase.USER_PWD = pwd;
			
			List<ServerAddress> addressLists = new ArrayList<ServerAddress>();
			List<MongoCredential> credentialsLists = new ArrayList<MongoCredential>();
			ServerAddress serverAddress = new ServerAddress(DB_IP, DB_PORT);
			addressLists.add(serverAddress);
			MongoCredential credentials = MongoCredential.createCredential(USER_ACCOUNT, DB_NAME,
					USER_PWD.toCharArray());
			credentialsLists.add(credentials);
			mongoClient = new MongoClient(addressLists, credentialsLists);
			database = mongoClient.getDatabase(DB_NAME);
			if (database == null) {
				DBOBService.getInstandce().dbConnectionError();
				LogUtil.d(TAG, "初始化异常");
				return;
			}
			DBOBService.getInstandce().dbConnectionSuccess();
			LogUtil.d(TAG, " db service init success ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
