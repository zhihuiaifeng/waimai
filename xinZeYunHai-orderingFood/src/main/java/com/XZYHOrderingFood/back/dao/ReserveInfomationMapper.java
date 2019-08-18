package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.ReserveInfomation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReserveInfomationMapper {
    int deleteByPrimaryKey(String id);

    int insert(ReserveInfomation record);

    int insertSelective(ReserveInfomation record);

    ReserveInfomation selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReserveInfomation record);

    int updateByPrimaryKey(ReserveInfomation record);

    /**
     * @description: 通过手机号查询失败
     * @author: zpy
     * @date: 2019-07-31
     */
    ReserveInfomation selectByPhoneNumber(ReserveInfomation record);
}