package com.XZYHOrderingFood.back.dao;

import org.apache.ibatis.annotations.Mapper;

import com.XZYHOrderingFood.back.pojo.ApplyReset;

import java.util.List;

@Mapper
public interface ApplyResetMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApplyReset record);

    int insertSelective(ApplyReset record);

    ApplyReset selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApplyReset record);

    int updateByPrimaryKey(ApplyReset record);

    List<ApplyReset> selectUserApplyReset(ApplyReset record);

    int updateByUserIdSelective(ApplyReset record);
}