/*
 * Copyright 2020-present, HZERO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.lm.redis.config;

import java.util.List;

import cn.lm.redis.RedisHelper;
import cn.lm.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * 动态 RedisTemplate 工厂类
 *
 * @author bojiangzhou 2020/05/06
 */
public class DynamicRedisTemplateFactory<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRedisTemplateFactory.class);

    private final RedisProperties properties;
    private final ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration;
    private final ObjectProvider<RedisClusterConfiguration> clusterConfiguration;
    private final ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> jedisBuilderCustomizers;
    private final ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> lettuceBuilderCustomizers;

    private static final String REDIS_CLIENT_LETTUCE = "lettuce";
    private static final String REDIS_CLIENT_JEDIS = "jedis";

    public DynamicRedisTemplateFactory(RedisProperties properties,
                                       ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                       ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                       ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> jedisBuilderCustomizers,
                                       ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> lettuceBuilderCustomizers) {
        this.properties = properties;
        this.sentinelConfiguration = sentinelConfiguration;
        this.clusterConfiguration = clusterConfiguration;
        this.jedisBuilderCustomizers = jedisBuilderCustomizers;
        this.lettuceBuilderCustomizers = lettuceBuilderCustomizers;
    }

    public RedisTemplate<K, V> createRedisTemplate(int database) {
        RedisConnectionFactory redisConnectionFactory = null;
        switch (getRedisClientType()) {
            case REDIS_CLIENT_LETTUCE:
                LettuceConnectionConfigure lettuceConnectionConfigure = new LettuceConnectionConfigure(properties,
                        sentinelConfiguration, clusterConfiguration, lettuceBuilderCustomizers, database);
                redisConnectionFactory = lettuceConnectionConfigure.redisConnectionFactory();
                break;
            case REDIS_CLIENT_JEDIS:
                JedisConnectionConfigure jedisConnectionConfigure = new JedisConnectionConfigure(properties,
                        sentinelConfiguration, clusterConfiguration, jedisBuilderCustomizers, database);
                redisConnectionFactory = jedisConnectionConfigure.redisConnectionFactory();
                break;
            default:
                //
        }

        Assert.notNull(redisConnectionFactory, "redisConnectionFactory is null.");
        return createRedisTemplate(redisConnectionFactory);
    }

    private RedisTemplate<K, V> createRedisTemplate(RedisConnectionFactory factory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private String getRedisClientType() {
        try {
            Class.forName("io.lettuce.core.RedisClient");
            return REDIS_CLIENT_LETTUCE;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Lettuce redis client");
        }

        try {
            Class.forName("redis.clients.jedis.Jedis");
            return REDIS_CLIENT_JEDIS;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Jedis redis client");
        }

        throw new RuntimeException("redis client not found.");
    }


}
