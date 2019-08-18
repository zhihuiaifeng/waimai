package com.XZYHOrderingFood.back.pojo;

import lombok.Data;

@Data
public class TScene {
    private Integer id;

    /**
     * 永久二维码时最大值为100000
     */
    private Integer sceneIdCount;

    /**
     * 当前使用到scene值
     */
    private Integer restSceneCount;
 
}