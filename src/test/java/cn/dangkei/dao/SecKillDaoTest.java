package cn.dangkei.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import cn.dangkei.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {
	
	@Resource
    private SecKillDao seckillDao;

    @Test
    public void queryById() throws Exception {

		  long seckillId=1000;
		  SecKill seckill= seckillDao.queryById(seckillId);
		  System.out.println("----------------------test queryById----------");
		  System.out.println(seckill.getName());
		  System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {

    	 List<SecKill> seckills=seckillDao.queryAll(0,100);
		 System.out.println("----------------------queryAll-----------");
    	 for (SecKill seckill : seckills) {
    	 	System.out.println(seckill);
    	 }

    }

    @Test
    public void reduceNumber() throws Exception {

		  long seckillId=1000; Date date=new Date();
		  int updateCount=seckillDao.reduceNumber(seckillId,date);
		System.out.println("----------------------reduceNumber-----------");
		  System.out.println(updateCount);


    }

}
