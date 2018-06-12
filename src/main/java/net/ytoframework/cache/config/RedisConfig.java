package net.ytoframework.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * 自定义redisConfig
 * @author 01482445(wangchao)
 * @version 1.0
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {
    /**
     * 默认全局缓存时间
     */
    private static final int DEFAULT_CACHE_TIME = 7 * 24 * 60 * 60;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                // 类名+方法名
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        YtoRedisCacheManager redisCacheManager = new YtoRedisCacheManager(redisTemplate);
        // 设置全局缓存过期时间
        redisCacheManager.setDefaultExpiration(DEFAULT_CACHE_TIME);
        return redisCacheManager;
    }

    /**
     * 使用自定义的序列化，FastJsonRedisSerializer,避免key值不明晰
     * @param redisConnectionFactory redisFactory
     * @return redisTemplate
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
      RedisTemplate<Object, Object> template = new RedisTemplate<>();  
      // 使用fastjson序列化  
      FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
      // value值的序列化采用fastJsonRedisSerializer
      template.setValueSerializer(fastJsonRedisSerializer);
      template.setHashValueSerializer(fastJsonRedisSerializer);
      // key的序列化采用StringRedisSerializer key值一般存String类型
      template.setKeySerializer(new StringRedisSerializer());
      template.setHashKeySerializer(new StringRedisSerializer());

      template.setConnectionFactory(redisConnectionFactory);
      return template;
    }

    @Bean  
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * redis异常处理，日志中打印错误信息，程序上放行
     * 保证能够出问题时不用redis缓存,可执行业务逻辑去查询数据库DB
     * @return ytoCacheErrorHandler
     */
    @Bean
    public CacheErrorHandler ytoCacheErrorHandler() {
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis server error：key=[{}]", key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Redis server error：key=[{}]", key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis server error：key=[{}]", key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("Redis server error：key=[{}]", exception);
            }
        };
        return cacheErrorHandler;
    }
}
