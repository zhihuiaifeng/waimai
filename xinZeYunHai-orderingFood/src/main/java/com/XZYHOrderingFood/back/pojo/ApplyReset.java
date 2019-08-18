package com.XZYHOrderingFood.back.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ApplyReset {
    private Integer id;
    /*
        用户名
     */
    private String username;
    /*
        商户名称
     */
    private String clientname;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private Integer isDelete;

    /*
        修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;

    /*
        审批状态  0-待审批  1-已审批
     */
    private Integer isAgree;

}