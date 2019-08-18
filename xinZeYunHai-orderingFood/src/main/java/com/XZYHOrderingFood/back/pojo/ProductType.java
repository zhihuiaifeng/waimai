package com.XZYHOrderingFood.back.pojo;

import com.XZYHOrderingFood.back.util.ParamBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ProductType extends ParamBean {
    /*
        主键id
     */
    private String id;
    /*
        用户姓名
     */
    private String tuserId;
    /*
        菜单名称
     */
    private String meunName;
    /*
        菜单id
     */
    private String meunId;
    /*
        能否编辑
     */
    private Integer isEdit;

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
        是否删除
     */
    private Integer isDelete;
    
    /**
     * 排序字段
     */
    private Integer sort;
    
    /**
     * 辅助字段
     */
    private List<ProductType> ptList = new ArrayList<ProductType>();

    /**
     * 菜品详情集合
     */
    private List<ProductList> productLists;
    /**
     * 总数
     */
    private Integer typeInitNumber;
}