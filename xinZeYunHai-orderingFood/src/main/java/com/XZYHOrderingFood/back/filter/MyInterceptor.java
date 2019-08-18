package com.XZYHOrderingFood.back.filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;

import lombok.extern.slf4j.Slf4j;

//@CrossOrigin(origins="*",maxAge=3600)
@Component
@Slf4j
public class MyInterceptor implements HandlerInterceptor {

	@Autowired
	private CacheConfig cacheConfig;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		 
		 Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			return true;
		}
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		final Method method = handlerMethod.getMethod();
		final Class<?> clazz = method.getDeclaringClass();
		Cache<Object, Object> cache = cacheConfig.cacheToken();
		log.info("============handler传过来的token为:"
				+ request.getHeader("token"));

		if (clazz.isAnnotationPresent(Auth.class) || method.isAnnotationPresent(Auth.class)) {
			if (StringUtils.hasLength(request.getHeader("token"))) {
				//Object obj = request.getSession().getAttribute(PublicDictUtil.USER_TOKE);
				Object obj = cache.getIfPresent(request.getHeader("token"));
				if (obj != null) {
					log.info("token有效，开始调用");
					return true;
					//String sessid = (String) obj;
					/*
					 * if (cache.getIfPresent(PublicDictUtil.USER_TOKE).equals(request.getHeader(
					 * "token"))) { log.info("token有效，开始调用"); return true; }
					 */
				}

			}
			/*
			 * resultMap.put(PublicDictUtil.KEY, PublicDictUtil.LOGIN_ERRO_VALUE);
			 * resultMap.put(PublicDictUtil.MSG_KEY, "登录失效，请重新登录"); log.info("登录失效，请重新登录");
			 * String json = JSON.toJSONString(resultMap);
			 */
			//response.getWriter().print(new String(json.getBytes("UTF-8")));
			throw new CustomException("登录失效，请重新登录", PublicDictUtil.TOKEN_INVAALID);
			//return false;
		}

		return true;
	}

	/*
	 * @Override public void postHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, ModelAndView modelAndView)
	 * throws Exception { // TODO Auto-generated method stub log.
	 * info("=======================进入拉拦截器的 postHandle 方法 ======================");
	 * HandlerInterceptor.super.postHandle(request, response, handler,
	 * modelAndView); }
	 */
}
