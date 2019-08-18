package com.XZYHOrderingFood.back.dao;

import java.util.List;

import com.XZYHOrderingFood.back.pojo.OrderDetails;
import org.apache.ibatis.annotations.Mapper;

import com.XZYHOrderingFood.back.pojo.TFlavor;
@Mapper
public interface TFlavorMapper {
    int deleteByPrimaryKey(String id);

    int insert(TFlavor record);

    int insertSelective(TFlavor record);

    TFlavor selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TFlavor record);

    int updateByPrimaryKey(TFlavor record);

    /**
     * 通过商品id 查询口味
     * @param productId
     * @return
     */
	List<TFlavor> getListByProductId(String productId);

	/**
	 * 通过商品id删除口味
	 * @param id
	 */
	int delByProductId(String productId);

	/**
	 * @description: 通过idlist查询对应的口味
	 * @author: zpy
	 * @date: 2019-07-29
	 */
	List<TFlavor> selectTFavorListByIdList(List<String> list);

	/**
	 * @description: 通过口味id查询对应的口味
	 * @author: zpy
	 * @date: 2019-08-03
	 */
    List<TFlavor> selectTFavorListById(List<String> list);

    /**
     * 通过商品id查询最大的sort值
     * @param id
     * @return
     */
	Integer findMaxSortByProductId(String productId);

	/**
	 * 通过单品id和口味名称查询口味id
	 * @param details
	 * @return
	 */
    TFlavor selectByProductIdAndName(OrderDetails details);

	/**
	 * 通过单品id查询口味
	 * @param id
	 * @return
	 */
	List<TFlavor> selectByProductId(String id);

	/**
	 * 通过详情表的口味主键查询口味
	 * @return
	 */
	List<TFlavor> selectTFavorList (List<String> list);
}