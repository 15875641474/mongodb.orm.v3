package com.jc.mongodb3_4.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.jc.mongodb3_4.inteface.Column;
import com.jc.mongodb3_4.inteface.IBaseService;
import com.jc.mongodb3_4.inteface.Table;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

/**
 * 
 * @author joncch
 *
 * @param <T>
 */
public class BaseDAOService<T> implements IBaseService {
	protected static final int asc = 1;
	protected static final int desc = -1;
	protected DBHelp db = DBHelp.getInstand();
	private MongoCollection query;

	/**
	 * 获得持久化操作对象
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Query getQuery() {
		synchronized (db) {
			if (db.getdb() == null) {
				db.initDb();
			}
			if (this.query == null) {
				String tableName = null;
				try {
					tableName = getTClass().newInstance().getClass().getAnnotation(Table.class).value();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("warn:lost Annotation with @Table ! use class name to get query collection");
				}
				if (tableName == null || tableName.isEmpty()) {
					tableName = getTClass().getSimpleName();
				}
				this.query = db.getdb().getCollection(tableName);
			}
			Query q = new Query();
			return q;
		}
	}

	/**
	 * 持久化相关操作
	 * 
	 * @author joncch
	 *
	 */
	public class Query {

		/**
		 * 获得表操作对象
		 * 
		 * @return
		 */
		public MongoCollection getCollectionQuery() {
			return query;
		}

		/**
		 * 默认查询
		 * 
		 * @param filters
		 *            条件过滤
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public QueryResult<T> findAll() {
			return findAll(null);
		}

		/**
		 * 默认查询
		 * 
		 * @param filter
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public QueryResult<T> findAllSimple(ColumnFilter filter) {
			List<ColumnFilter> filters = new ArrayList<>();
			filters.add(filter);
			return findAll(filters, -1, -1);
		}

		/**
		 * 默认查询
		 * 
		 * @param filters
		 *            条件过滤
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public QueryResult<T> findAll(List<ColumnFilter> filters) {
			return findAll(filters, -1, -1);
		}

		/**
		 * 默认查询
		 * 
		 * @param filters
		 *            条件过滤
		 * @param page
		 *            起始页从0开始
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public QueryResult<T> findAll(List<ColumnFilter> filters, int page) {
			return findAll(filters, page, 15);
		}

		/**
		 * 默认查询
		 * 
		 * @param filters
		 *            条件过滤
		 * @param page
		 *            起始页从0开始
		 * @param size
		 *            分页大小
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public QueryResult<T> findAll(List<ColumnFilter> filters, int page, int size) {
			return findAll(filters, page, size, 0, null);
		}

		/**
		 * 默认查询
		 * 
		 * @param filters
		 *            条件过滤
		 * @param page
		 *            起始页从0开始
		 * @param size
		 *            分页大小
		 * @param order
		 *            asc|desc
		 * @param orderkeys
		 *            排序的字段
		 * @return
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		@SuppressWarnings("unchecked")
		public QueryResult<T> findAll(List<ColumnFilter> filters, int page, int size, int order, String... orderkeys) {
			QueryResult<T> result = new QueryResult<T>();
			result.setPage(page);
			result.setSize(size);

			FindIterable<T> findIterable = null;
			if (filters != null && filters.size() > 0) {
				Bson[] bson = new Bson[filters.size()];
				for (int i = 0; i < bson.length; i++) {
					bson[i] = filters.get(i).builder(query);
				}
				findIterable = query.find(Filters.and(bson));
				result.setCount(query.count(Filters.and(bson)));
			} else {
				findIterable = query.find();
				result.setCount(query.count());
			}
			if (page != -1 && size != -1) {
				findIterable.limit(size).skip(page * size);
			}
			if (orderkeys != null && orderkeys.length > 0) {
				if (order == asc) {
					findIterable.sort(Sorts.ascending(orderkeys));
				} else if (order == desc) {
					findIterable.sort(Sorts.descending(orderkeys));
				}
			}

			// 获得数据集
			findIterable.forEach((Block<? super T>) new Block<Document>() {
				@Override
				public void apply(Document document) {
					try {
						result.getList().add(convertToEntity(document));
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});

			// 计算总页数
			if (result.getCount() > 0) {
				long totalPage = result.getCount() / result.getSize();
				if ((result.getCount() % result.getSize()) != 0) {
					totalPage += 1;
				}
				result.setTotalPage(totalPage);
			}

			return result;
		}

		/**
		 * 获得总数
		 * 
		 * @param filters
		 * @return
		 */
		public long count() {
			return count(null);
		}

