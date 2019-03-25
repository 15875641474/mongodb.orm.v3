package com.jc.mongodb3_4.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具
 * @author joncch
 *
 * @param <T>
 */
public class QueryResult<T> {
	private List<T> list = new ArrayList<>();
	private Object object;
	private int page;
	private int size;
	private long totalPage;
	private long count;
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
	public long getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}

	
	
	
}
