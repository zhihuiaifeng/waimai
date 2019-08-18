package com.XZYHOrderingFood.back.redis;
import java.lang.reflect.Method;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching //启用注解
public class RedisConfig extends CachingConfigurerSupport{
    
	/**
     * 生成key的策略
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
    
    
    /**
     * RedisTemplate配置
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
    	 RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
         redisTemplate.setConnectionFactory(factory);

         // 使用Jackson2JsonRedisSerialize 替换默认序列化
         Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
         objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

         jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

         // 设置value的序列化规则和 key的序列化规则
         redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
         redisTemplate.setKeySerializer(new StringRedisSerializer());
         redisTemplate.afterPropertiesSet();
         
         return redisTemplate;
    }

}