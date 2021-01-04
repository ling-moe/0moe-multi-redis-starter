package cn.lm.redis;

import java.util.Map;
import java.util.Objects;

import cn.lm.redis.config.DynamicRedisTemplateFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author yukdwan@gmail.com 2020/1/13 上午10:20
 */
public class MultiDbRedisTemplate implements InitializingBean {

    public static final Logger log = LoggerFactory.getLogger(MultiDbRedisTemplate.class);

    private Map<Object, RedisHelper> redisTemplateMap;
    private RedisHelper defaultRedisTemplate;
    private final ObjectMapper objectMapper;
    private final DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory;

    public MultiDbRedisTemplate(DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory,
                                ObjectMapper objectMapper) {
        this.dynamicRedisTemplateFactory = dynamicRedisTemplateFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.redisTemplateMap == null) {
            throw new IllegalArgumentException("Property 'redisTemplates' is required");
        }
        if (this.defaultRedisTemplate == null) {
            throw new IllegalArgumentException("Property 'defaultRedisTemplate' is required");
        }
    }

    protected RedisHelper targetRedisTemplate(Object dbNo) {
        if (dbNo == null) {
            return this.defaultRedisTemplate;
        }
        RedisHelper redisTemplate = this.redisTemplateMap.get(dbNo);
        if (Objects.isNull(redisTemplate)) {
            synchronized (MultiDbRedisTemplate.class){
                // 双重校验锁
                redisTemplate = this.redisTemplateMap.get(dbNo);
                if (redisTemplate == null) {
                    redisTemplate = createRedisTemplateOnMissing(dbNo);
                    this.redisTemplateMap.put(dbNo, redisTemplate);
                }
            }
        }
        return redisTemplate;
    }

    protected RedisHelper targetRedisTemplate() {
        return targetRedisTemplate(null);
    }

    private RedisHelper createRedisTemplateOnMissing(Object db){
        RedisTemplate<String, String> redisTemplate = dynamicRedisTemplateFactory.createRedisTemplate((Integer) db);
        return new RedisHelper(redisTemplate, objectMapper);
    }

    public void setRedisTemplateMap(Map<Object, RedisHelper> redisTemplateMap) {
        this.redisTemplateMap = redisTemplateMap;
    }

    public void setDefaultRedisTemplate(RedisHelper defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }
}
