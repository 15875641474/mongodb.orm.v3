package com.jc.mongodb3_4.core;

import java.util.Date;

/**
 * 日志
 * @author joncch
 *
 */
public class LogUtil {
	
	private static final String TAG = LogUtil.class.getSimpleName();
	
	//清空创建的html文件
	public final static boolean CLARE_HTML_FILE = false;
	//上传到CND服务器
	public final static boolean UPDATE_COS_CND = false;
	/**
	 * 提交数据库
	 */
	public static boolean UPDATE_MLAB_DATABASE = false;
	/**
	 * 显示控制台日志
	 */
	public static boolean SHOW_CONTROL_LOG = true;
	 
 
	public static void d(String text){
		d(TAG,text);
	}
	public static void d(String tag,String text){
		print(tag, text);
	}
	
	private static void print(String tag,String text){
		if(SHOW_CONTROL_LOG){
			Date d = new Date();
			long l = d.getTime();
			System.out.println(String.format("%tF %tT.%s %s --- %s", d,d,String.valueOf(l).substring(String.valueOf(l).length() - 3),tag,text));
		}
	}
	
	public static void main(String[] args) {
		 
	}
}
