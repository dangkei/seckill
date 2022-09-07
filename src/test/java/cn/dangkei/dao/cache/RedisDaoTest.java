package cn.dangkei.dao.cache;

import cn.dangkei.dao.SecKillDao;
import cn.dangkei.entity.SecKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    @Autowired
    RedisDao redisDao;

    @Autowired
    SecKillDao secKillDao;

    private long seckillId = 1001;
    @Test
    public void getSecKill() {
        SecKill secKill = redisDao.getSecKill(seckillId);
        if(secKill==null){
            secKill = secKillDao.queryById(seckillId);
            if(secKill!=null){
                String  result = redisDao.putSecKill(secKill);
                System.out.println(result);
                secKill = redisDao.getSecKill(seckillId);
                System.out.println(secKill);
            }
        }
    }

    @Test
    public void putSecKill() {
    }
}