		/**
		 * 获得总数
		 * 
		 * @param filters
		 * @return
		 */
		public long count(List<ColumnFilter> filters) {
			try {
				if (filters != null && filters.size() > 0) {
					Bson[] bson = new Bson[filters.size()];
					for (int i = 0; i < bson.length; i++) {
						bson[i] = filters.get(i).builder(query);
					}
					return query.count(Filters.and(bson));
				} else {
					return query.count();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * 新增
		 * 
		 * @param ad
		 * @return
		 */
		public boolean insert(List<T> t) {
			try {
				if (!LogUtil.UPDATE_MLAB_DATABASE)
					return true;
				List<Document> datas = new ArrayList<>();
				t.forEach(o -> datas.add(converToDBFormat(o)));
				query.insertMany(datas);
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return false;
		}

		/**
		 * 新增
		 * 
		 * @param ad
		 * @return
		 */
		public boolean insert(T t) {
			try {
				if (!LogUtil.UPDATE_MLAB_DATABASE)
					return true;
				query.insertOne(converToDBFormat(t));
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return false;
		}

		/**
		 * 主键查找
		 * 
		 * @param id
		 * @return
		 */
		public T findOne(String id) {
			try {
				String pk = getPk();
				Object one = query.find(new Document(pk, pk.equals("_id") ? new ObjectId(id) : id)).first();
				if (one != null) {
					if (one instanceof Document) {
						return convertToEntity((Document) one);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * 主键删除
		 * 
		 * @param id
		 * @return
		 */
		public boolean delete(String id) {
			try {
				query.deleteOne(new Document(getPk(), id));
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return false;
		}

		/**
		 * 更新对象
		 * 
		 * @param id
		 * @param t
		 * @return
		 */
		public boolean update(String id, T t) {
			try {
				Object result = query.findOneAndUpdate(new Document(getPk(), id),
						new Document("$set", converToDBFormat(t)));
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return false;
		}
	}

	// 主键
	private String pk = null;

	// 泛型的类
	private Class tclass = null;

	/**
	 * 获得主键key
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected String getPk() throws InstantiationException, IllegalAccessException {
		if (pk == null || pk.isEmpty()) {
			Class cls = getTClass();
			Field[] fs = getAllField(cls.newInstance());
			for (Field f : fs) {
				if (f.isAnnotationPresent(Column.class)) {
					if (f.getAnnotation(Column.class).value()) {
						pk = f.getName();
						break;
					}
				}
			}
		}
		// 没有设置主键则使用mongodb默认主键
		if (pk == null || pk.isEmpty()) {
			pk = "_id";
		}
		return pk;
	}

	/**
	 * 获得默认泛型类型
	 * 
	 * @return
	 */
	private Class getTClass() {
		if (tclass == null) {
			ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
			tclass = (Class<T>) (parameterizedType.getActualTypeArguments()[0]);
		}
		return tclass;
	}

	/**
	 * 转换成持久化对象
	 * 
	 * @param t
	 * @return
	 */
	private Document converToDBFormat(T t) {
		Document d = new Document();
		try {
			Field[] fs = getAllField(t);
			for (Field f : fs) {
				if (f.isAnnotationPresent(Column.class)) {
					f.setAccessible(true);
					d.put(f.getName(), f.get(t));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 获得所有的属性
	 * 
	 * @param t
	 * @return
	 */
	private static Field[] getAllField(Object t) {
		Class cls = t.getClass();
		List<Field> listField = new ArrayList<>();
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			listField.add(f);
		}
		while (!((cls = cls.getSuperclass()).getSimpleName().equals("Object"))) {
			fs = cls.getDeclaredFields();
			for (Field f : fs) {
				listField.add(f);
			}
		}
		return listField.toArray(new Field[listField.size()]);
	}

	/**
	 * 持久化对象转换成映射实体
	 * 
	 * @param document
	 * @return
	 */
	protected T convertToEntity(Document document) {
		try {
			Object obj = getTClass().newInstance();
			for (String key : document.keySet()) {
				try {
					if (document.get(key) != null) {
						// 使用符合JavaBean规范的属性访问器
						PropertyDescriptor pd = new PropertyDescriptor(key, obj.getClass());
						Method writeMethod = pd.getWriteMethod();
						writeMethod.invoke(obj, String.valueOf(document.get(key)));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			return (T) obj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
