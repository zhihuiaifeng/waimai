package com.XZYHOrderingFood.back.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "tox.config")
@Getter
@Setter
public class Config {
 
	private String ossUrl;//oss url地址
	
	private String ossImgUrl;//oss 图片访问域名
	
	private String ossAccessId;//oss AccessId
	
	private String ossAccessKey;//oss AccessKey
	
	private String ossBucketName;//oss BucketName
	
	private String ossStyleName;//oss图片样式名称
	
	private String ossEndPoint;
	
	private String localhostPath;//本地缓存
	
	private String serverUrl;//服务器地址
	
	private String indexSlideshow;//首页轮播图
	
	private String uploadPath;//上传路径

	private String shopImg;// 商铺图片
	
	private String industryImg;//行业图片
	
	private String caseImg; //案例图片
	
	private String productImg;//商品图片
	/**
	 * 小票机
	 */
	private String clientId;
	private String clientSecret;
	
	/**
	 * 阿里短信发送
	 */
	private String accessKeyId;
	private String accessSecret;
	
	/**
	 * 微信
	 */
	//自定义私有token 便于验证公众平台URL时使用
	private String selfToken;
	/**
	 * 点餐宝app跳转url
	 */
	private String appPageUrl;
	
	/**
	 * 二维码
	 */
	private String qrImg;
	
}
