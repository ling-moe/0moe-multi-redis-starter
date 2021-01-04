package cn.lm.redis;


/**
 * @author yukdwan@gmail.com 2020/1/13 上午10:36
 */
public class RedisUtil {

    private static MultiDbRedisTemplate multiDbRedisTemplate;


    protected static void setMultiDbRedisTemplate(MultiDbRedisTemplate multiDbRedisTemplate) {
        RedisUtil.multiDbRedisTemplate = multiDbRedisTemplate;
    }

    public static RedisHelper db(int db){
        return multiDbRedisTemplate.targetRedisTemplate(db);
    }

    public static RedisHelper db(){
        return multiDbRedisTemplate.targetRedisTemplate();
    }
}
