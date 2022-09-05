package org.seckill.service.impl;

import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SecKillServiceImpl implements SecKillService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillDao secKillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //用于混淆md5
    private final String slat = "5gyuilhjkj908779809-i;nygiuhk";

    @Override
    public List<SecKill> getSecKillList() {
        return secKillDao.queryAll(0,4);
    }

    @Override
    public SecKill getById(long id) {
        return secKillDao.queryById(id);
    }

    @Override
    public Exposer exportSecKillUrl(long seckillId) {
        SecKill secKill = getById(seckillId);
        if(null==secKill){
            return  new Exposer(false,seckillId);
        }
        Date startTime = secKill.getStartTime();
        Date endTime = secKill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime() ||
                nowTime.getTime() > endTime.getTime()) {
            //return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime()
            //        , endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId); //todo
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId+"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制方法的优点
     * 1.开发团队达成一致的约定，明确标注十五方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他的网络操作 RPC/HTTP请求/或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，或者只读操作。
     */
    public SecKillExecution executesSecKill(long seckillId, long phone, String md5) throws SecKillException, SecKillCloseException, RepeatKillException {
        if(md5==null||!md5.equals(getMD5(seckillId))){
            throw new SecKillException("seckill data rewrite.");
        }
        //秒杀逻辑. 1.减库存， 2. 记录秒杀记录。
        Date nowTime = new Date();
        try {
            int updateCount = secKillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0){
                //没有更新记录.
                throw new SecKillCloseException("seckill is closed!");
            }else{
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,phone);
                if (insertCount<=0){
                    throw new RepeatKillException("seckill repeated !");
                }else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,phone);
                    return new SecKillExecution(seckillId, SecKillStatEnum.SUCCESS, successKilled);
                }
            }
        }catch (SecKillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SecKillException("seckill inner error!" + e.getMessage());
        }
    }
}
