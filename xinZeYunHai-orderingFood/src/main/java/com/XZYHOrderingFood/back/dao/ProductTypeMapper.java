package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.ProductType;
import com.XZYHOrderingFood.back.pojo.TUser;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(ProductType record);

    int insertSelective(ProductType record);

    ProductType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ProductType record);

    int updateByPrimaryKey(ProductType record);

    /**
     * 查询最大排序号
     * @param user
     * @return
     */
	Integer findMaxSortByUser(TUser user);

	/**
	 * 查询类型是否存在
	 * @param productType
	 * @return
	 */
	ProductType findNameIsExist(ProductType productType);

	/**
	 * @description: 查询所有菜品列表
	 * @author: zpy
	 * @date: 2019-07-26
	 */
    List<ProductType> selectProductTypeByShopId(ProductType productType);
}