package com.me.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.me.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2017/7/10.
 */
public class RedisDao {
    private final JedisPool jedisPool;

    //使用自定义序列化操作，protostuff
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    //从缓存中获取seckill
    public Seckill getSeckill(long seckillId) {
        Jedis jedis = jedisPool.getResource();
        try {
            try {
                //jedis中对象是被序列化成字节数组后存储起来的
                String key = "seckill:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {//从缓存中获取到了对象
                    //反序列化
                    Seckill seckill = schema.newMessage();//空对象
                    ProtobufIOUtil.mergeFrom(bytes, seckill, schema);//把seckill反序列化
                    return seckill;
                }

            } finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return null;
    }

    //把seckill放到缓存中
    public String putSeckill(Seckill seckill) {
        // seckill -> byte[]
        Jedis jedis = jedisPool.getResource();
        try {
            try {
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                String key = "seckill:"+seckill.getSeckillId();
                String result = jedis.setex(key.getBytes(),60*60,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
