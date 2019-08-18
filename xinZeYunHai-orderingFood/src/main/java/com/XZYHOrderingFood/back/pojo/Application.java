package com.XZYHOrderingFood.back.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.Data;
@Data
 public class Application extends ParamBean {
	
	/**
	 * 主键id
	 */
	
    private String id;
    
    /**
	 *商户id
	 */
    
    private String tuserId;
   
    /**
	 *提现时间
	 */
    
    private Date cashoutTime;
   
    /**
  	 *提现金额
  	 */
    
    private BigDecimal cashoutMoney;
    
    /**
  	 *未提现金额
  	 */
    
    private BigDecimal uncashoutMoney;
   
    /**
  	 *0-不通过 1-申请提现 2-申请通过 3-已提现
  	 */
    
    private Integer cashoutFlag;
    /**
  	 *0-不通过 1-申请提现 2-申请通过 3-已提现
  	 */
    
    private String createId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date createTime;

    private String modifyId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date modifyTime;

    private String checkPersonId;



}