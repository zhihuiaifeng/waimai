package com.XZYHOrderingFood.back.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.dao.FanControlMapper;
import com.XZYHOrderingFood.back.pojo.FanControl;
import com.XZYHOrderingFood.back.pojo.QrCode;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.Coder;
import com.XZYHOrderingFood.back.util.Config;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.weixin.wxsdk.WXPayUtil;
import com.github.vioao.wechat.api.UserApi;
import com.github.vioao.wechat.bean.entity.user.User;
import com.github.vioao.wechat.bean.response.user.FollowResponse;
import com.github.vioao.wechat.bean.response.user.UserListResponse;
import com.github.vioao.wechat.bean.response.user.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FansService {
	@Autowired
	private CacheConfig cacheConfig;

	@Autowired
	private FanControlMapper fanControlMapper;

	@Autowired
	private Config config;

	private List<String> openIds = new ArrayList<String>();

	public static final String MSGTYPE_EVENT = "event";// 消息类型--事件
	public static final String MESSAGE_SUBSCIBE = "subscribe";// 消息事件类型--订阅事件
	public static final String MESSAGE_UNSUBSCIBE = "unsubscribe";// 消息事件类型--取消订阅事件
	public static final String SCAN = "SCAN"; //少描二维码,并且用户已经关注公众号
	
	public static final String MESSAGE_TEXT = "text";// 消息类型--文本消息

	public static final Integer IS_FOCUS_Y = 0; // 取消关注

	public static final Integer IS_FOCUS_N = 1;// 关注

	public Result<List<User>> fansList(User user, String nextOpenid) {

		FollowResponse followResponse = UserApi
				.getUserOpenIds((String) cacheConfig.cache().getIfPresent(PublicDictUtil.ACCESS_TOKEN), nextOpenid);
		openIds.addAll(followResponse.getData().getOpenid());
		if (followResponse.getData().getOpenid().size() > 10000) {

			fansList(null, followResponse.getNextOpenid());
		}
		List<User> UserList = new ArrayList<User>();

		if (openIds.size() > 0) {
			// 批量查询用户信息
			int lag = openIds.size() % 100;
			int count = openIds.size() / 100;
			int group = lag > 0 ? lag + 1 : count;
			String acctoken = (String) cacheConfig.cache().getIfPresent(PublicDictUtil.ACCESS_TOKEN);
			if (openIds.size() > 0 && openIds.size() <= 100) {
				UserListResponse ulr = UserApi.batchGetUserInfo(acctoken, "zh_CN", openIds);
				UserList.addAll(ulr.getUserInfoList());
			} else if (openIds.size() > 0 && openIds.size() > 100) {
				Map<String, List<String>> map = groupList(openIds);
				for (List<String> value : map.values()) {
					UserList.addAll(UserApi.batchGetUserInfo(acctoken, "zh_CN", value).getUserInfoList());
				}
			}
		}
		// PageHelper.startPage(this.pageNo , this.pageSize);

		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, UserList);
	}

	/**
	 * map分组
	 */
	public Map<String, List<String>> groupList(List<String> list) {

		int listSize = list.size();
		int toIndex = 100;
		Map<String, List<String>> map = new HashMap(); // 用map存起来新的分组后数据
		int keyToken = 0;
		for (int i = 0; i < list.size(); i += 100) {
			if (i + 100 > listSize) { // 作用为toIndex最后没有100条数据则剩余几条newList中就装几条
				toIndex = listSize - i;
			}
			List<String> newList = list.subList(i, i + toIndex);
			map.put("keyName" + keyToken, newList);
			keyToken++;
		}

		return map;
	}

	/*
	 * 响应订阅事件--通过opid 查询用户并加入数据库
	 */
	public String subscribeForText(String toUserName, String fromUserName,String eventKey) {
		String acctoken = (String) cacheConfig.cache().getIfPresent(PublicDictUtil.ACCESS_TOKEN);
		UserResponse ur = UserApi.getUserInfo(acctoken, "zh_CN", fromUserName);
		if (ur != null) {
			FanControl fanControl = new FanControl();
			fanControl.setId(Sid.nextShort());
			fanControl.setCreateTime(new Date());
			fanControl.setIsFocus(IS_FOCUS_Y);
			if(ur.getSubscribeTime() != null) {
				fanControl.setFocusTime(new Date(ur.getSubscribeTime()));
			}
			
			fanControl.setHeadImgAddress(ur.getHeadimgurl());
			fanControl.setOpenId(ur.getOpenid());
			fanControl.setWechatName(ur.getNickname());
			fanControl.setTempEventKey(eventKey);
			int count = fanControlMapper.insertSelective(fanControl);
			if (count < 0) {
				log.info("用户:" + fromUserName + "信息添加失败");
			}
		} else {
			log.info("用户:" + fromUserName + "信息获取失败");
		}
		// return textMsg(toUserName, fromUserName, "欢迎关注，精彩内容不容错过！！！");
		return "";
	}

	/*
	 * 响应取消订阅事件 --通过openId 删除数据库用户
	 */
	public String unsubscribe(String toUserName, String fromUserName) {
		// TODO 可以进行取关后的其他后续业务处理
		FanControl fanControl = new FanControl();
		fanControl.setOpenId(fromUserName);
		fanControl.setIsFocus(IS_FOCUS_N);
		int count = fanControlMapper.updateByOpenId(fanControl);
		if (count < 0) {
			log.info("用户" + fromUserName + "取消关注失败");
		}
		return "";
	}

	/*
	 * 组装文本消息
	 */
	/*
	 * public static String textMsg(String toUserName,String fromUserName,String
	 * content){ TextMessage text = new TextMessage();
	 * text.setFromUserName(toUserName); text.setToUserName(fromUserName);
	 * text.setMsgType(MESSAGE_TEXT); text.setCreateTime(new Date().getTime());
	 * text.setContent(content); return XmlUtil.textMsgToxml(text); }
	 */

	public void isFollow(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// 把微信返回的xml信息转义成map
		Map<String, String> map = WXPayUtil.xmlToMapReq(req);
		String message = "success";
		String fromUserName = map.get("FromUserName");// 消息来源用户标识
		String toUserName = map.get("ToUserName");// 消息目的用户标识
		String msgType = map.get("MsgType");// 消息类型
		String content = map.get("Content");// 消息内容
		String eventType = map.get("Event");
		 
		// PrintWriter out = resp.getWriter();
		if (MSGTYPE_EVENT.equals(msgType)) {// 如果为事件类型
			if (MESSAGE_SUBSCIBE.equals(eventType)) {// 处理订阅事件
				String eventKey = map.get("EventKey").replaceAll("qrscene_", ""); // 事件KEY值，qrscene_为前缀，后面为二维码的参数值
				message = this.subscribeForText(toUserName, fromUserName,eventKey);
			} else if (MESSAGE_UNSUBSCIBE.equals(eventType)) {// 处理取消订阅事件
				message = this.unsubscribe(toUserName, fromUserName);
			}else if(SCAN.equals(eventType)) { //少描二维码,并且关注公众号
				String eventKey = String.valueOf(map.get("EventKey"));
				message = this.scanQr(toUserName, fromUserName,eventKey);
			}
		}

		PrintWriter out = resp.getWriter();
		out.println(message);
		if (out != null) {
			out.close();
		}

	}

	private String scanQr(String toUserName, String fromUserName, String eventKey) {
		System.out.println(fromUserName+"扫描带参数二维码已经关注");
		return null;
	}

	public void checkToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("嗨,微信小同学,您进来啦!");
		Enumeration pNames = req.getParameterNames();
		while (pNames.hasMoreElements()) {
			String name = (String) pNames.nextElement();
			String value = req.getParameter(name);
			// out.print(name + "=" + value);

			String logStr = "name =" + name + "     value =" + value;
			log.error(logStr);
		}

		String signature = req.getParameter("signature");/// 微信加密签名
		String timestamp = req.getParameter("timestamp");/// 时间戳
		String nonce = req.getParameter("nonce"); /// 随机数
		String echostr = req.getParameter("echostr"); // 随机字符串

		// 验证签名是否一致
		log.info("---------------开始校验微信签名开始-------------");
		log.info("token:" + config.getSelfToken() + "=============" + "微信时间戳:" + timestamp + "=============" + "微信随机数:"
				+ nonce + "=============" + "微信随机字符串" + echostr);
		// 排序
		String sortString = Coder.sort(config.getSelfToken(), timestamp, nonce);
		// 加密
		String myString = Coder.sha1(sortString);

		if (StringUtils.isNotBlank(myString) && StringUtils.isNotBlank(signature) && myString.equals(signature)) {
			log.info("---------------校验微信签名成功!-------------返回原样字符串啊:" + echostr);
			PrintWriter out = resp.getWriter();
			out.print(echostr);
			out.close();
		}
		log.info("---------------校验微信签名失败-------------");
	}

	public  FanControl selectFansInfo(FanControl fanControl) {
		return  fanControlMapper.findFanControlInfo(fanControl);
	}

	public int addFans(FanControl fanControl) {
		return fanControlMapper.insertSelective(fanControl);
	}

}
