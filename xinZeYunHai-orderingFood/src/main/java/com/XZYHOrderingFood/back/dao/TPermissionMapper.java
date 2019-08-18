package com.XZYHOrderingFood.back.dao;

import org.apache.ibatis.annotations.Mapper;

import com.XZYHOrderingFood.back.pojo.TPermission;
@Mapper
public interface TPermissionMapper {
    int deleteByPrimaryKey(String id);

    int insert(TPermission record);

    int insertSelective(TPermission record);

    TPermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TPermission record);

    int updateByPrimaryKey(TPermission record);
}