package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

public interface SecKillDao {
	/*
	 * 减库存
	 * @param seckkillId
	 * @param killTime
	 * 
	 * */
	
	int reduceNumber(@Param("seckillId") long seckillId , @Param("killTime") Date killTime);
	
	/*
	 * 根据Id查询秒杀
	 * */
	
	SecKill queryById(long seckillId);
	
	/*
	 * 根据偏移量查询秒杀商品列表
	 * */
	
	List<SecKill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

}
