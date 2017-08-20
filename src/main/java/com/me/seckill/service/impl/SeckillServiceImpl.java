package com.me.seckill.service.impl;

import com.me.seckill.dao.SeckillDao;
import com.me.seckill.dao.SuccessKilledDao;
import com.me.seckill.dto.Exposer;
import com.me.seckill.dto.SeckillExcecution;
import com.me.seckill.entity.Seckill;
import com.me.seckill.entity.SuccessKilled;
import com.me.seckill.enums.SeckillStateEnum;
import com.me.seckill.exception.RepeatKillException;
import com.me.seckill.exception.SeckillCloseException;
import com.me.seckill.exception.SeckillException;
import com.me.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/14.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    //md5盐值字符串，用于混淆md5
   private final String salt = "asfjhaeou4u32hqifh0fsd099r@OI#U@H@Hasd*&wa";
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime()
            ,endTime.getTime());
        }
        String md5 = getMD5(seckillId);//转化特定字符串的过程，不可逆
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/"+salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点
     *      开发团队达成一致约定，明确标注事务方法的编程风格
     *      保证事务方法的执行时间尽可能短，不要穿插其他网络操作、RPC/HTTP请求或者剥离到事务方法外
     *      不是所有方法都需要事务，只有一条修改操作或只读操作不需要事务控制
     */
    public SeckillExcecution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        if(md5 == null || !getMD5(seckillId).equals(md5)){
            throw new SeckillException("Data rewrite!");
        }
        try {//把内部所有可能发生的异常转化为SeckillException(RuntimeException..)
            //执行秒杀逻辑：记录购买行为+减库存

            //先执行插入是为降低mysql rowlock的持有时间，提高并发量
            //记录购买行为....不会插入多条记录的，因为是在同一个事务，只有减库存成功后才会提交事务
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if(insertCount <= 0){
                throw new RepeatKillException("Repeatly Kill!!");
            }else {
                //减库存
                Date nowTime = new Date();
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if(updateCount <= 0){
                    //没有更新记录，说明秒杀已结束或未开始
                    throw new SeckillCloseException("Seckill is closed！！");
                }

                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExcecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }

    }

    /**
     * 使用存储过程的秒杀逻辑
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @Override
    public SeckillExcecution excuteSeckillByProcedure(long seckillId, long userPhone, String md5){
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExcecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map,"result",-2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExcecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            }else {
                return new SeckillExcecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            return new SeckillExcecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }


}
