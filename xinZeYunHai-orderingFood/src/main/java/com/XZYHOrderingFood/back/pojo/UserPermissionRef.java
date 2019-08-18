package com.XZYHOrderingFood.back.pojo;

import lombok.Data;

@Data
public class UserPermissionRef {
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 权限id
     */
    private String permissionId;
 
}