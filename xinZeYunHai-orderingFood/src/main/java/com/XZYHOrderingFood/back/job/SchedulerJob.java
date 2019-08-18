package com.XZYHOrderingFood.back.job;
 
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.dao.TUserMapper;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.pojo.DayCompare;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.TUserService;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.vioao.wechat.api.TokenApi;
import com.github.vioao.wechat.bean.response.token.TokenResponse;
import com.github.vioao.wechat.bean.response.user.UserResponse;

import lombok.extern.slf4j.Slf4j;


/** 
 * 在线 cron生成器
 * https://www.pppet.net/
 * 
 * 通过@PostConstruct方法实现初始化bean进行操作
 */

@Component
@Slf4j
public class SchedulerJob  {
	
	@Autowired
	private CacheConfig cacheConfig;
	
	@Autowired
	private TUserMapper userMapper;

	@Value(value = "${weixin.appID}")
	private String appID;

	@Value(value = "${weixin.appSecret}")
	private String appSecret;



	public static final Integer IS_ACTIVE_D = 0;//到期

	public static final Integer IS_ACTIVE_N = 1;//未到期

	public static final Integer IS_ACTIVE_JJ = 2;//即将到期


	//@Scheduled(cron="0 */1 * * * *") //每一分钟执行一次
	public void cancelSessionId() {
		//获取sessioi
		ServletRequestAttributes  requestAttributes  = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		log.info("========定时任务==========session中"+requestAttributes.toString());
		//request.getSession().removeAttribute(PublicDictUtil.USER_TOKE);

	}


	//获取access_token 2小时更新一次  cron =  0 */2 * * *

	@Scheduled(cron="0 0 */2 * * ?") //每两小时执行一次
	//@Scheduled(cron="0 */1 * * * *") //每一分钟执行一次
		public void accessTokenManager() {
		Cache<Object, Object> cache = cacheConfig.cache();
		log.info("开始查询微信access_token");
		TokenResponse tokenBean = TokenApi.token(this.appID, this.appSecret);
		if(tokenBean.getErrcode() != 0) {
			log.info("token获取失败 ,原因为:"+tokenBean.getErrmsg());
			throw new CustomException(tokenBean.getErrmsg(),String.valueOf(tokenBean.getErrcode()));
		}
			//删除旧token 缓存新token
		cache.invalidate(PublicDictUtil.ACCESS_TOKEN);
		cache.put(PublicDictUtil.ACCESS_TOKEN, tokenBean.getAccessToken());
		log.info("查询微信access_token结束值为:"+cache.getIfPresent(PublicDictUtil.ACCESS_TOKEN)+"过期时间为:"+tokenBean.getAccessToken());
		}

	//更新商户们的到期天数  0 0 1 * * ?
	 @Scheduled(cron="0 0 1 * * ?") // 每天凌晨1点执行一次
	public void expireTime() {
		//查询出全部  >=起始时间 和 <= 截止时间 的商户 status = 1启用    is_active != 0   is_shops = 1

		List<TUser> userList = userMapper.findNoexpireTimeUser(new Date());
		List<TUser> paramList = new ArrayList<TUser>();
		userList.forEach(user -> {
			TUser tempUser = new TUser();
			tempUser.setId(user.getId());
			//计算每个商户的到期天数并且更新
				DayCompare dayCompare = DateUtil.dayCompare(new Date(), user.getEndTime());
				int days = dayCompare.getDay();
			tempUser.setExpireTime(days);
			if(days < 1) {
				//<1 到期
				tempUser.setIsActive(IS_ACTIVE_D);
			}else if(days <= 30 && days >= 1) {
				// 为即将到期
				tempUser.setIsActive(IS_ACTIVE_JJ);
			}else if(days > 30) {
				//未到期
				tempUser.setIsActive(IS_ACTIVE_N);
			}

			int count = userMapper.updateUserExpireTime(tempUser);
			log.info("用户:"+user.getClientname()+"到其状态更新成功");
			if(count < 0) {
				log.error("商户:"+user.getClientname()+"到期状态更新失败");
			}
		});

	}


	/*
	 * public static void main(String[] args) {
	 *
	 * }
	 */

}
