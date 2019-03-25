package com.jc.mongodb3_4.core;

/**
 * mongodb 固有属性
 * 
 * @author joncch
 *
 */
public class BaseEntity {

	protected String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
 
	
}
