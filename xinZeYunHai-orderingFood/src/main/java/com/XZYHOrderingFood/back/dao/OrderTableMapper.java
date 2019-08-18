package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.OrderTable;

import com.XZYHOrderingFood.back.pojo.TUser;

import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrderTableMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderTable record);

    int insertSelective(OrderTable record);

    OrderTable selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderTable record);

    int updateByPrimaryKey(OrderTable record);

    /**
     * @description: 查询总流水
     * @author: zpy
     * @date: 2019-07-22 11:38
     * @params:
     * @return:
     */
    BigDecimal countAllMoney(TUser user);

    List<OrderTable> getOrderTableByTime(@Param("param") HashMap map);

    List<OrderTable> getOrderTableByExample(@Param("param") Map<String, Object> map);

    /**
     * @description: 通过订单编号和商户id修改订单状态
     * @author: zpy
     * @date: 2019-07-26
     */
    int updateBytOrderCodeAndShopId (@Param("param") Map<String, Object> map);

    /**
     * @description: 通过订单id 和商户id 查询订单
     * @author: zpy
     * @date: 2019-07-30
     */
    OrderTable selectOrderTableIdByCode(@Param("param") Map map);

    /**
     * @description: 通过商户id 和openid 和订单id 查询订单
     * @author: zpy
     * @date: 2019-08-05
     */
    OrderTable selectByOpenIdAndUserId(OrderTable orderTable);
}