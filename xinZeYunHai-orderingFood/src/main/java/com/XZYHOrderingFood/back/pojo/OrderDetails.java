package com.XZYHOrderingFood.back.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class OrderDetails extends ParamBean {
	/**
	 * 主键id
	 */
    private String id;
    /**
	 * 单价
	 */
    private BigDecimal price;
    /**
	 *单品名
	 */
    private String foodName;
    /**
	 * 数量
	 */
    private Integer foodNumber;
    /**
	 *订单id
	 */
    private String orderId;
    /**
	 * 商户id
	 */
    private String tuserId;

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
        下单时间
     */
    private Date orderTime;
    /*
        口味  -用于前台下单传口味
     */
    private TFlavor tFlavor;
    /*
        口味id 用于查询订单餐品的口味
     */
    private String tflavorId;
    /*
        口味名称
     */
    private String tflavorName;
    /*
        单品id
     */
    private String foodId;
    /*
        单品路径
     */
    private String productPath;
    /*
        总价
     */
    private BigDecimal moneyCount;
    /*
        单品id
     */
    private String productId;
    /*
        单品名称
     */
    private String productName;
}