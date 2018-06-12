package net.ytoframework.cache.config;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 自定义缓存管理类
 * 在name后用@分割，然后加上过期的时间，单位为秒
 * <pre> @Cacheable(value = "UserMacRelationService.findAllUserMacInfo@120", keyGenerator = "keyGenerator")</pre>
 * <pre> @CacheEvict(value = "UserMacRelationService.findAllUserMacInfo", allEntries = true)</pre>
 * @author 01482445(wangchao)
 * @version 1.0
 */
public class YtoRedisCacheManager extends RedisCacheManager {

    public YtoRedisCacheManager(RedisOperations redisOperations) {
        super(redisOperations);
    }

    @Override
    public Cache getCache(String name) {
        String[] cacheParams = name.split("@");
        String cacheName = cacheParams[0];
        Long expirationSecondTime = this.computeExpiration(cacheName);
        if(cacheParams.length > 1) {
            expirationSecondTime = Long.parseLong(cacheParams[1]);
            this.setDefaultExpiration(expirationSecondTime);
        }
        Cache cache = super.getCache(cacheName);
        return cache;
    }
}
