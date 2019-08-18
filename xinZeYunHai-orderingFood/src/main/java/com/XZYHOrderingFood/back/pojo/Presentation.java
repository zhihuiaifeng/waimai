package com.XZYHOrderingFood.back.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class Presentation extends ParamBean {
	/**
	 * 主键id
	 */
    private String id;
    /**
	 * 申请提现当前时间
	 */
    private Date cashoutTime;
    /**
	 * 申请提现金额
	 */
    private BigDecimal cashoutMoney;
    /**
	 *未提现金额
	 */
    private BigDecimal uncashoutMoney;
    /**
	 * 商户id
	 */
    private String tuserId;
    /**
	 *是否提现 0-不通过 1-申请提现 2-申请通过 3-已提现
	 */
    private Integer cashoutFlag;
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