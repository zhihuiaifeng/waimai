package com.XZYHOrderingFood.back.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class TPermission {
    private String id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 0:停用,1:启用
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
 
}