package com.jc.mongodb3_4.inteface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * 条件表达式构建类
 * @author joncch
 *
 */
public interface IBaseService {

	/**
	 * 条件表达式枚举
	 * 
	 * @author joncch
	 *
	 */
	public enum ColumnFilterType {
		ANDLIKE, // a like '?' and a like '?'
		ORLIKE, //  a like '?' or a like '?'
		ANDEQUAL, // a = '?' and a = '?'
		OREQUAL, // a = '?' or a = '?' 
		IN,
		GT, // a > b
		GTE, // a >= b
		LT, // a < b
		LTE, // a <= b
		WHERE,
		
		 
	}

	public default ColumnFilter createColumnFilter() {
		return new ColumnFilter();
	} 
	
	public default ColumnFilter createColumnFilter(Object value) {
		return createColumnFilter(null,value,ColumnFilterType.WHERE);
	}
	
	public default ColumnFilter createColumnFilter(String key, Object value) {
		return createColumnFilter(key,value,ColumnFilterType.ANDEQUAL);
	}
	
	public default ColumnFilter createColumnFilter(String key, Object value, ColumnFilterType columnFilterType) {
		ColumnFilter columnFilter = new ColumnFilter();
		columnFilter.setKey(key);
		columnFilter.setValue(value);
		columnFilter.setColumnFilterType(columnFilterType);
		return columnFilter;
	} 

	/**
	 * 条件表达式
	 * 
	 * @author joncch
	 *
	 */
	public class ColumnFilter {

		public Bson builder(MongoCollection collection) {
			//筛选条件
			Bson b = null;
			//值集合
			List<String> vlist = new ArrayList<>();
			//判断类型并转换
			if (value instanceof String) {
				vlist.add(String.valueOf(value));
			} else if (value instanceof String[]) {
				vlist = Arrays.asList((String[])value);
			} else if (value instanceof List) {
				vlist = (List) value;
			}
			//预筛选数组
			Bson[] bs = new Bson[vlist.size()];
			//使用and或者or的语法拼接sql
			boolean and = false;
			
			//andlike & orlike
			if (ColumnFilterType.ANDLIKE == columnFilterType || ColumnFilterType.ORLIKE == columnFilterType) {
				and = ColumnFilterType.ANDLIKE == columnFilterType;
				for (int i = 0; i < vlist.size(); i++) {
					bs[i] = isNot() ? Filters.not(Filters.regex(key, vlist.get(i))) : Filters.regex(key, vlist.get(i)) ;
				}
			} 
			//equal & notequal
			else if (ColumnFilterType.ANDEQUAL == columnFilterType || ColumnFilterType.OREQUAL == columnFilterType) {
				and = ColumnFilterType.ANDEQUAL == columnFilterType;
				for (int i = 0; i < vlist.size(); i++) {
					bs[i] = isNot() ? Filters.not(Filters.eq(key, vlist.get(i))) : Filters.eq(key, vlist.get(i));
				} 
			}else if (ColumnFilterType.IN == columnFilterType) {
				and = true;
				bs = new Bson[1];
				bs[0] = isNot() ? Filters.not(Filters.in(key, vlist)) : Filters.in(key, vlist);
			}else if (ColumnFilterType.GT == columnFilterType || ColumnFilterType.GTE == columnFilterType) {
				and = true;
				boolean eq = ColumnFilterType.GT == columnFilterType;
				for (int i = 0; i < vlist.size(); i++) {
					bs[i] = eq ? Filters.gt(key, vlist.get(i)) : Filters.gte(key, vlist.get(i));
					bs[i] = isNot() ? Filters.not(bs[i]) : bs[i];
				} 
			}else if (ColumnFilterType.LT == columnFilterType || ColumnFilterType.LTE == columnFilterType) {
				and = true;
				boolean eq = ColumnFilterType.LT == columnFilterType;
				for (int i = 0; i < vlist.size(); i++) {
					bs[i] = eq ? Filters.lt(key, vlist.get(i)) : Filters.lte(key, vlist.get(i));
					bs[i] = isNot() ? Filters.not(bs[i]) : bs[i];
				}
			}else if(ColumnFilterType.WHERE == columnFilterType){
				and = true;
				for (int i = 0; i < vlist.size(); i++) {
					bs[i] = Filters.where(vlist.get(i));
				}
			}
			
			
			//如果是非语法，未知原因需要反转才能得到预期结果
			if(isNot()){
				and = !and;
			} 
			b = and ? Filters.and(bs) : Filters.or(bs);
			
			return b;
		}

		private String key;
		private Object value;
		private ColumnFilterType columnFilterType;
		private boolean not;

		
		public boolean isNot() {
			return not;
		}
		/**
		 * 正反条件
		 * @param not
		 */
		public void setNot(boolean not) {
			this.not = not;
		}

		public ColumnFilterType getColumnFilterType() {
			return columnFilterType;
		}

		public void setColumnFilterType(ColumnFilterType columnFilterType) {
			this.columnFilterType = columnFilterType;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		} 

	}
}
