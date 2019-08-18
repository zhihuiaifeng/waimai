package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.dao.OrderTableMapper;
import com.XZYHOrderingFood.back.pojo.OrderDetails;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.service.OrderTableService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.XZYHOrderingFood.back.annotion.Auth;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单 controller
 * @author dell
 *
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/ordertable")
@Auth
public class OrderTableController {

    @Autowired
    private OrderTableService orderTableService;

    /**
     * @description: 获取订单折线图数据
     * @author: zpy
     * @date: 2019-07-24
     * @params:
     * @return:
     */
    @PostMapping("bill/linechart")
    public Result<Map<String,Integer>> getOrderBillLineChart (@RequestBody OrderTable orderTable,HttpServletRequest httpServletRequest) {
        return orderTableService.getOrderTableLineChart(orderTable, httpServletRequest);
    }

    /**
     * @description: 查询该商户总订单金额
     * @author: zpy
     * @date: 2019-07-24
     * @params:
     * @return:
     */
    @PostMapping("shopmoney/select")
    public Result getAllBillMoneny(HttpServletRequest httpServletRequest){
        return orderTableService.getAllBillMoneny(httpServletRequest);
    }

    /**
     * @description: 单品销售数量折线图
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    @PostMapping("singlenum/select")
    public Result getSingleSumLineSelect (@RequestBody OrderTable orderTable, HttpServletRequest httpServletRequest) {
        return orderTableService.getSingleSumLineSelect(orderTable, httpServletRequest);
    }

    /**
     * @description: 用餐人数折线图查询 针对商户
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    @PostMapping("mealsnum/select")
    public Result getMealsNumberLine (@RequestBody OrderTable orderTable, HttpServletRequest httpServletRequest) {
        return orderTableService.getMealsNumberLine(orderTable, httpServletRequest);
    }

    /**
     * @description: 订单管理- 订单详情查询
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    @PostMapping("orderdetail/select")
    public Result getOrderDetailsInfo (@RequestBody OrderTable orderTable, HttpServletRequest httpServletRequest) {
        return orderTableService.getOrderDetailsInfo(orderTable, httpServletRequest);
    }

    /**
     * @description: 订单管理- 订单信息查询，一个列表
     * @author: zpy
     * @date: 2019-08-02
     * @params:
     * @return:
     */
    @PostMapping("orderinfo/select")
    public Result getOrderManageInfo (@RequestBody OrderTable orderTable, HttpServletRequest httpServletRequest) {
        return orderTableService.getOrderManageInfo(orderTable, httpServletRequest);
    }

    /**
     * @description: 订单状态改变接口
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    @PostMapping("orderinfo/update")
    public Result updateOrderTableById (@RequestBody OrderTable orderTable, HttpServletRequest httpServletRequest){
        return orderTableService.updateOrderTableById(orderTable, httpServletRequest);
    }

}
