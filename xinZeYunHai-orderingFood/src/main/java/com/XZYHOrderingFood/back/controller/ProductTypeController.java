package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.ProductType;
import com.XZYHOrderingFood.back.service.ProductTypeService;
import com.XZYHOrderingFood.back.util.Result;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/product/type")
@Auth
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    /**
     * 添加类型
     */
    @PostMapping("add")
    public Result add(@RequestBody ProductType productType, HttpServletRequest request) {
        return productTypeService.add(productType, request);
    }

    /**
     * 删除商品类型
     */
    @GetMapping("del")
    public Result del(String id) {
        return productTypeService.del(id);
    }

    /**
     * 排序
     */

    @PostMapping("editSort")
    public Result editSort(@RequestBody ProductType productType) {
        return productTypeService.editSort(productType);
    }

    /**
     * @description: 查询该商家全部商铺类型
     * @author: zpy
     * @date: 2019-07-30
     */
    @PostMapping("select")
    public Result selectAllType(HttpServletRequest httpServletRequest){
        return productTypeService.selectAllTypeByUserId(httpServletRequest);
    }
}
