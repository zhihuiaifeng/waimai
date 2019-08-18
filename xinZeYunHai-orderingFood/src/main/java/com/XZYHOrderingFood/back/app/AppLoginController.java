package com.XZYHOrderingFood.back.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.controller.FansManager;
import com.XZYHOrderingFood.back.dao.FanControlMapper;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.pojo.FanControl;
import com.XZYHOrderingFood.back.pojo.QrCode;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.redis.RedisUtils;
import com.XZYHOrderingFood.back.service.FansService;
import com.XZYHOrderingFood.back.service.QrCodeService;
import com.XZYHOrderingFood.back.service.TUserService;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.alibaba.fastjson.JSON;
import com.github.vioao.wechat.Const;
import com.github.vioao.wechat.api.SnsApi;
import com.github.vioao.wechat.bean.response.sns.SnsTokenResponse;
import com.github.vioao.wechat.bean.response.user.UserResponse;
import com.github.vioao.wechat.utils.Params;
import com.github.vioao.wechat.utils.UrlUtils;
import com.github.vioao.wechat.utils.client.HttpUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * app/h5 微信登录
 * https://blog.csdn.net/sihai12345/article/details/80929199
 * window.location.href
 * @author dell
 *
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
@RequestMapping("/")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class AppLoginController {
	@Value("${weixin.redirectUri}")
	private String redirectUri;
	
	@Value("${weixin.appID}")
	private String appID;
	
	@Value("${weixin.appSecret}")
	private String appSecret;
	
	@Autowired
	private TUserService userService;
	
	@Autowired
	private FansService fansService;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private QrCodeService qrCodeService;
	
	public static final Integer IS_FOCUS_Y = 0; // 取消关注

	public static final Integer IS_FOCUS_N = 1;// 关注
	
	public static final Integer TABLE_STATUS_LOGIN = 0;//登录
	public static final Integer TABLE_STATUS_LOK=1;//锁定
	public static final Integer TABLE_STATUS_OPEN=2;//解锁
	
	private static final String SNS_OAUTH2_TOKEN = Const.Uri.API_URI + "/sns/oauth2/access_token";
	
	private static final String SNS_USER_INFO = Const.Uri.API_URI + "/sns/userinfo";
	
	 public static String getOath2Url(String appId, String redirectUri, boolean detail, String state) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(Const.Uri.OPEN_URI + "/connect/oauth2/authorize?")
	                .append("appid=").append(appId)
	                .append("&redirect_uri=").append(UrlUtils.encode(redirectUri))
	                .append("&response_type=code")
	                .append("&scope=").append(detail ? "snsapi_userinfo" : "snsapi_base")
	                .append("&state=").append(state == null ? "" : state);
	        sb.append("&connect_redirect=1#wechat_redirect");
	        log.info("拼接后:============================" + redirectUri );
	        return sb.toString();
	    }
	
	 
	 /**
	     * 第二步：通过code换取网页授权access_token.
	     *
	     * @param appId  公众号的唯一标识
	     * @param secret 公众号的appsecret
	     * @param code   填写第一步获取的code参数
	     */
	    public  SnsTokenResponse oauth2AccessToken(String appId, String secret, String code) {
	        Map<String, String> params = Params.create("appid", appId).put("secret", secret).put("code", code)
	                .put("grant_type", "authorization_code").get();
	        return HttpUtil.getJsonBean(SNS_OAUTH2_TOKEN, params, SnsTokenResponse.class);
	    }
	 
	
	/**
	 * 获取授权URL。
	 */
	@GetMapping("authorizeUrl")
	@ResponseBody
	public Result authorizeUrl(String tUserId,String tableNum) {
		 redirectUri = redirectUri + "?" +"tUserId="+tUserId+"&tableNum="+tableNum;
		 String paramUrlString = getOath2Url(appID, redirectUri, true, "www.xinzeyunhai.com");
		 log.info("授权url:"+paramUrlString);
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, paramUrlString);
	}
	
	/**
	 * 微信回调
	 * @throws IOException 
	 */
	@RequestMapping("appLogin")
	public String appLogin(@RequestParam("code") String code,
                     @RequestParam("state") String returnUrl,
                     @RequestParam("tUserId") String tUserId,
                     @RequestParam("tableNum") String tableNum,
                     HttpServletResponse response) throws IOException {
		log.info("微信授权回来了........=====================哈哈哈" +appID+" "+ appSecret + " "+ code);
		
		//获取用户微信信息
		UserResponse userResponse = new UserResponse();
		if(!redisUtils.exists(code)) {
			SnsTokenResponse snsTokenResponse = SnsApi.oauth2AccessToken(this.appID,this.appSecret,code);
			  
			if(snsTokenResponse.getErrcode() != 0) {
				log.error("通过code换取网页授权access_token 失败!!!!!!错误码:"+snsTokenResponse.getErrcode()+"错误信息"+ snsTokenResponse.getErrmsg());
			}
			
			redisUtils.set(code, code, 18000L);
			log.info(JSON.toJSONString(snsTokenResponse));
			userResponse =  getUserInfo(snsTokenResponse.getAccessToken(),snsTokenResponse.getOpenid(),"zh_CN");
			if(userResponse.getErrcode() != 0) {
				log.error("拉取用户信息(需scope为 snsapi_userinfo) 失败!错误码:"+userResponse.getErrcode()+"错误信息:"+userResponse.getErrmsg());
			}
			log.info("用户的openId为:"+userResponse.getOpenid()+"昵称"+userResponse.getNickname());
		} 
		
		
		//查看数据库是否拥有该粉丝用户
		 FanControl fanControl = new FanControl();
		 fanControl.setOpenId(userResponse.getOpenid());
		 fanControl = fansService.selectFansInfo(fanControl);
		 if(fanControl == null) {
			 fanControl = new FanControl();
			 //将该粉丝插入数据库
				fanControl.setId(Sid.nextShort());
				fanControl.setCreateTime(new Date());
				fanControl.setIsFocus(IS_FOCUS_Y);
				if(userResponse.getSubscribeTime() != null) {
					fanControl.setFocusTime(new Date(userResponse.getSubscribeTime()));
				}
				
				fanControl.setHeadImgAddress(userResponse.getHeadimgurl());
				fanControl.setOpenId(userResponse.getOpenid()); //new String(fileName.getBytes("GBK"), StandardCharsets.ISO_8859_1)
				fanControl.setWechatName(new String(userResponse.getNickname().getBytes(),StandardCharsets.ISO_8859_1));
				fanControl.setTempEventKey(tUserId+","+tableNum);
				
				int count = fansService.addFans(fanControl);
				if(count <= 0) {
					throw new CustomException(PublicDictUtil.ERROR_VALUE,"粉丝插入失败");
				}
		 }
		//查看该用用户是否登录
		 Object obj = redisUtils.get(userResponse.getOpenid());
		 if(obj == null) {
			 //放入缓存
			 redisUtils.set(userResponse.getOpenid(), JSON.toJSONString(userResponse), 18000L);
		 }
		//编辑当前商户的二维码状态 为进入状态
		  QrCode qrCode = new QrCode();
		  qrCode.setTuserId(tUserId);
		  qrCode.setTableStatus(TABLE_STATUS_LOGIN);
		  int cont = qrCodeService.updateTableStatusByUserId(qrCode);
		  if(cont<= 0) {
			  throw new CustomException(PublicDictUtil.ERROR_VALUE,"二维码状态更新失败");
		  }
		//重定向到app首页 携带当前用户的openId
		  String path = "/index.html" + "?openId=" + userResponse.getOpenid()+"&nickName="+userResponse.getNickname();
		  log.info("重定向路径为:" + path+"-=-==-=-=-==========");
		  //response.sendRedirect(returnUrl+"/myhome.html"+ "?openId=" + userResponse.getOpenid());
		  return "redirect:"+ path;
	}
	
	/*
	 * @RequestMapping("myhome") public String myhome() {
	 * log.info("=====================进入首页========================="); return
	 * "redirect:" + "myhome.html"; }
	 */
	
	
	 /**
     * 拉取用户信息(需scope为 snsapi_userinfo).
     *
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openid      用户的唯一标识
     * @param lang        国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     */
    public static UserResponse getUserInfo(String accessToken, String openid, String lang) {
        Map<String, String> params = Params.create("access_token", accessToken).put("openid", openid)
                .put("lang", lang).get();
        return HttpUtil.getJsonBean(SNS_USER_INFO, params, UserResponse.class);
    }
}
