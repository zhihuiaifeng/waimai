package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.OrderDetails;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderDetailsMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderDetails record);

    int insertSelective(OrderDetails record);

    OrderDetails selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDetails record);

    int updateByPrimaryKey(OrderDetails record);

    /**
     * @description: 通过id集合查询 用于折线图
     * @author: zpy
     * @date: 2019-07-26
     */
    List<OrderDetails> getOrderDetailsByListId(List<String> map);

    /**
     * @description: 通过商户id查询订单详情表
     * @author: zpy
     * @date: 2019-07-26
     */
    List<OrderDetails> getOrderDetailsByShopId(TUser tUser);

    /**
     * @description: 加菜时批量插入数据
     * @author: zpy
     * @date: 2019-07-29
     */
    int insertOrderDetails (List<OrderDetails> orderDetails);

    /**
     * @description: 通过订单id 和userid 查询订单详情
     * @author: zpy
     * @date: 2019-08-02
     */
    List<OrderDetails> selectOrderDetailsByOrderCode(OrderTable orderTable);
}