package com.jc.mongodb3_4.observice;

import java.util.LinkedList;

/**
 * 被观察者服务注册类
 * @author joncch
 *
 * @param <T>
 */
public class OBService<T> {
	
	//缓存对象
	private LinkedList<T> observicelist = new LinkedList<>();
	
	
	public LinkedList<T> getObservicelist() {
		return observicelist;
	}

	/**
	 * 注册
	 * @param t
	 */
	public void register(T t){
		synchronized (observicelist) {
			if(!observicelist.contains(t)){
				observicelist.add(t);
			}
		}
	}
	
	/**
	 * 卸载
	 * @param t
	 */
	public void unRegister(T t){
		synchronized (observicelist) {
			observicelist.remove(t);
		}
	}
	
	/**
	 * 全部卸载
	 */
	public void unRegisterAll(){
		synchronized (observicelist) {
			observicelist.clear();
		}
	}
}
