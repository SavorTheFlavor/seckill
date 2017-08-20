package com.me.seckill.exception;

/**
 * Created by Administrator on 2017/5/14.
 */
public class RepeatKillException extends  SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
