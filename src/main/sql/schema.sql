#数据库初始化脚本
#创建数据库

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
#mysql引擎配置为innordb，以便支持事务

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





