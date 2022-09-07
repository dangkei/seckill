package cn.dangkei.dao.cache;

import cn.dangkei.entity.SecKill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDao {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int timeout = 5000;
    private JedisPool jedisPool;

    public RedisDao(String ip, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        config.setMaxIdle(1024);
        config.setMaxTotal(1024);
        config.setMinIdle(20);
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * 10);
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, ip, port, timeout, password, 0);
    }

    private RuntimeSchema<SecKill> schema = RuntimeSchema.createFrom(SecKill.class);

    public SecKill getSecKill(long seckillId) {
        //Jedis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //并没有内部序列化操作
                //get->byte[]->反序列化->Object(SecKill)
                //采用自定义开源序列化方式 1. 节省cpu 2. 压缩空间是原生jdk序列化的十到五分之一 3. 压缩速度是两位数量级的提高
                //protostuff pojo
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //空对象
                    SecKill secKill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,secKill,schema);
                    //seckill被反序列化
                    return secKill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSecKill(SecKill secKill) {
        //set Object(secKill)-对象序列化->byte[]
        try{
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+ secKill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(secKill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                String result = jedis.setex(key.getBytes(),timeout,bytes);
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
