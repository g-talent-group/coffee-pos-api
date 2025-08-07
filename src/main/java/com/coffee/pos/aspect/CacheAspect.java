package com.coffee.pos.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Aspect
@Slf4j
public class CacheAspect {

    private final ConcurrentHashMap<String, Cache<String, Object>> cacheMap = new ConcurrentHashMap<>();

    @Around("@annotation(cacheable)")
    public Object handleCache(ProceedingJoinPoint joinPoint,
                              Cacheable cacheable) throws Throwable {

        // 產生快取key
        String cacheKey = generateCacheKey(joinPoint, cacheable.key());
        
        // 獲取或創建快取實例
        Cache<String, Object> cache = getCache(cacheable.expireSeconds());

        // 嘗試從快取取得資料
        Object cached = cache.getIfPresent(cacheKey);
        if (cached != null) {
            log.debug("Cache hit for key: {}", cacheKey);
            return cached;
        }

        log.debug("Cache miss for key: {}", cacheKey);

        // 執行原方法
        Object result = joinPoint.proceed();

        // 儲存到快取
        if (result != null) {
            cache.put(cacheKey, result);
            log.debug("Cached result for key: {} with expiration: {} seconds",
                    cacheKey, cacheable.expireSeconds());
        }

        return result;
    }

    private Cache<String, Object> getCache(int expireSeconds) {
        String cacheId = "cache_" + expireSeconds;
        return cacheMap.computeIfAbsent(cacheId, k ->
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(expireSeconds))
                .maximumSize(1000)
                .build());
    }

    private String generateCacheKey(ProceedingJoinPoint joinPoint, String keyTemplate) {
        if (keyTemplate.isEmpty()) {
            // 預設key生成策略
            return joinPoint.getSignature().toShortString() + ":" +
                    Arrays.hashCode(joinPoint.getArgs());
        }

        // 簡化版的key模板解析
        return keyTemplate.replace("#args", Arrays.toString(joinPoint.getArgs()));
    }
}