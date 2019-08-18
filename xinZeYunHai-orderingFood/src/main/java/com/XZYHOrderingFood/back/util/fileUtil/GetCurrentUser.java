package com.XZYHOrderingFood.back.util.fileUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.dao.TUserMapper;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.TUserService;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.bingoohuang.utils.codec.Json;
 

/**
 * 获取当前用户
 * @author dell
 *
 */
@Component
public class GetCurrentUser {
     
	
	private static TUserService userService;
	
	private static CacheConfig staticCacheConfig;
	
	@Autowired
	private TUserService  service;
	
	@Autowired
	private CacheConfig cacheConfig;
	
	
	
	@PostConstruct 
	public void init() {
		userService = service;
		staticCacheConfig = cacheConfig;
	}
	
	public static TUser getCurrentUser(HttpServletRequest request) {
		String token = request.getHeader("token");
		Cache<Object, Object> cache = staticCacheConfig.cacheToken();
		Object object = cache.getIfPresent(token);
		
		/*
		 * String userId = JWT.decode(token).getAudience().get(0); TUser user =
		 * userService.getByUserId(userId);
		 */
		if(object != null) {
			return (TUser)object;
		}
		/*
		 * Object obj = request.getSession().getAttribute(PublicDictUtil.USER); if(obj
		 * != null) { return (TUser) obj; }
		 */
		return null;
	}
	
	public static String getToken(TUser user) {
		
        String token="";
        token= JWT.create().withAudience(user.getId())
                .sign(Algorithm.HMAC256(user.getUsername()));
        return token;
    }
}
