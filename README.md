# 高并发急速秒杀经典案例
## 整体开发思路，是从后端到前端再优化
一.DAO层开发

    1. 创建数据库及表格，初始化秒杀数据
    2. 通过sources/spring/spring-dao.xml配置dao层spring注入
       - 配置数据库连接 jdbc.properties
       - 连接池配置
       - sqlSessionFactory配置
       - mybatis接口mapper是在包配置
    3. 设计实体类cn.dangkei.entity包下类
    4. 设计mybatis接口cn.dangkei.dao包下类
    5. 开发接口实现类resource/mapper下对应接口的xml实现
    6. 配置logback.xml打印输出sql语句
    7. unit test 注意@RunWith,@ContextConfiguration及@Resource,@Autowired等注解的用法

二. Service层开发

    1. 开发cn.dangkei.service包下service接口
    2. 开发cn.dangkei.service.impl包下接口实现
    3. 完善数据传输类 cn.dangkei.dto，cn.dangkei.exception, cn.dangkei.enums
    4. 通过sources/spring/spring-service.xml配置service层注入
    5. unit test

三. Web层及前端页面开发

    1. 根据需求设计需要用到的Restful接口
        /seckill/list #查询秒杀列表页
        /seckill/{seckillId}/detail #查询单个秒杀详情页
        /seckill/time/now #查询当前时间
        /seckill/{seckill}/exposer #暴露秒杀接口
        /seckill/{seckill}/{md5}/execution #执行秒杀
    2. 根据接口设计开发SecKillController类
        - 注意 @RequestMapping,@PathVarible，@ReponseBody等注解用法
        - SecKillResult类设计
    3. Web层spring的注入
        - resourses/spring-mvc.xml
        - 通过webapp/WEB-INF/web.xml文件DispatcherServlet的contextConfigLocation参数注入所有容器对象
        注入所有容器对象    
    4. 前端页面开发
        webapp/WEB-INF/jsp/list.jsp，detail.jsp 
        source/script/seckill.js
        include用法，CDN的使用，cookie模拟注册，bootstrap用法,jstl使用，
        jquery $.post，$.get与后端交互
        seckill对象封装使用
四
 
## 数据库设计
src/main/sql/schema.sql
```sql
#数据库初始化脚本
#创建数据库

DROP DATABASE IF EXISTS seckill;
CREATE Database seckill;
USE seckill;

#创建表
CREATE TABLE seckill(
  seckill_id  bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  name VARCHAR(120) NOT NULL COMMENT '商品名称',
  number int NOT NULL COMMENT '库存数量',
  start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '秒杀开始时间',
  end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '秒杀结束时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_create_time(create_time),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀数据库';
# mysql引擎配置为innordb，以便支持事务

#初始化数据库
INSERT INTO
  seckill(name,number,start_time,end_time)
VALUES
  ('1000元秒杀iphone6',100,'2016-11-23 00:00:00','2016-11-24 00:00:00'),
  ('500元秒杀ipad2',200,'2016-11-23 00:00:00','2016-11-24 00:00:00'),
  ('300元秒杀小米4',300,'2016-11-23 00:00:00','2016-11-24 00:00:00'),
  ('2000元秒杀iphone6s',400,'2016-11-23 00:00:00','2016-11-24 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息(简化为手机号)
CREATE TABLE success_killed(
  `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
  `user_phone` BIGINT NOT NULL COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY(seckill_id,user_phone),/*联合主键*/
  KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';
```

## DAO层设计开发
### 连接数据库配置文件
1. resources/spring/jdbc.properties
```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf-8
jdbc.username=root
jdbc.password=123456
```
2. resource/spring/spring-dao.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 配置整合mybatis参数 -->
    <!-- 1. 配置数据库相关参数 properties的属性：${url}-->
    <context:property-placeholder location="classpath:spring/jdbc.properties"/>
	<!-- 2. 数据库连接池 -->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
		<property name="driverClass" value="${jdbc.driver}"/>
		<property name="jdbcUrl" value="${jdbc.url}"/>
		<property name="user" value="${jdbc.username}"/>
		<property name="password" value="${jdbc.password}"/>
		<!-- 连接私有属性 -->
		<property name="maxPoolSize" value="30"/>
		<property name="minPoolSize" value="10"/>
		<!-- 关闭连接后不自动commit -->
		<property name="autoCommitOnClose" value="false"/>
		<!-- 获取连接超时时间 -->
		<property name="checkoutTimeout" value="1000"/>
		<!-- 获取连接失败重试次数 -->
		<property name="acquireRetryAttempts" value="2"/>
	</bean>
	<!-- 3.注入SqlSessionFactory 对象 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- 配置数据库连接池 -->
		<property name="dataSource" ref="dataSource"/>
		<!-- 配置mybatis 全局属性 -->
		<property name="configLocation" value="classpath:mybatis-config.xml"/>
		<!-- 扫描entity包 使用别名 -->
		<property name="typeAliasesPackage" value="org.seckill.entity"/>
		<!-- 扫描接口实现xml -->
		<property name="mapperLocations" value="classpath:mapper/*.xml"/>
	</bean>
	<!-- 4.扫描dao接口包， 动态实现dao接口并注入spring -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- 注入sqlSessionFactory包 -->
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
		<!-- 给出药扫描的Dao包 -->
		<property name="basePackage" value="org.seckill.dao"/>
	</bean>
</beans>
```




