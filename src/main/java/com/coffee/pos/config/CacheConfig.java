package com.coffee.pos.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)           // 初始容量
                .maximumSize(1000)              // 最大容量
                .expireAfterWrite(Duration.ofMinutes(10))  // 寫入後10分鐘過期
                .expireAfterAccess(Duration.ofMinutes(5))  // 最後存取5分鐘後過期
                .recordStats());                // 啟用異計
        return cacheManager;
    }
}