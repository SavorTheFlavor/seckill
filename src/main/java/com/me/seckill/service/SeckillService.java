package com.me.seckill.service;

import com.me.seckill.dto.Exposer;
import com.me.seckill.dto.SeckillExcecution;
import com.me.seckill.entity.Seckill;
import com.me.seckill.exception.RepeatKillException;
import com.me.seckill.exception.SeckillCloseException;
import com.me.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型
 */
public interface SeckillService {
    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id查询秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间（倒计时...
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExcecution excuteSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException,SeckillCloseException,RepeatKillException;

    /**
     * 使用存储过程执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExcecution excuteSeckillByProcedure(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,RepeatKillException;

}
