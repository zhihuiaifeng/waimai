package com.XZYHOrderingFood.back.pojo;


import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class FanControl extends ParamBean {
	/**
	 * 主键id
	 */
    private String id;
    /**
	 * 商铺id
	 */
    private String tuserId;
    /**
	 * 存头像的地址
	 */
    private String headImgAddress;
    /**
	 * 微信昵称
	 */
    private String wechatName;
    /**
	 * 姓名
	 */
    private String username;
    /**
	 * 手机号
	 */
    private String phone;
    /**
	 * 关注的日期
	 */
    @JsonFormat(pattern = "yyyy-MM-dd" ,timezone = "GMT+8")
    private Date focusTime;
    /*
        查询起始时间
     */
    private Date startTime;
    /*
        查询结束时间
     */
    private Date endTime;

    private String createId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;
    /**
     * 是否关注
     */
    private Integer isFocus;
    /*
        用于接收前台传入的时间段
     */
    private String newFanLineChart;

    /**
     * 微信唯一openid
     */
    private String openId;
    
    /**
     * 临时场景参数 例如商户id ,餐桌号
     */
    private String tempEventKey;

}