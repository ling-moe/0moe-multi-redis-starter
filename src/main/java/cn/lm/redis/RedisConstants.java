package cn.lm.redis;

/**
 * @author yukdawn@gmail.com 2020/12/26 15:45
 */
public class RedisConstants {

    /**
     * 默认时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24L;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    /**
     * 空字符串
     */
    public static final String STR_EMPTY = "";
}
