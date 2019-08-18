package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.PresentationOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PresentationOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(PresentationOrder record);

    int insertSelective(PresentationOrder record);

    PresentationOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PresentationOrder record);

    int updateByPrimaryKey(PresentationOrder record);
}