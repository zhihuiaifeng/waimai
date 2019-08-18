package com.XZYHOrderingFood.back.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.XZYHOrderingFood.back.pojo.ProductList;

@Mapper
public interface ProductListMapper {
    int deleteByPrimaryKey(String id);

    int insert(ProductList record);

    int insertSelective(ProductList record);

    ProductList selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ProductList record);

    int updateByPrimaryKey(ProductList record);

    /**
     * @description: 查询该商户的商品列表
     * @author: zpy
     * @date: 2019-07-26
     */
    List<ProductList> selectProductListByShopId(List list);

    /**
     * 查询商户下单品名称是否存在
     * @param productList
     * @return
     */
	ProductList findByParam(ProductList productList);

	/**
	 * 商户商品的分页列表
	 * @param productList
	 * @return
	 */
	List<ProductList> pageList(ProductList productList);

	List<ProductList> selectProductList(ProductList productName);

    /**
     * @description: 通过userid查询 单品列表
     * @author: zpy
     * @date: 2019-08-02
     */
    List<ProductList> selectProductListByUserId(String id);

    /**
     * @description: 批量修改单品数量
     * @author: zpy
     * @date: 2019-08-02
     */
    Integer updateProductListById(@Param(value = "productList") List<ProductList> productList);

    /**
     * 根据商品id 查询最大的sort值
     * @param id
     * @return
     */
	Integer findMaxSortByProductId(String productId);

	/**
	 * 通过单品id 查询信息
	 * @param productList
	 * @return
	 */
	ProductList getproductInfo(ProductList productList);


    List<ProductList> pr(List<String> foodIdList);

    /**
     * 通过单品主键集合查询多条数据
     * @param foodIdList
     * @return
     */
    List<ProductList> selectProductListByIdList(List<String> foodIdList);
}