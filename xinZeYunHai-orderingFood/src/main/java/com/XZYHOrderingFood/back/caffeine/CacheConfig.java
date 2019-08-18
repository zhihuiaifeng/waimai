package com.XZYHOrderingFood.back.caffeine;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 *  giturl:https://github.com/ben-manes/caffeine
 * 案例:
 * 	https://www.jianshu.com/p/c72fb0c787fc
 * @author Administrator
 *
 */

@Configuration
@EnableCaching
public class CacheConfig {
	public static final int DEFAULT_MAXSIZE = 50000;
    public static final int DEFAULT_TTL = 10;
    
    /**
     * 定義cache名稱、超時時長（秒）、最大容量
     * 每个cache缺省：10秒超时、最多缓存50000条数据，需要修改可以在                构造方法的参数中指定。
     */
    public enum Caches{
        getPersonById(5), //有效期5秒
        getSomething, //缺省10秒
        getOtherthing(300, 1000), //5分钟，最大容量1000
        myAcctoken(7200,5000);
        ;

        Caches() {
        }

        Caches(int ttl) {
            this.ttl = ttl;
        }

        Caches(int ttl, int maxSize) {
            this.ttl = ttl;
            this.maxSize = maxSize;
        }

        private int maxSize=DEFAULT_MAXSIZE;    //最大數量
        private int ttl=DEFAULT_TTL;        //过期时间（秒）

        public int getMaxSize() {
            return maxSize;
        }
        public int getTtl() {
            return ttl;
        }
    }
    
    /**
     * 创建基于Caffeine的Cache Manager
     * @return
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        ArrayList<CaffeineCache> caches = new ArrayList<CaffeineCache>();
        for(Caches c : Caches.values()){
            caches.add(new CaffeineCache(c.name(), 
                Caffeine.newBuilder().recordStats()
                .expireAfterWrite(c.getTtl(), TimeUnit.SECONDS)
                .maximumSize(c.getMaxSize())
                .build())
            );
        }

        cacheManager.setCaches(caches);

        return cacheManager;
    }
    
    @Bean
    public Cache<Object, Object> cache(){
    	Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS) //最后一次写入后经过固定时间过期 
                .expireAfterAccess(2,TimeUnit.HOURS)   //最后一次写入或访问后经过固定时间过期
                .maximumSize(1000)
                .build();
    	 return cache;
    }
    
    @Bean
    public Cache<Object, Object> cacheToken(){
    	Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) //最后一次写入后经过固定时间过期 
                .expireAfterAccess(30,TimeUnit.MINUTES)   //最后一次写入或访问后经过固定时间过期
                .maximumSize(1000)
                .build();
    	 return cache;
    }
     
   
}
