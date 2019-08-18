package com.XZYHOrderingFood.back.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.XZYHOrderingFood.back.util.ParamBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Setter;
import org.apache.tika.config.Param;

@Data
//@Setter
public class TFlavor extends ParamBean {
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商户id
     */
    private String tuserId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 排序号
     */
    private Integer sort;
    
    /**
     * 辅助字段
     */
    private List<TFlavor> flavorList = new ArrayList<TFlavor>();
    /**
     * 口味数量
     */
    private Integer flovrInitNumber;
}