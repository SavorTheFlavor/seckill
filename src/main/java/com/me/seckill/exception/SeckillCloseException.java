package com.me.seckill.exception;

/**
 * Created by Administrator on 2017/5/14.
 */
public class SeckillCloseException extends  SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
