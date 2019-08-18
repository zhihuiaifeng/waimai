package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.service.OrderDetailsService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.annotion.Auth;

import javax.servlet.http.HttpServletRequest;

/**
 * banner控制器
 * @author dell
 *
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/orderdetals")
@Auth
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

    /**
     * @description: 查询该商户的餐品详情和餐品列表
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    @PostMapping("select")
    public Result getOrderDetailsById (HttpServletRequest httpServletRequest) {
        return orderDetailsService.getOrderDetailsByShopId(httpServletRequest);
    }

}
