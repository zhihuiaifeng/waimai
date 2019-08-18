 
package com.XZYHOrderingFood.back.pojo;
 

import java.util.Date;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;
 
import lombok.Data;

@Data
public class ReserveInfomation extends ParamBean {
    /*
        主键id
     */
    private String id;
    /*
        手机号
     */
    private String phone;
    /*
        用户名
     */
    private String username;
    /*
        保存时间
     */
    private Date saveTime;
    /*
        需求信息
     */
    private String needInfo;
    /*
        联络状态
     */
    private Integer isContact;
    /*
        备注
     */
    private String remark;

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
        公司名称
     */
    private String companyName;
    /*
        手机验证码
     */
    private String verificationCode;
 
}