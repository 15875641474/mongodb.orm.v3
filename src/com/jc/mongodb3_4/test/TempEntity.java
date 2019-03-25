package com.jc.mongodb3_4.test;

import com.jc.mongodb3_4.core.BaseEntity;
import com.jc.mongodb3_4.inteface.Column;

public class TempEntity extends BaseEntity {

	@Column(true)
	private String c1;
	@Column
	private String c2;
	public String getC1() {
		return c1;
	}
	public void setC1(String c1) {
		this.c1 = c1;
	}
	public String getC2() {
		return c2;
	}
	public void setC2(String c2) {
		this.c2 = c2;
	}
	
	
}
