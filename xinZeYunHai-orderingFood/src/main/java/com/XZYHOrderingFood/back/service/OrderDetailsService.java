package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.dao.OrderDetailsMapper;
import com.XZYHOrderingFood.back.dao.QrCodeMapper;
import com.XZYHOrderingFood.back.dao.TFlavorMapper;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.pojo.*;
import com.XZYHOrderingFood.back.redis.RedisUtils;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.auth0.jwt.impl.PublicClaims;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class OrderDetailsService {

    @Autowired
    private OrderDetailsMapper orderDetailsMapper;

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private TicketMachineService ticketMachineService;

    @Autowired
    private ProductListService productListService;

    @Autowired
    private QrCodeMapper qrCodeMapper;

    @Autowired
    private TFlavorMapper tFlavorMapper;

    @Autowired
    private RedisUtils redisUtils;
    /**
     * @description: 通过订单id查询订单详情表数据
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    public List<OrderDetails> getOrderDetailsByListId (List<String> list) throws CustomException {
        // 调用此接口目的是通过传入的idlist查询详情表数据
        List<OrderDetails> detailsList = null;
        try {
            detailsList = orderDetailsMapper.getOrderDetailsByListId(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(PublicDictUtil.ERROR_VALUE,"查询订单详情失败");
        }
        return detailsList;
    }


    /**
     * @description: 查询订单详情通过商户id
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    public Result getOrderDetailsByShopId (HttpServletRequest request) {
        // session中获取用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "从缓存中获取用户信息失败", null);
        }
        // 初始化结果集
        List<OrderDetails> orderDetails = null;
        try {
            orderDetails = orderDetailsMapper.getOrderDetailsByShopId(currentUser);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情失败", null);
        }
        return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情成功", orderDetails);
    }

    /**
     * @description: 下单时 在菜单详情表插入数据
     * @author: zpy
     * @date: 2019-07-29
     */
    public Result addOrderDetailsAgain (OrderTable orderTable) {
        // 获取ordertable
        OrderTable orderSelect = (OrderTable) addOrderDetailsById(orderTable).getData();
        // 小票机打印
        /**
         * 1. 首先通过tuserid查询小票机信息
         * 2. 获取商户名称和machine_code
         * 3. 调用打印接口
         */
        String tuserId = orderTable.getTuserId();
        String tableNumber = orderTable.getTableNumber();

        // 将ordertable的值赋全
        orderTable.setOrderTime(orderSelect.getOrderTime());
        orderTable.setMealsNumber(orderSelect.getMealsNumber());
        orderTable.setTableNumber(orderSelect.getTableNumber());
        orderTable.setRemark(orderSelect.getRemark());
        TicketMachine ticketMachine = null;
        TUser tUser = null;
        try {
            tUser = new TUser();
            tUser.setId(orderTable.getTuserId());
            ticketMachine = ticketMachineService.getTicketMachineInfoByUserId(tUser);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询小票机失败", null);
        }
        String content = null;
        // 获取小票msg
        if (ticketMachine != null) {
            content = ticketMachineService.printContentCommon(ticketMachine, orderTable, 1);
        }
        // 清空redis缓存
        redisUtils.remove(tuserId+tableNumber);
        if (redisUtils.exists(tuserId+tableNumber)) {
            log.error("redis缓存清除失败");
        }
        // 调取打印接口
        if (content != null) {
            try {
                return ticketMachineService.commentPrint(ticketMachine.getMachineCode(), content, orderTable.getOrderCode());
            } catch (Exception e) {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "小票机打印失败", null);
            }
        } else {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "小票机打印失败", null);
        }
    }

    /**
     * @description: 向订单详情表插入数据
     * @author: zpy
     * @date: 2019-08-02
     */
    public Result addOrderDetailsById (OrderTable orderTable){
        /**
         * 1. 首先前台传入订单编号
         * 2. 通过编号查询订单id
         * 3. 通过订单id给订单详情表添加数据
         */
        if (StringUtils.isBlank(orderTable.getTuserId())) {
            log.info("商户id为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id为空", null);
        }
        if (StringUtils.isBlank(orderTable.getOrderCode())) {
            log.info("订单编号不存在");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单编号不存在", null);
        }
        if (orderTable.getOrderDetails() == null) {
            log.info("加餐列表为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "加餐列表为空", null);
        }
        BigDecimal totalMoneny = new BigDecimal("0");
        // 计算总金额
        for (OrderDetails details:orderTable.getOrderDetails()
        ) {
            totalMoneny = totalMoneny.add(details.getPrice().multiply(BigDecimal.valueOf(details.getFoodNumber())));
        }
        if (totalMoneny.compareTo(orderTable.getTotalMoney()) != 0) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "总订单金额有误");
        }
        // session中获取用户信息
