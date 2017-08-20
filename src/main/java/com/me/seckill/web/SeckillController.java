package com.me.seckill.web;

import com.me.seckill.dto.Exposer;
import com.me.seckill.dto.SeckillExcecution;
import com.me.seckill.dto.SeckillResult;
import com.me.seckill.entity.Seckill;
import com.me.seckill.enums.SeckillStateEnum;
import com.me.seckill.exception.RepeatKillException;
import com.me.seckill.exception.SeckillCloseException;
import com.me.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */
@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){//前端页面丢过来的一个model(的引用..
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        //list.jsp+model = ModelAndView
        //System.out.println("-----------------------------------"+list.get(0).getName());
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
    produces = {"application/json;charset=UTF-8"})
    @ResponseBody   //使spring尝试以json形式返回数据
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",
    method = RequestMethod.POST,
    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExcecution> execute(@PathVariable("seckillId") Long seckillId,
                                                    @PathVariable("md5") String md5,
                                                    @CookieValue(value = "killPhone",required = false) Long phone){
       if(phone == null){
           return  new SeckillResult<SeckillExcecution>(false,"未注册");
       }
       try {
           SeckillExcecution excecution = seckillService.excuteSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExcecution>(true,excecution);
       }catch (SeckillCloseException e){
            SeckillExcecution excecution = new SeckillExcecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExcecution>(true,excecution);
       }catch (RepeatKillException e){
            SeckillExcecution excecution = new SeckillExcecution(seckillId,SeckillStateEnum.REPEATE_KILL);
            return new SeckillResult<SeckillExcecution>(true,excecution);
       }catch (Exception e){
           SeckillExcecution excecution = new SeckillExcecution(seckillId,SeckillStateEnum.INNER_ERROR);
           return new SeckillResult<SeckillExcecution>(true,excecution);
       }
    }
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public @ResponseBody SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }

}