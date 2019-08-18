package com.XZYHOrderingFood.back.pojo;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderTable extends ParamBean {
    /*
        主键id
     */
    private String id;
    /*
        下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd" ,timezone = "GMT+8")
    private Date orderTime;
    /*
        订单金额
     */
    private BigDecimal totalMoney;
    /*
        支付状态
     */
    private Integer orderStatus;
    /*
        桌号
     */
    private String tableNumber;
    /*
        商户id
     */
    private String tuserId;
    /*
        是否提现
     */
    private Integer isCashout;
    /*
        二维码id
     */
    private String qrCodeId;

    private String createId;

    private Date createTime;

    private String modifyId;

    private Date modifyTime;
    /*
        订单编号
     */
    private String orderCode;
    /*
        用于接前台传入时段
     */
    private String orderTableLineChart;

    /*
        用餐人数
     */
    private Integer mealsNumber;
    /*
        是否删除 0-已删除 1-未删除
     */
    private Integer isDelete;
    /*
        订单金额区间 0-小于100 1-101到300 2-301到500 3-501到1000 4-1001到2000 5-2000以上
     */
    private Integer orderMoneyInterval;
    /*
        微信openid
     */
    private String openId;
    /*
        订单详情列表
     */
    private List<OrderDetails> orderDetails;
    /*
        备注
     */
    private String remark;
    /*
        单品id
     */
    private String foodId;
    /*
        单品name
     */
    private String foodName;
}