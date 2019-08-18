package com.XZYHOrderingFood.back.pojo;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ProductList extends ParamBean {
    /*
        主键
     */
    private String id;
    /*
        商品名称
     */
    private String productName;
    /*
        菜单名称
     */
    private String menuName;
    /*
        菜单id
     */
    private String menuId;
    /*
        是否热卖
     */
    private Integer isHotsell;
    /*
        是否是新品
     */
    private Integer isNewproduct;
    /*
        菜单价格
     */
    private BigDecimal price;
    /*
        菜单价格标志
     */
    private Integer priceFlag;
    /*
        是否固定月销量
     */
    private Integer isFixedsell;
    /*
        固定销售量的销售笔数
     */
    private Integer sellNumber;
    /*
        0-倒序  1-正序
     */
    private Integer sellNumberFlag;
    /*
        单品介绍
     */
    private String introduce;
    /*
        菜品剩余份数
     */
    private Integer restNumber;
    /*
        商铺id
     */
    private String tuserId;
    /*
        是否上架
     */
    private Integer isActive;

    private String createId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;
    /*
        商品图片
     */
    private String productImg;
    /*
        商品类型id
     */
    private String productTypeId;
    /*
         口味表数据
     */
    private List<TFlavor> tFlavors;
    /*
        口味名称
     */
    private String flavorName;
    /*
        微信openid
     */
    private String openId;
    /*
         传入数量
     */
    private Integer foodNumber;
    /*
        桌号
     */
    private String tableNumber;
    /*
        初始数量
     */
    private Integer initNumber;
    /**
     * 下表
     */
    private Integer subScript;
    /**
     * 单个商品总价格
     */
    private BigDecimal moneyCount;
}