package com.XZYHOrderingFood.back.pojo;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TUser extends ParamBean {
    /*
        主键
     */
    private String id;
    /*
        名称
     */
    private String username;
    /*
        密码
     */
    private String password;
    /*
        是否为商户
     */
    private Integer isShops;
    /*
        是否第一次登陆
     */
    private Integer isFirstLogin;
    /*
        是否到期
     */
    private Integer isActive;
    /*
        客户名称
     */
    private String clientname;
    /*
        省
     */
    private String province;
    /*
        市
     */
    private String city;
    /*
        区
     */
    private String area;
    /*
        省市区集合
     */
    //@JsonIgnore(value = true)
    List<String> location;
    /**
     * 辅助参数
     */
    List<Integer> intLocations;
    /*
        详细地址
     */
    private String address;
    /*
        联系人姓名
     */
    private String contactName;
    /*
        联系人电话
     */
    private String contactPhone;
    /*
        唯一的openid
     */
    private String openId;
    /*
        最大餐桌数量
     */
    private Integer tableMaxNumber;
    /*
        最大餐桌数flag
     */
    private Integer tableMaxNumberFlag;
    /*
        使用年限启始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm" ,timezone = "GMT+8")
    private Date startTime;
    /*
        使用年限截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm" ,timezone = "GMT+8")
    private Date endTime;
    /*
        盐
     */
    private String salt;
 
    /**
   	 *  联系人电话
   	 */
    private String otpCode;
    /**
   	 *  联系人电话
   	 */
    private String createId;

    /**
     * 1:启用,0:停用
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;

    /*
        过期时间（手机验证码专用）
     */
    private Date otpOverTime;

    /**
     * 附加字段 权限集合
     */
   private List<TPermission> perList = new ArrayList<TPermission>();

   /**
    * 附加字段 ref_id
    */
   private String refId;
 

    /**
     * 新增字段 用于接折线图前台传 时限
     */
    private String newShopLinechart;

    /**
     * 到期天数
     */
    private Integer expireTime;
    /*
        到期天数标志
     */
    private Integer expireTimeFlag;
    /**
     * 座机区号
     */
    private String areaCode;

    /**
     * 座机号
     */
    private String airlineNumber;
    /*
        小票机是否启用 0-未启用 1-启用
     */
    private Integer ticketIsUsed;
    /*
        商家公告
     */
    private String shopNotice;
    /*
        停车场位置信息
     */
    private String parkSpace;
    /*
        营业起始时间
     */
    @JsonFormat(pattern = "HH:mm" ,timezone = "GMT+8")
    private String businessHoursBegin;
    /*
        营业结束时间
     */
    @JsonFormat(pattern = "HH:mm" ,timezone = "GMT+8")
    private String businessHoursEnd;
    /*
        商家图片
     */
    private String shopImg;
    /*
        特色服务
     */
    private String specialService;
    /*
       起始时间和结束时间的集合
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm" ,timezone = "GMT+8")
    private List<Date> dateMap;
    /*
        商店是否关门不营业 0-不营业 1-营业
     */
    private Integer ticketStatus;
}