package cn.dangkei.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import cn.dangkei.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        System.out.println("a");
        long id = 1001L;
        long phone = 13581600404L;
        int insertCount = successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount="+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 13581600404L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}