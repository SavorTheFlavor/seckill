package com.me.seckill.exception;

/**
 * 所有秒杀业务相关的异常
 * Created by Administrator on 2017/5/14.
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
