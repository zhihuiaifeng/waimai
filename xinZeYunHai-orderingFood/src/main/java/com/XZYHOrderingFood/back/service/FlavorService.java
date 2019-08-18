package com.XZYHOrderingFood.back.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.XZYHOrderingFood.back.dao.ProductListMapper;
import com.XZYHOrderingFood.back.dao.TFlavorMapper;
import com.XZYHOrderingFood.back.pojo.ProductList;
import com.XZYHOrderingFood.back.pojo.ProductType;
import com.XZYHOrderingFood.back.pojo.TFlavor;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;

@Service
@Transactional(noRollbackFor = Exception.class)
public class FlavorService {
 @Autowired
 private TFlavorMapper flavorMapper;
 
 @Autowired
 private ProductListMapper productListMapper;

public Result add(TFlavor flavor) {
	if(flavor.getProductId() == null) {
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "商品id不能为空", null);
	} 
	ProductList productList = productListMapper.selectByPrimaryKey(flavor.getProductId());
	flavor.setId(Sid.nextShort());
	flavor.setTuserId(productList.getTuserId());
	flavor.setCreateTime(new Date());
	//查询当前商品说是口味的最大sort值
	Integer maxSort =  flavorMapper.findMaxSortByProductId(flavor.getProductId());
	if(maxSort == null) {
		maxSort = 1;
	}else {
		maxSort+=1;
	}
	flavor.setSort(maxSort);
	int count = flavorMapper.insertSelective(flavor);
	if(count > 0) {
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
	}
	return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
}

public Result<List<TFlavor>> list(String productId) {
	 if(StringUtils.isBlank(productId)) {
		 return Result.resultData(PublicDictUtil.ERROR_VALUE, "商品id不能为空", null);
		 
	 }
	 List<TFlavor> list = flavorMapper.getListByProductId(productId);
	return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询口味成功", list);
}

public Result del(String id) {
	 if(StringUtils.isBlank(id)) {
		 return Result.resultData(PublicDictUtil.ERROR_VALUE, "口味id不能为空", null);
	 }
	 int count = flavorMapper.deleteByPrimaryKey(id);
	 if(count > 0) {
		 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
	 }
	return Result.resultData(PublicDictUtil.ERROR_VALUE,"操作失败", null);
}

public Result editSort(TFlavor flavor,HttpServletRequest request) {
	List<TFlavor> ptList = flavor.getFlavorList();
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
		 TFlavor tempFlavor = ptList.get(i);
		 tempFlavor.setSort(sorts.get(i));
		 int count = flavorMapper.updateByPrimaryKeySelective(tempFlavor);
		 if(count <= 0) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "排序失败", null);
		 }
	 }
	 
	return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "排序成功", null);
}
}
