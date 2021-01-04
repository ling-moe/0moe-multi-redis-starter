package cn.lm.redis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.lm.redis.config.DynamicRedisTemplateFactory;
import cn.lm.redis.convert.DateDeserializer;
import cn.lm.redis.convert.DateSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

/**
 * @author yukdwan@gmail.com 2020/1/14 上午11:23
 */
@Configuration
public class MultiDbRedisConfiguration {

    @Bean
    @ConditionalOnMissingBean(MultiDbRedisTemplate.class)
    public MultiDbRedisTemplate multiDbRedisTemplate(RedisProperties redisProperties,
                                                     ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                                     ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                                     ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> jedisBuilderCustomizers,
                                                     ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> lettuceBuilderCustomizers){
        // 构建factory
        DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = new DynamicRedisTemplateFactory<>(
                redisProperties,
                sentinelConfiguration,
                clusterConfiguration,
                jedisBuilderCustomizers,
                lettuceBuilderCustomizers
        );
        // 构建objectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer());
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(RedisConstants.DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(RedisConstants.DATE_FORMAT)));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MultiDbRedisTemplate multiDbRedisTemplate = new MultiDbRedisTemplate(dynamicRedisTemplateFactory, objectMapper);
        multiDbRedisTemplate.setRedisTemplateMap(new ConcurrentHashMap<>(16));
        multiDbRedisTemplate.setDefaultRedisTemplate(multiDbRedisTemplate.targetRedisTemplate(redisProperties.getDatabase()));
        RedisUtil.setMultiDbRedisTemplate(multiDbRedisTemplate);
        return multiDbRedisTemplate;
    }
    @Bean
    @DependsOn("multiDbRedisTemplate")
    public RedisHelper redisHelper(){
        return RedisUtil.db();
    }


}
