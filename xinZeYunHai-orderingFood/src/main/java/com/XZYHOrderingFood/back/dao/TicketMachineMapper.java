package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.pojo.TicketMachine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMachineMapper {
    int insert(TicketMachine record);

    int insertSelective(TicketMachine record);

    /**
     * @description: 通过id查询小票机状态
     * @author: zpy
     * @date: 2019-07-24
     * @params:
     * @return:
     */
    TicketMachine selectByUserId(String id);

    /**
     * @description: 更改小票机状态
     * @author: zpy
     * @date: 2019-08-01
     */
    Integer updateTicketStautsByUserId(TUser tUser);

    /**
     * 跟新小票机
     * @param ticketMachine
     * @return
     */
	int update(TicketMachine ticketMachine);
}