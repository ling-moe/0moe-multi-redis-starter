package cn.lm.redis;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * @author yukdawn@gmail.com 2020/12/26 11:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(MultiDbRedisConfiguration.class)
public @interface Enable0moeMultiDbRedis {
}
