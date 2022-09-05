package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SecKillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SecKillService secKillService;


    @Test
    public void getSecKillList() {
        List<SecKill> secKillList= secKillService.getSecKillList();
        logger.info("List={}",secKillList);
    }

    @Test
    public void getById() {
        long id =1000;
        SecKill secKill = secKillService.getById(id);
        logger.info("secKill={}",secKill);
    }


    /**
     * 集成测试代码完整逻辑， 可重复执行
     */
    @Test
    public void executesSecKillLogic() {
        long id = 1001;
        Exposer exposer = secKillService.exportSecKillUrl(id);
        logger.info("exposer={}",exposer);
        if(exposer.isExposed()){
            long phone = 13581700404L;
            String MD5= "1ad3b7c869b19557e9cb531a26222fa7";
            try {
                SecKillExecution secKillExecution = secKillService.executesSecKill(id,phone,MD5);
                logger.info("result={}" ,secKillExecution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SecKillCloseException e2){
                logger.error(e2.getMessage());
            }
        }else {
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }

    }
}