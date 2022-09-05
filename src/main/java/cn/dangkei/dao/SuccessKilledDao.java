package cn.dangkei.dao;

import org.apache.ibatis.annotations.Param;
import cn.dangkei.entity.SuccessKilled;

public interface SuccessKilledDao {
	/*
	 * 插入购买明细， 可过滤重复
	 * @param seckillId
	 * @param userPhone
	 * @return 如果影响行数>1 表示更新成功
	 * */

	int insertSuccessKilled(@Param("id") long seckillId,@Param("phone") long userPhone);
	
	/*
	 * 根据Id查询SuccessKilled冰携带m秒杀产品对象实体。
	 * @param seckillId 
	 **/
	
	SuccessKilled queryByIdWithSeckill(@Param("id") long seckillId,@Param("phone") long phone);
}
