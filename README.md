
### [mongodb3.4 ORM 工具类](https://git.oschina.net/what_the_fuck/Mongodb3.4.Tool.git)
最低JDK版本要求1.8

##### 使用方法

在程序入口初始化数据库的链接
DBHelp dbHelp = DBHelp.getInstand();
dbHelp.initDb();

其中IDBService 接口负责对初始化数据库链接时成功与失败的回调
用DBOBService来注册IDBService 的回调事件


#### DAO层的使用

任何需要对DAO操作的service层只需要继承BaseDAOService<实体bean> 即可

通过service.getQuery().**() 进行相关持久化操作


#### 实体bean
数据库字段需要用@DBAnnotaion注解
如果是主键@DBAnnotaion(true)表明
没有时会使用默认的_id
同时需要继承BaseEntity
=======
#Mongodb3.4.Tool
 