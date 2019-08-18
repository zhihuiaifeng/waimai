package com.XZYHOrderingFood.back.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.service.FansService;
import com.XZYHOrderingFood.back.util.BasePage;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.vioao.wechat.bean.entity.user.User;

import lombok.extern.slf4j.Slf4j;

/**
 * 公众号粉丝管理
 * 
 * @author Administrator
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/fansManager")
@Slf4j
public class FansManager {
	
	@Autowired
	private FansService fansService;
	 

	/**
	 * 获取粉丝列表
	 */
	@GetMapping("fansList")
	@Auth
	public Result<List<User>> fansList(User user, String nextOpenid) {
		  return fansService.fansList(user,nextOpenid);
	}
	
	/**
	 * 关注/取消公众号 粉丝管理
	 * @throws Exception 
	 */
	@RequestMapping("isfollow")
	public void isFollow(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		  //fansService.checkToken(req,resp);
		  fansService.isFollow(req,resp);
	}
	
}
