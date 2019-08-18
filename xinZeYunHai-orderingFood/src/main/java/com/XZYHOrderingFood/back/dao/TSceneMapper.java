package com.XZYHOrderingFood.back.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.XZYHOrderingFood.back.pojo.TScene;
@Mapper
public interface TSceneMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TScene record);

    int insertSelective(TScene record);

    TScene selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TScene record);

    int updateByPrimaryKey(TScene record);

    /**
     * 查询列表
     * @return
     */
	List<TScene> list();
}