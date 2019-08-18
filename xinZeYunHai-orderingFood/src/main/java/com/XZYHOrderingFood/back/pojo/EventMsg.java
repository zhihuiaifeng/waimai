package com.XZYHOrderingFood.back.pojo;

import java.util.Date;

import lombok.Data;

/**
 * 微信时间消息
 * @author dell
 *
 */
@Data
public class EventMsg {
	/**
	 * 开发者微信号
	 */
	private String ToUserName;
	
	/**
	 * 发送方帐号（一个OpenID）
	 */
	private String FromUserName;
	
	/**
	 * 消息创建时间 （整型）
	 */
	private Long CreateTime;
	
	/**
	 * 	消息类型，event
	 */
	private String MsgType;
	
	/**
	 * 	事件类型，subscribe(订阅)、unsubscribe(取消订阅)
	 */
	private String Event;
	
	private String Content;
	private String MsgId;
}
