package com.jc.mongodb3_4.test;

import java.util.ArrayList;
import java.util.List;

import com.jc.mongodb3_4.core.LogUtil;
import com.jc.mongodb3_4.core.QueryResult;
import com.jc.mongodb3_4.inteface.IBaseService.ColumnFilter;
import com.jc.mongodb3_4.inteface.IBaseService.ColumnFilterType;

public abstract class TestMain {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		TempService t = new TempService();
//		System.out.println(t.getQuery().count());
//		LogUtil.UPDATE_MLAB_DATABASE = true;
//		for (int i = 0; i < 10; i++) {
//			TempEntity e = new TempEntity();
//			e.setC1("a"+i);
//			t.getQuery().insert(e);
//		}
//		System.out.println(t.getQuery().count());
		 
		t.getQuery().findAllSimple(t.createColumnFilter("this.c1 == 'a0' "));
	}
}
