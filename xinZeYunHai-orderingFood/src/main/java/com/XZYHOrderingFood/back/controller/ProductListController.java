package com.XZYHOrderingFood.back.controller;


import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.ProductList;
import com.XZYHOrderingFood.back.service.ProductListService;
import com.XZYHOrderingFood.back.util.BasePage;
import com.XZYHOrderingFood.back.util.Result;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/product/list")
@Auth
public class ProductListController {

    @Autowired
    private ProductListService productListService;

    /**
     * 添加单品信息
     */
    @PostMapping("add")
    public Result add(ProductList productList,MultipartFile pImg,HttpServletRequest request) {
    	return productListService.add(productList,pImg,request);
    }
    
    /**
     * 编辑商品
     */
    @PostMapping("edit")
    public Result edit(@RequestBody ProductList productList,MultipartFile pImg,HttpServletRequest request) {
    	return productListService.edit(productList,pImg,request);
    }
    
    /**
     * 删除单品
     */
    @GetMapping("del")
    public Result del(String id) {
    	return productListService.del(id);
    }
    
    /**
     * 上架or下架
     */
    @GetMapping("editActive")
    public Result editActive(String id) {
    	return productListService.editActive(id);
    }
    
    /**
     * 分页列表
     */
    @PostMapping("pageList")
    public Result<BasePage<ProductList>> pageList(@RequestBody ProductList productList,HttpServletRequest request) {
    	return  productListService.pageList(productList,request);
    }

    /**
     * @description: 编辑时的 通过id查询
     * @author: zpy
     * @date: 2019-08-01
     */
    @PostMapping("select")
    public Result selectById (@RequestBody ProductList productList) {
        return productListService.selectProductListById(productList);
    }

    /**
     * @description: 编辑时的 上传单品图片
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    @PostMapping("uploadimg")
    public Result uploadImgByEdit (ProductList productList, MultipartFile pImg) {
        return productListService.uploadImgByEdit(productList, pImg);
    }

    /**
     * @description: 查询该商家的全部商品
     * @author: zpy
     * @date: 2019-08-03
     * @params:
     * @return:
     */
    @PostMapping("shop/select")
    public Result selectProductListByUserId (HttpServletRequest httpServletRequest) {
        return productListService.getProductListByUserId(httpServletRequest);
    }
}
