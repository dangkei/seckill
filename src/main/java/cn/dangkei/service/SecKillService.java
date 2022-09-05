package cn.dangkei.service;

import cn.dangkei.exception.RepeatKillException;
import cn.dangkei.exception.SecKillCloseException;
import cn.dangkei.exception.SecKillException;
import cn.dangkei.dto.Exposer;
import cn.dangkei.dto.SecKillExecution;
import cn.dangkei.entity.SecKill;

import java.util.List;

/*
* 站在使用者的角度去设计接口
* 三方面： 方法定义力度， 参数 ， 返回类型（return 类型友好）
* */
public interface SecKillService {
    /**
     * 查询所有秒杀记录
     */
    List<SecKill> getSecKillList();

    /**
     * 查询一条描述记录
     * @param id
     * @return
     */
    SecKill getById(long id);

    /**
     * 输出秒杀接口地址，否则系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSecKillUrl(long seckillId);

    /**
     * 执行秒杀
     * @param seckillId
     * @param phone
     * @param md5
     */
    SecKillExecution executesSecKill(long seckillId,long phone , String md5)
    throws SecKillException, SecKillCloseException, RepeatKillException;
}