//        TUser currentUser = GetCurrentUser.getCurrentUser(request);
//        if (currentUser == null) {
//            return Result.resultData(PublicDictUtil.ERROR_VALUE, "从缓存中获取用户信息失败", null);
//        }
        // 订单主键id
        OrderTable orderSelect = null;
        try {
            orderSelect = orderTableService.getOrderTableCode(orderTable.getOrderCode(), orderTable.getTuserId());
        } catch (Exception e) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "订单表查询失败");
        }
        if (orderSelect == null) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "订单表查询失败");
        }
        // 初始化数据库查需对象
        List<OrderDetails> orderDetails = new ArrayList<>();
        // 遍历数据
        if (orderSelect != null) {
            for (OrderDetails details : orderTable.getOrderDetails()
            ) {
                details.setId(Sid.nextShort());
                details.setOrderId(orderSelect.getOrderCode());
                details.setTuserId(orderTable.getTuserId());
                details.setCreateTime(new Date());
                details.setCreateId(orderTable.getTuserId());
                details.setOrderTime(new Date());
                details.setFoodId(details.getFoodId());
                // 通过单品id和单品名称查询
                TFlavor tFlavor = tFlavorMapper.selectByProductIdAndName(details);
                if (tFlavor != null) {
                    details.setTflavorId(tFlavor.getId());
                }
                orderDetails.add(details);
                if (StringUtils.isBlank(details.getPrice().toString())) {
                    log.info("单品价格不存在");
                    throw new CustomException(PublicDictUtil.ERROR_VALUE, "单品价格不存在");
                }
                if (StringUtils.isBlank(details.getFoodName())) {
                    log.info("单品名称不存在");
                    throw new CustomException(PublicDictUtil.ERROR_VALUE, "单品名称不存在");
                }
                if (StringUtils.isBlank(details.getFoodNumber().toString())) {
                    log.info("单品数量不存在");
                    throw new CustomException(PublicDictUtil.ERROR_VALUE, "单品数量不存在");
                }
            }
        }
        // 将list集合放入订单详情表
        Integer count = null;
        try {
            count = orderDetailsMapper.insertOrderDetails(orderDetails);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "批量插入订单详情表失败");
        }
        // 返回
        if (count < 1) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "加餐失败");
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "成功", orderSelect);
    }


    /**
     * @description: 手机app下单接口
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    public Result addOrderDetails (OrderTable orderTable) {

        // 验证字段不为空
        if (StringUtils.isBlank(orderTable.getTuserId())) {
            log.info("商户id为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id为空", null);
        }
        if (orderTable.getOrderDetails() == null) {
            log.info("加餐列表为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "加餐列表为空", null);
        }
        if (orderTable.getTotalMoney() == null) {
            log.info("总订单金额为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "总订单金额为空", null);
        }
        if (orderTable.getMealsNumber() == null) {
            log.info("用餐人数为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "用餐人数为空", null);
        }
        if (orderTable.getTableNumber() == null) {
            log.info("桌号为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号为空", null);
        }
        if (orderTable.getOpenId() == null) {
            log.info("微信openid为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "微信openid为空", null);
        }

        String tuserId = orderTable.getTuserId();
        String tableNumber = orderTable.getTableNumber();

        // 查询二维码状态 如果为禁用则不可下单
        List<QrCode> qrCode = qrCodeMapper.selectByShopId(orderTable.getTuserId());
        if (qrCode.size() < 1) {return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家没有二维码", null);}
        boolean flag = false;
        for (QrCode qrcode:qrCode
             ) {
            if (qrcode.getTableNumber().toString().equals(orderTable.getTableNumber())) {
                flag = true;
                if (qrcode.getTableStatus() == 1) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "目前二维码处于锁定状态", null);
                }
            }
        }
        if (!flag) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商家没有对应桌号", null);
        }
        BigDecimal totalMoneny = new BigDecimal("0");
        // 计算总金额
        for (OrderDetails details:orderTable.getOrderDetails()
             ) {
            totalMoneny = totalMoneny.add(details.getPrice());
        }
        if (totalMoneny.compareTo(orderTable.getTotalMoney()) != 0) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "总订单金额有误", null);
        }
        /**
         * 将订单信息插入到数据库中
         * 1. 首先获取到传入的数据 userid，openid
         * 2. 将订单插入数据库中
         * 3. 将订单详情插入详情表中
         */
        // 初始化返回值
        List<ProductList> productLists = null;
        // 首先先查询该商户单品信息，判断该单品是否存在那么多商品，如果不存在则报错，如果存在不是999则减值
        try {
            productLists = productListService.selectProductListByUserId(orderTable);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询单品表失败", null);
        }
        if (productLists == null) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商户单品不存在", null);
        }
        Integer count = null;
        // 将返回值判断 修改单品剩余数量
        try {
            count = productListService.updateProductListRestNum(productLists, orderTable);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改单品剩余数失败", null);
        }
        if (count < 1) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改单品剩余数失败", null);
        }
        String orderCode = null;
        // 在订单表插入数据
        try {
            orderCode = orderTableService.insertOrderTable(orderTable);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "插入订单表失败", null);
        }
        // 在订单详情表插入数据
        orderTable.setOrderCode(orderCode);
        try {
            Result result = addOrderDetailsById(orderTable);
            if (PublicDictUtil.ERROR_VALUE.equals(result.getCode())) {
                throw new CustomException(PublicDictUtil.ERROR_VALUE, "菜单详情插入失败");
            }
        } catch (Exception e) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "菜单详情插入失败");
        }
        // 清空redis缓存
        redisUtils.remove(tuserId+tableNumber);
        if (redisUtils.exists(tuserId+tableNumber)) {
            log.error("redis缓存清除失败");
        }
        // 小票机打印
        /**
         * 1. 首先通过tuserid查询小票机信息
         * 2. 获取商户名称和machine_code
         * 3. 调用打印接口
         */
        TicketMachine ticketMachine = null;
        TUser tUser = null;
        try {
            tUser = new TUser();
            tUser.setId(orderTable.getTuserId());
            ticketMachine = ticketMachineService.getTicketMachineInfoByUserId(tUser);
        } catch (Exception e) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "查询小票机失败");
        }
        String content = null;
        // 获取小票msg
        if (ticketMachine != null) {
            content = ticketMachineService.printContentCommon(ticketMachine, orderTable, 0);
        }
        // 调取打印接口
        if (content != null) {
            try {
                return ticketMachineService.commentPrint(ticketMachine.getMachineCode(), content, orderTable.getOrderCode());
//                return null;
            } catch (Exception e) {
                throw new CustomException(PublicDictUtil.ERROR_VALUE, "小票机打印失败");
            }
        } else {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "小票机打印失败");
        }
    }

    //通过订单ID查询订单详情
    public List<OrderDetails> selectOrderDetailsByOrderCode(TUser tUser, OrderTable orderTable) {
        // 设置商户id
        orderTable.setTuserId(tUser.getId());
        return orderDetailsMapper.selectOrderDetailsByOrderCode(orderTable);
    }
}
