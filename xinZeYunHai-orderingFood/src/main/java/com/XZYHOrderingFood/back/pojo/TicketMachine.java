
package com.XZYHOrderingFood.back.pojo;
 
import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class TicketMachine extends ParamBean {
	/**
	 * 主键id
	 */
    private String id;
    /**
	 *机器名
	 */
    private String machineName;
    /**
	 * 打印内容
	 */
    private String printContent;
    /**
	 * 商户表
	 */
    private String tuserId;
    /**
	 *创建人姓名
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
    /*
        小票机是否启用
     */
    private Integer isUsed;
    
    /**
     * 机器名
     */
    private String machineCode;
    /**
     * 易联云终端密钥
     */
    private String msign;
    /**
     * 商铺名称
     */
    private String shopsName;
    /**
     * 版本号
     */
    private String version;
    /**
     * 是否锁定 1:锁定 0:未锁定
     */
    private Integer isLocking;
    
    /**
     * 绑定状态
     */
    private Integer bangdingStatus;
    /*
        图片路径
     */
    private String imgPath;
    /*
        是否营业 0-no 1-yes
     */
    private Integer isBusiness;
}