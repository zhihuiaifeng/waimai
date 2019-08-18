package com.XZYHOrderingFood.back.caffeine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;

@Component
public class TestCache {
  @Autowired
  private CacheConfig cacheConfig;
  
//	 public void main(String[] args) { Cache<Object, Object> cache =
//	  cacheConfig.cache(); cache.put("key", "value"); //添加缓存
//	  cache.invalidate("key");//通过key删除缓存 Object object =
//	  cache.getIfPresent("key"); //获取缓存value
//
//
//	  //清楚缓存的所有数据 cacheConfig.cache().invalidateAll();
//	  cacheConfig.cache().cleanUp(); }

}
