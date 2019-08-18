package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.Presentation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PresentationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Presentation record);

    int insertSelective(Presentation record);

    Presentation selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Presentation record);

    int updateByPrimaryKey(Presentation record);
}