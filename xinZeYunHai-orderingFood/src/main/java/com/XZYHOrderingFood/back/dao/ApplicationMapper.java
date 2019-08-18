package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);
}