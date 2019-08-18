package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.dao.ProductTypeMapper;
import com.XZYHOrderingFood.back.pojo.ProductList;
import com.XZYHOrderingFood.back.pojo.ProductType;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;


import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ProductTypeService {

	public static final Integer IS_EDIT = 0; //可编辑吧

	public static final Integer NO_EDIT = 1; //不可编辑

    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Autowired
	private ProductListService productListService;

	public Result add(ProductType productType,HttpServletRequest request) {
		 if(productType == null) {return Result.resultData(PublicDictUtil.ERROR_VALUE, "参数不能为空", null);}


		 productType.setId(Sid.nextShort());
		 productType.setCreateTime(new Date());
		 productType.setIsEdit(IS_EDIT);
		 TUser user = GetCurrentUser.getCurrentUser(request);
		 if(user == null) {
			 return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请重新登录后再添加数据", null);
		 }
		 productType.setTuserId(user.getId());
		 productType.setCreateId(user.getId());

		 //判断类名是否存在
		 ProductType tempPt = productTypeMapper.findNameIsExist(productType);
		 if(tempPt != null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "类名:"+productType.getMeunName()+"已经存在", null);
		 }
		 Integer maxSort = productTypeMapper.findMaxSortByUser(user);
		 if(maxSort == null) {
			 maxSort = 3;
		 }else {
			 maxSort += 1;
		 }
		 productType.setSort(maxSort);
		 int cont = productTypeMapper.insertSelective(productType);
		 if(cont > 0) {
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		 }
		 return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	public Result del(String id) {
		 if(id == null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "id不能为空", null);
		 }
		 //ProductType productType = productTypeMapper.selectByPrimaryKey(id);
		 ProductType productType = new ProductType();
		 productType.setId(id);
		 productType.setIsDelete(IS_EDIT);
		 int count = productTypeMapper.updateByPrimaryKeySelective(productType);
		 if(count > 0) {
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		 }
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	public Result editSort(ProductType productType) {
		List<ProductType> ptList =  productType.getPtList();
		if(ptList.size() < 2 || ptList.isEmpty()) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "至少选择两条数据", null);
		 }
		 List<Integer> sorts = new ArrayList<Integer>();
		 ptList.forEach(b -> {
			 sorts.add(b.getSort());
		 });
		 Collections.sort(sorts); //从小到大排序
		 Set<Integer> sets = new HashSet<Integer>();
		 for(Integer i:sorts) {
			 boolean b = sets.add(i);
			 if(!b) {
				 return Result.resultData(PublicDictUtil.ERROR_VALUE, "序号"+i+"重复了", null);
			 }
		 }

		 for(int i=0;i<ptList.size();i++ ) {
			 ProductType tempPt = ptList.get(i);
			 tempPt.setSort(sorts.get(i));
			 int count = productTypeMapper.updateByPrimaryKeySelective(tempPt);
			 if(count <= 0) {
				 return Result.resultData(PublicDictUtil.ERROR_VALUE, "排序失败", null);
			 }
		 }

		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);

	}


	/**
	 * @description: 查询单品列表和单品详情
	 * @author: zpy
	 * @date: 2019-07-26
	 * @params:
	 * @return:
	 */
	public Result getProductTypeAndListByShop (ProductType productTyp) {
   		if (StringUtils.isBlank(productTyp.getTuserId())) {
			log.info("商户id为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id为空", null);
		}
		/**
		 * 1.首先通过用户id查询单品列表
		 * 2.获取该商户全部单品信息
		 * 3.将同列表的信息分类，将列表放入list
		 */
		List<ProductType> productTypeList = new ArrayList<>();
		List<ProductType> productTypeReturn = null;
		// 获取商户餐品列表
		try {
			productTypeReturn = productTypeMapper.selectProductTypeByShopId(productTyp);
			Collections.sort(productTypeReturn, (o1, o2) -> o1.getSort() - o1.getSort());
		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "获取商户餐品列表失败", null);
		}
		ProductType hotsell = new ProductType();
		hotsell.setMeunName("热卖推荐");
		ProductType newproduct = new ProductType();
		newproduct.setMeunName("新品上市");
		productTypeList.add(hotsell);
		productTypeList.add(newproduct);
		for (ProductType productype:productTypeMapper.selectProductTypeByShopId(productTyp)
		) {
			productTypeList.add(productype);
		}
		/**
		 * 1. 首先将列表id存入list中
		 * 2. 将idlist作为查询条件查询
		 */
		List<String> idList = new ArrayList<>();
		for (ProductType productType:productTypeList
			 ) {
			idList.add(productType.getId());
		}
		// 获取商户餐品单品
		List<ProductList> productLists = null;
		try {
			productLists = productListService.selectProductListByShopId(idList);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "获取商户餐品单品失败", null);
		}
		Integer subScript = 0;
		// 将取到的数据放入type中
		for (ProductType productType:productTypeList
			 ) {
			productType.setProductLists(new ArrayList<>());
			productType.setTypeInitNumber(0);
			for (ProductList productList:productLists
				 ) {
				productList.setInitNumber(0);
				// 匹配商户菜品id和菜单id相同
				if (productType.getId()!=null) {
					if (productType.getId().equals(productList.getMenuId())) {
						productList.setSubScript(subScript);
						productType.getProductLists().add(productList);
					}
				}
				if (productList.getIsHotsell() == 0 && "热卖推荐".equals(productType.getMeunName())) {
					productType.getProductLists().add(productList);
				}
				if (productList.getIsNewproduct() == 0 && "新品上市".equals(productType.getMeunName())) {
					productType.getProductLists().add(productList);
				}
			}
			subScript++;
		}
//		// 返回一个map试试
//		HashMap hashMap = Maps.newHashMap();
//		hashMap.put("menu", productTypeList);
//		hashMap.put("productList", productLists);
   		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询商户餐品列表成功", productTypeList);
	}

	public Result selectList(ProductList product) {
		if (StringUtils.isBlank(product.getProductName())) {
			log.info("商品信息为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商品信息为空", null);
		}
		return null;
	}

	/**
	 * @description: 通过商户id查询该商家全部商品列表
	 * @author: zpy
	 * @date: 2019-07-30
	 * @params:
	 * @return:
	 */
	public Result selectAllTypeByUserId(HttpServletRequest request) {
		// 从session缓存中获取用户
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		List<ProductType> productTypes = null;
		try {
			ProductType productType = new ProductType();
			productType.setTuserId(currentUser.getId());
			productTypes = productTypeMapper.selectProductTypeByShopId(productType);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "获取商户所有商品列表", null);
		}
		if (productTypes != null) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "获取商户列表成功", productTypes);
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户列表为空", null);
		}
	}
}
