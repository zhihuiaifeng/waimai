package com.XZYHOrderingFood.back.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class PresentationOrder extends ParamBean {
	/**
	 * 主键id
	 */
    private String id;
    /**
	 * 提款id
	 */
    private String presentationId;
    /**
	 * 商铺id
	 */
    private String tuserId;
    /**
	 * 订单id
	 */
    private String orderId;
    /**
	 * 申请表id
	 */
    private String applicationId;

    private String createId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;

}