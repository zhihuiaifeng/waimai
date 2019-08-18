package com.XZYHOrderingFood.back.app;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.ProductList;
import com.XZYHOrderingFood.back.pojo.ProductType;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.*;
import com.XZYHOrderingFood.back.util.Result;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zpy
 * @create: 2019-07-26 15:40
 **/
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("app")
//@Auth
public class AppController {
    @Autowired
    private ProductTypeService productTypeService;

    @Autowired
    private OrderDetailsService orderDetailsService;
    
    @Autowired
   	private ProductListService productListService;
    

    @Autowired
    private TUserService tUserService;

    /**
     * @description: 查询单品列表和单品详情
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    @PostMapping("product/select")
    public Result getProductListAndDetailsByShop (@RequestBody ProductType productType) {
        return productTypeService.getProductTypeAndListByShop(productType);
    }

    /**
     * @description: 加菜接口
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    @PostMapping("product/addagain")
    public Result insertOrderDetailsById (@RequestBody OrderTable orderTable ) {
        return orderDetailsService.addOrderDetailsAgain(orderTable);
    }

    /**
     * @description: 添加订单接口
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    @PostMapping("product/add")
    public Result insertOrderDetail (@RequestBody OrderTable orderTable) {
        return orderDetailsService.addOrderDetails(orderTable);
    }

    /**
     * @description: 购物车添加接口
     * @author: zpy
     * @date: 2019-08-05
     * @params:
     * @return:
     */
    @PostMapping("shopcart/add")
    public Result insertShopCart(@RequestBody ProductList productList) {
        return productListService.addShopCart(productList);
    }

    /**
     * @description: 模糊查询菜品列表
     * @author: scc
     * @date: 2019-07-29
     * @params:
     * @return:
     * @throws Exception 
     */
    @PostMapping("product/selectProductList")
    public Result<List<ProductList>>  selectProductList (@RequestBody ProductList productName ){
        return productListService.selectProductList(productName);
    }

    /**
     * @description: 查询商家信息接口
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    @PostMapping("shopinfo/select")
    public Result selectShopInfo (@RequestBody TUser tUser ){
        return tUserService.selectShopInfo(tUser);
    }

    /**
     * @description: 通过openid和商户id查询订单详情
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    @PostMapping("ordertable/select")
    public Result selectOrderInfo (@RequestBody OrderTable orderTable ){
        return tUserService.selectOrderInfo(orderTable);
    }

    /**
     * 通过单品id查询单品的数量和详情
     * @return
     */
    @PostMapping("shopcart/product/select")
    public Result selectProductInfo (@RequestBody OrderTable orderTable) {
        return tUserService.selectProductInfo(orderTable);
    }

    /**
     * 通过商户id和桌号查询单品的数量和详情
     * @return
     */
    @PostMapping("shopcart/usertable/select")
    public Result selectShopCartInfo (@RequestBody OrderTable orderTable) {
        return tUserService.selectShopCartInfo(orderTable);
    }

    /**
     * 通过单品id查询详情
     */
    @PostMapping("getproductInfo")
    public Result getproductInfo(@RequestBody ProductList productList) {
    	return productListService.getproductInfo(productList);
    }

}
