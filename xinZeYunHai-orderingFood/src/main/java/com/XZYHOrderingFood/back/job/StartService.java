package com.XZYHOrderingFood.back.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.vioao.wechat.api.TokenApi;
import com.github.vioao.wechat.bean.response.token.TokenResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动后 会按执行顺序执行run方法
 * 
 * @author Administrator
 *
 */
@Component
//@Order(value = 1)
@Slf4j
public class StartService implements ApplicationRunner {
	@Value(value = "${weixin.appID}")
	private String appID;

	@Value(value = "${weixin.appSecret}")
	private String appSecret;

	@Autowired
	private CacheConfig cacheConfig;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Cache<Object, Object> cache = cacheConfig.cache();

		log.info("开始查询微信access_token");

		TokenResponse tokenBean = TokenApi.token(this.appID, this.appSecret);
		if (tokenBean.getErrcode() != 0) {
			log.info("token获取失败 ,原因为:" + tokenBean.getErrmsg());
			throw new CustomException(tokenBean.getErrmsg(), String.valueOf(tokenBean.getErrcode()));
		} // 将token放入缓存 CacheConfig

		cache.put(PublicDictUtil.ACCESS_TOKEN, tokenBean.getAccessToken());

		log.info("查询微信access_token结束值为:" + tokenBean.getAccessToken() + "过期时间为:" + tokenBean.getAccessToken());

	}

}
