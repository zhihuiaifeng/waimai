package com.XZYHOrderingFood.back.pojo;

import com.XZYHOrderingFood.back.util.ParamBean;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class QrCode extends ParamBean {
    private String id;
    /*
        商铺id
     */
    private String tuserId;
    /*
        桌子的编号
     */
    private Integer tableNumber;
    /**
     *  0:登录  1:锁定 2:解锁
     */
    private Integer tableStatus;

    private String createId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;
    /*
        是否删除
     */
    private Integer isDelete;
    /*
        二维码图片链接地址
     */
    private String qrImgChain;
    /*
        二维码图片名称（拼接路径）
     */
    private String qrImgName;
    /**
     * 二维码图片标识
     */
    private String ticket;
    
    /**
     * 二维码 短连接
     */
    private String shortUrl;

}