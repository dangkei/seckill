package cn.dangkei.controller;

import cn.dangkei.dto.Exposer;
import cn.dangkei.dto.SecKillResult;
import cn.dangkei.enums.SecKillStatEnum;
import cn.dangkei.exception.RepeatKillException;
import cn.dangkei.exception.SecKillCloseException;
import cn.dangkei.service.SecKillService;
import cn.dangkei.dto.SecKillExecution;
import cn.dangkei.entity.SecKill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SecKillController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SecKillService secKillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        List<SecKill> list = secKillService.getSecKillList();
        logger.info("----------------into seckill list---------------------------------------------------");
        model.addAttribute("list",list);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId,Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        SecKill secKill = secKillService.getById(seckillId);
        if(secKill==null){
            return "redirect:/seckill/list";
        }
        model.addAttribute("seckill",secKill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer"
            ,method = RequestMethod.POST
            ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SecKillResult<Exposer> result;
        logger.info("---------暴露秒杀链接----------------------");
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SecKillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = new SecKillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution"
            ,method = RequestMethod.POST
            ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<SecKillExecution> execute(@PathVariable("seckillId") Long seckillId
            ,@PathVariable("md5") String md5
            ,@CookieValue(value = "killPhone",required = false) Long phone){
        logger.info("-----------执行秒杀操作-------------------");
        if(null==phone){
            return  new SecKillResult<SecKillExecution>(false,"未注册");
        }
        //SecKillResult<SecKillExecution> result;
        try{
            SecKillExecution secKillExecution = secKillService.executesSecKill(seckillId,phone,md5);
            return new SecKillResult<SecKillExecution>(true,secKillExecution);
        }catch(SecKillCloseException e){
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStatEnum.END);
            return new SecKillResult<SecKillExecution>(true,secKillExecution);
        }catch (RepeatKillException e){
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStatEnum.REPEAT_KILL);
            return new SecKillResult<SecKillExecution>(true,secKillExecution);
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStatEnum.INNER_ERROR);
            return new SecKillResult<SecKillExecution>(true,secKillExecution);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<Long> time(){
        Date now = new Date();
        return new SecKillResult<>(true,now.getTime());
    }
}
