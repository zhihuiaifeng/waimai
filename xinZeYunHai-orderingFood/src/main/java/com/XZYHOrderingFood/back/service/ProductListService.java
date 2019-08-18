package com.XZYHOrderingFood.back.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.redis.RedisUtils;
import com.XZYHOrderingFood.back.util.*;
import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.XZYHOrderingFood.back.dao.ProductListMapper;
import com.XZYHOrderingFood.back.dao.TFlavorMapper;
import com.XZYHOrderingFood.back.pojo.OrderDetails;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.ProductList;
import com.XZYHOrderingFood.back.pojo.TFlavor;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ProductListService {

	public static final Integer IS_ACTIVE = 1; //上架

	public static final Integer NO_ACTIVE = 0;//下架

    @Autowired
    private ProductListMapper productListMapper;

    @Autowired
    private TFlavorMapper flavorMapper;

    @Autowired
    private Config config;

    @Autowired
	private RedisUtils redisUtils;

	/**
	 * 上传文件
	 * @throws Exception
	 */
	public String uploadFile(MultipartFile file)  {
		String tag = "";
		String newFileName = "";
		String path = "";
		try {
			String contentType = Util.getFileRealType(file.getInputStream());
			//图片文件
			path = config.getUploadPath() + config.getProductImg();
			if (!contentType.contains("image")) {
				tag = "noType";
				return tag;
			}

			String filename = file.getOriginalFilename();// 得到原来的文件名称
			newFileName = System.nanoTime() * 1000 + filename.substring(filename.lastIndexOf("."));
			File file2 = new File(path); // 创建本地文件流对象
			if (!file2.exists()) {
				file2.mkdirs();
			}
			File targetFile = new File(path, newFileName);

			file.transferTo(targetFile);

			//缩略图
			ThumbnailsUtil tu = new ThumbnailsUtil();
			ThumbnailsUtil.shrinkToSize(Integer.parseInt(PublicDictUtil.WIDTH),Integer.parseInt(PublicDictUtil.HEIGHT), targetFile.getAbsolutePath(), path,newFileName);
			//new OSSUtils().uploadFile(path + newFileName, targetFile.getPath());
			//OSSObjectSample.uploadFile(conf.getCourseImg() + newFileName, targetFile.getPath());
			//targetFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			tag = "no";
			return tag;
		}
		tag = newFileName;
		return tag;
	}


//删除图片
public boolean delFile(String path) {

	File file = new File(path);
	if (!file.exists() || !file.isFile()) {
		return false;
	}
	return file.delete();
}

	public Result add(ProductList productList,MultipartFile pImg,HttpServletRequest request) {
		if (StringUtils.isBlank(productList.getProductName())) {
			log.info("单品名称为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品名称为空", null);
		}
		if (StringUtils.isBlank(productList.getMenuId())) {
			log.info("单品类型为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品类型为空", null);
		}
		if (StringUtils.isBlank(productList.getIsHotsell().toString())) {
			log.info("是否热卖为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "是否热卖为空", null);
		}
		if (StringUtils.isBlank(productList.getIsNewproduct().toString())) {
			log.info("是否新品上市");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "是否新品上市", null);
		}
		if (StringUtils.isBlank(productList.getPrice().toString())) {
			log.info("单价为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单价为空", null);
		}
		if (StringUtils.isBlank(productList.getIntroduce())) {
			log.info("单品介绍为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品介绍为空", null);
		}
		if (StringUtils.isBlank(productList.getRestNumber().toString())) {
			log.info("剩余餐品数为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "剩余餐品数为空", null);
		}
		 if(pImg != null) {
			 //上传图片
			 String tempData = uploadFile(pImg);
			 if("no".equals(tempData) || "noType".equals(tempData)) {
				 return Result.resultData(PublicDictUtil.ERROR_VALUE, "图片上传失败,请检查类型和网络", null);
			 }else {
				 productList.setProductImg(tempData);
			 }
		 }
		 //获取当前用户
		 TUser user = GetCurrentUser.getCurrentUser(request);
		 if (user == null) {return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "token失效", null);}
		 //判断单品名称是否存在
		 productList.setCreateId(user.getId());
		 productList.setTuserId(user.getId());
		 ProductList p = productListMapper.findByParam(productList);
		 if(p != null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "当前商品名称:"+productList.getProductName()+"已经存在", null);
		 }
		 productList.setId(Sid.nextShort());
		 productList.setCreateTime(new Date());

		 productList.setIsActive(IS_ACTIVE);
		 int count = productListMapper.insertSelective(productList);
		 if(count > 0) {
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		 }
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}


	public Result edit(ProductList productList, MultipartFile pImg, HttpServletRequest request) {
		 if(productList.getId() == null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "id不能为空", null);
		 }
		 ProductList temp = productListMapper.selectByPrimaryKey(productList.getId());
//		 if(pImg != null) {
//
//			 //上传图片
//			 String tempData = uploadFile(pImg);
//			 if("no".equals(tempData) || "noType".equals(tempData)) {
//				 return Result.resultData(PublicDictUtil.ERROR_VALUE, "图片上传失败,请检查类型和网络", null);
//			 }else {
//				 productList.setProductImg(tempData);
//			 }
//		 }
		 //获取当前用户
		 TUser user = GetCurrentUser.getCurrentUser(request);
		 if(user == null) {
			 return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		 }
		 productList.setModifyTime(new Date());
		 productList.setCreateId(user.getId());
		 int count = productListMapper.updateByPrimaryKeySelective(productList);
//		 if(count > 0) {
//			 //删除原来图片
//			 String path = config.getUploadPath() + config.getProductImg() + temp.getProductImg();
//			 delFile(path);
//			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
//		 }
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}


	public Result del(String id) {
		 if(id == null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "id不能为空", null);
		 }
		 ProductList productList = productListMapper.selectByPrimaryKey(id);
		 int count = productListMapper.deleteByPrimaryKey(id);
		 if(count > 0) {
			//删除相关的口味
			  count = flavorMapper.delByProductId(id);
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "删除成功", null);
		 }

		return Result.resultData(PublicDictUtil.ERROR_VALUE, "删除失败", null);
	}


	public Result editActive(String id) {
		 if(StringUtils.isBlank(id)) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "商品id不能为空", null);
		 }
		 ProductList tempData = productListMapper.selectByPrimaryKey(id);
		 if(tempData == null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "当前商品不存在", null);
		 }
		 ProductList productList = new ProductList();
		 if(tempData.getIsActive() == IS_ACTIVE) {
			 productList.setIsActive(NO_ACTIVE);
		 }else if(tempData.getIsActive() == NO_ACTIVE) {
			 productList.setIsActive(IS_ACTIVE);
		 }
		 productList.setTuserId(tempData.getTuserId());
		 productList.setId(id);
		 int count = productListMapper.updateByPrimaryKeySelective(productList);
		 if(count > 0) {
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		 }
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}


	public Result<BasePage<ProductList>> pageList(ProductList productList,HttpServletRequest request) {
		if (productList.getPageNo() == null || productList.getPageSize() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "pageNo,pageSize都不能为空", null);
		}
		//获取当前用户
		TUser user = GetCurrentUser.getCurrentUser(request);
		if (user == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		}
		PageHelper.startPage(productList.getPageNo(), productList.getPageSize());
		productList.setTuserId(user.getId());
		List<ProductList> list = productListMapper.pageList(productList);
		String path = config.getServerUrl() + config.getProductImg().replaceAll("\\\\", "/");
		list.forEach(pl -> {
			pl.setProductImg(path + pl.getProductImg());
		});
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, new BasePage<ProductList>(list));
	}

    /**
     * @description: 查询该商户商品列表
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    public List<ProductList> selectProductListByShopId (List list) throws Exception {
        // 通过用户查询商户列表
        List<ProductList> productLists = null;
        try {
            productLists = productListMapper.selectProductListByShopId(list);
        } catch (Exception e) {
            throw new Exception("查询菜品详情失败");
        }
        // 初始化单品id集合
		List<String> strings = new ArrayList<>();
        // 遍历单品id v
		for (ProductList productList:productLists
			 ) {
			strings.add(productList.getId());
			// 查询成功后将地址拼接好
			String path = config.getUploadPath() + config.getProductImg() + productList.getProductImg();
			productList.setProductImg(path);
		}
        // 将所有id带着查询口味表
		List<TFlavor> flavors = null;
		try {
			flavors = flavorMapper.selectTFavorListByIdList(strings);
		} catch (Exception e) {
			throw new Exception("查询口味失败");
		}
		// 将所有口味放入对应的list中
		/**
		 * 1. 首先遍历单品表 取用单品id
		 * 2. 遍历口味表
		 * 3. 将属于哪个口味的信息放入单品表中
		 */
		if (flavors != null) {
			for (ProductList productList:productLists
				 ) {
				productList.setTFlavors(new ArrayList<>());
				for (TFlavor flavor:flavors
					 ) {
					if (productList.getId().equals(flavor.getProductId())) {
						flavor.setFlovrInitNumber(0);
						productList.getTFlavors().add(flavor);
					}
				}
			}
		}
        return productLists;
    }


    /**
     * @description: 模糊查询菜品列表
     * @author: scc
     * @date: 2019-07-29
     * @params:
     * @return:
     * @throws Exception 
     */
	public Result<List<ProductList>> selectProductList(ProductList productName){
			
			List<ProductList>produsList=null;
			try {
				produsList=productListMapper.selectProductList(productName);
			} catch (Exception e) {
				e.printStackTrace();
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "商品查询失败", null);
			}
			
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null,produsList);
	}

	/**
	 * @description: 查询商品列表通过 传入id
	 * @author: zpy
	 * @date: 2019-08-01
	 * @params:
	 * @return:
	 */
	public Result selectProductListById(ProductList productList) {
		if (StringUtils.isBlank(productList.getId())) {
			log.info("主键不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键不能为空", null);
		}
		ProductList productList1 = null;
		// 通过传入id查询数据
		try {
			productList1 = productListMapper.selectByPrimaryKey(productList.getId());
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键不能为空", null);
		}
		if (productList1 != null) {
			// 设置图片
			productList1.setProductImg(config.getServerUrl() + config.getProductImg()+productList1.getProductImg());
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询成功", productList1);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询失败", null);
	}

	/**
	 * @description: 编辑时添加图片
	 * @author: zpy
	 * @date: 2019-08-01
	 */
	public Result uploadImgByEdit(ProductList productList, MultipartFile pImg) {
		if(pImg == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "图片不能为空", null);
		}
		if(productList.getId() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "id不能为空", null);
		}
		ProductList temp = productListMapper.selectByPrimaryKey(productList.getId());
		if(pImg != null) {
			//上传图片
			String tempData = uploadFile(pImg);
			if("no".equals(tempData) || "noType".equals(tempData)) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "图片上传失败,请检查类型和网络", null);
			}else {
				productList.setProductImg(tempData);
			}
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
		}
		//删除原来图片
		productList.setModifyTime(new Date());
		int count = productListMapper.updateByPrimaryKeySelective(productList);
		String path = config.getUploadPath() + config.getProductImg() + temp.getProductImg();
		if (delFile(path)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "文件不存在或删除文件失败", null);
		}
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改操作成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	/**
	 * @description: 通过userid查询
	 * @author: zpy
	 * @date: 2019-08-02
	 */
	public List<ProductList> selectProductListByUserId(OrderTable orderTable) {
		return productListMapper.selectProductListByUserId(orderTable.getTuserId());
	}

	/**
	 * @description: 修改单品的剩余数量
	 * @author: zpy
	 * @date: 2019-08-02
	 * @params:
	 * @return:
	 */
	public Integer updateProductListRestNum(List<ProductList> productList, OrderTable orderTable) throws Exception {
		List<ProductList> lists = new ArrayList<>();
		// 遍历单品类型
		for (ProductList prolist:productList
			 ) {
			for (OrderDetails details: orderTable.getOrderDetails()
				 ) {
				// 如果单品名和食品名相同
				if (prolist.getProductName().equals(details.getFoodName())) {
					if (prolist.getSellNumber() != 999) {
						if (prolist.getRestNumber()!=null && details.getFoodNumber()!=null ) {
							// 修改单品数量
							prolist.setRestNumber(prolist.getRestNumber()-details.getFoodNumber());
							if (prolist.getRestNumber() < 0) {
								throw new CustomException(PublicDictUtil.ERROR_VALUE, "餐品剩余数量不足");
							} else {
								lists.add(prolist);
							}
						} else {
							throw new CustomException(PublicDictUtil.ERROR_VALUE, "餐品数或剩余数为空");
						}
					}
				}
			}
			prolist.setModifyTime(new Date());
		}
		// 批量修改数量
		Integer count = null;
		try {
			count = productListMapper.updateProductListById(lists);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
		return count;
	}

	/**
	 * @description: 通过传入id 查询商家全部单品
	 * @author: zpy
	 * @date: 2019-08-03
	 */
	public Result getProductListByUserId(HttpServletRequest request) {
		// session中获取用户信息
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "从缓存中获取用户信息失败", null);
		}
		List<ProductList> productLists = productListMapper.selectProductListByUserId(currentUser.getId());
		if (productLists == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询单品信息失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询单品信息成功", productLists);
	}

	/**
	 * @description: 购物车点击加菜
	 * @author: zpy
	 * @date: 2019-08-05
	 */
	public Result addShopCart(ProductList productList) {
		if (StringUtils.isBlank(productList.getTuserId())) {
			log.info("商户id为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id为空", null);
		}
		if (StringUtils.isBlank(productList.getTableNumber())) {
			log.info("桌号为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号为空", null);
		}
		if (StringUtils.isBlank(productList.getOpenId())) {
			log.info("openid为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "openid为空", null);
		}
		if (StringUtils.isBlank(productList.getFoodNumber().toString())) {
			log.info("加减标志为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "加减标志为空", null);
		}
		if (StringUtils.isBlank(productList.getId())) {
			log.info("单品id为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品id为空", null);
		}
		if (StringUtils.isBlank(productList.getProductName())) {
			log.info("单品id为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品id为空", null);
		}
		// 商户id
		String tuserid = productList.getTuserId();
		// 桌号
		String tablenumber = productList.getTableNumber();
		// 微信openid
		String openid = productList.getOpenId();
		// 加餐减餐
		Integer foodnumber = productList.getFoodNumber();
		// 单品id
		String productid = productList.getId();
		// 单品 name
		String productname = productList.getProductName();
		// 口味name
		String flavorName = productList.getFlavorName();
		// 加餐标志
		boolean flag = false;
		// 口味是否为1个
		boolean flovrFlag = false;

		// websocket传值
		/**
		 * 这个方法是对传入单品进行购物车的存放，对所有相同的商户id和桌号相同的轮播
		 */
		// 首先将redis中存入数据
//		Map<String, Map> map = Maps.newHashMap();
//		Map<String, Integer> mapin = Maps.newHashMap();
		Map<String, Integer> integerStringMap = Maps.newHashMap();
		Map<String, List<Map<String, Integer>>> getMap;
		List<Map<String, Integer>> inlist = Lists.newArrayList();
		// 返回的集合
		Map<String, Map<String, Integer>> putMap = Maps.newHashMap();
		Map<String, Integer> putMapin = Maps.newHashMap();
		/**
		 * 1. 首先从redis通过桌号和商户id取数据，如果为空就第一次村
		 * 2. 如果不为空，则将取出的list集合进行遍历，如果map中含有该单品，则将数量加1，然后进行sockt
		 */
//		HashMap<String, Map<String, Integer>> mapList = null;
		// 从redis中取数据
 		getMap = (Map<String, List<Map<String, Integer>>>)redisUtils.get(tuserid+tablenumber);
		// 不为空，说明商家和该桌子已经开始点餐
		if (getMap != null) {
			loop2:
			// 遍历餐品          单品id       单品口味    单品数量
			for (HashMap.Entry<String, List<Map<String, Integer>>> mapo: getMap.entrySet()
				 ) {
				// 匹配单品成功
				if ((productid+"|"+productname).equals(mapo.getKey())) {
					if (foodnumber == 1) {
						if (mapo.getValue() != null) {
							for (Map<String, Integer> value : mapo.getValue()
							) {
								for (Map.Entry<String, Integer> mapp : value.entrySet()) {
									if (flavorName.equals(mapp.getKey())) {
										mapp.setValue(mapp.getValue() + 1);
										flag = true;
										// 口味已经存在
										flovrFlag = true;
									}
								}
							}
						}
					} else if (foodnumber == -1) {
						if (mapo.getValue() != null) {
							loop:
							for (Map<String, Integer> value : mapo.getValue()
							) {
								Set<Map.Entry<String, Integer>> entries = value.entrySet();
								Iterator<Map.Entry<String, Integer>> iteratorMap = entries.iterator();
								while (iteratorMap.hasNext()) {
									Map.Entry<String, Integer> mapp = iteratorMap.next();
									if (flavorName.equals(mapp.getKey())) {
										if (mapp.getValue() > 1) {
											mapp.setValue(mapp.getValue() - 1);
											flag = true;
											flovrFlag = true;
										} else if (mapp.getValue() == 1) {
											iteratorMap.remove();
											flovrFlag = true;
											flag = true;
											break loop;
										} else {
											return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商品未有数量，减餐失败", null);
										}
									}
								}
//								for (Map.Entry<String, Integer> mapp : value.entrySet()) {
//									if (flavorName.equals(mapp.getKey())) {
//										if (mapp.getValue() > 1) {
//											mapp.setValue(mapp.getValue() - 1);
//											flag = true;
//											flovrFlag = true;
//										} else if (mapp.getValue() == 1) {
//											value.clear();
//											flovrFlag = true;
//											flag = true;
//											break loop;
//										} else {
//											return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商品未有数量，减餐失败", null);
//										}
//									}
//								}
							}
						}
					}
					if (!flovrFlag) {
						if (flavorName != null) {
							integerStringMap.put(flavorName, 1);
						} else {
							integerStringMap.put("原味", 1);
						}
						mapo.getValue().add(integerStringMap);
						flag = true;
					}
					boolean isExistflag = false;
					Set<Map.Entry<String, List<Map<String, Integer>>>> entries = getMap.entrySet();
					Iterator<Map.Entry<String, List<Map<String, Integer>>>> iteratorMap = entries.iterator();
					while (iteratorMap.hasNext()) {
						Map.Entry<String, List<Map<String, Integer>>> next = iteratorMap.next();
						if (next.getValue().size() < 1) {
							iteratorMap.remove();
						} else {
							for (Map<String, Integer> existList : next.getValue()
							) {
								if (existList.size() > 0) {
									isExistflag = true;
								}
							}
							if (!isExistflag) {
								iteratorMap.remove();
								break loop2;
							}
						}
					}
				}
			}
			// 说明单品不存在
			if (!flag) {
				if (foodnumber == 1) {
					if (flavorName != null) {
						integerStringMap.put(flavorName, 1);
					} else {
						integerStringMap.put("原味", 1);
					}
					inlist.add(integerStringMap);
					getMap.put(productid+"|"+productname, inlist);
				} else if (foodnumber == -1) {
					return Result.resultData(PublicDictUtil.ERROR_VALUE, "该单品未有数量，减餐失败", null);
				}
			}
		} else {
			if (foodnumber == 1) {
				if (flavorName != null) {
					integerStringMap.put(flavorName, 1);
				} else {
					integerStringMap.put("原味", 1);
				}
				inlist.add(integerStringMap);
				getMap = Maps.newHashMap();
				getMap.put(productid+"|"+productname, inlist);
			} else if (foodnumber == -1) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "还未点过该餐品", null);
			}
		}
//		if (getMap != null) {
//			// 遍历redis缓存的数据
//			for (Map map : getMap
//				 ) {
//				// 匹配成功
//				if (productid.equals(map.get("productid"))) {
//					flag = true;
//					if (foodnumber == 1) {
//						map.put("productNum", (Integer)map.get("productNum")+1);
//					} else if (foodnumber == -1) {
//						if ((Integer)map.get("productNum") > 1) {
//							map.put("productNum", (Integer)map.get("productNum")+1);
//						}
//						if ((Integer)map.get("productNum") < 1) {
//							return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商品数量不足1", null);
//						}
//						if ((Integer)map.get("productNum") == 1) {
//							map.clear();
//						}
//					}
//				}
//			}
//			// 遍历完成后匹配失败
//			if (!flag) {
//				if (foodnumber == 1) {
//					integerStringMap = Maps.newHashMap();
//					integerStringMap.put("productid", productid);
//					integerStringMap.put("productname", productname);
//					integerStringMap.put("flavorName", flavorName);
//					integerStringMap.put("productNum", 1);
//					getMap.add(integerStringMap);
//				} else if (foodnumber == -1) {
//					return Result.resultData(PublicDictUtil.ERROR_VALUE, "还未点过该餐品", null);
//				}
//			}
//		} else {
//			if (foodnumber == 1) {
//				getMap = Lists.newArrayList();
//				integerStringMap = Maps.newHashMap();
//				integerStringMap.put("productid", productid);
//				integerStringMap.put("productname", productname);
//				integerStringMap.put("flavorName", flavorName);
//				integerStringMap.put("productNum", 1);
//				getMap.add(integerStringMap);
//			} else if (foodnumber == -1) {
//				return Result.resultData(PublicDictUtil.ERROR_VALUE, "还未点过该餐品", null);
//			}
//		}
		redisUtils.set(tuserid+tablenumber, getMap, 3600l);
		try {
			List list = Lists.newArrayList();
			Map map = Maps.newHashMap();
			map.put("productid", productid);
			map.put("productname", productname);
			map.put("flavorName", flavorName);
			map.put("foodnumber", foodnumber>0?1:-1);
			list.add(map);
            WebSocket.sendInfo(list, tuserid+tablenumber, openid);
        } catch (Exception e) {
		    return Result.resultData(PublicDictUtil.ERROR_VALUE, "发送websocket失败", null);
        }
		// 将数据进行轮播
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "添加成功", productList);
	}

    /**
     * 通过传入的map进行转换数据类型
     * @param map
     * @return
     */
    public String getStringByMap(Map<String, List<Map<String, Integer>>> map) {
        /**
         * 1. 传入的map有多个单品
         * 2. 对应着多个口味和数量
         * 3. 最后想要的格式为 [{“productname”：“productid”，“productTflovr”：“”，“productNum”：“”}，{}]
         */
        // 初始化返回值
        List reList = Lists.newArrayList();
        Map<String, Object> reMap = null;
        // 多个单品
        for (Map.Entry<String, List<Map<String, Integer>>> mapin : map.entrySet()
             ) {
            for (Map<String, Integer> listin: mapin.getValue()
                 ) {
                for (Map.Entry<String, Integer> listinin : listin.entrySet()
                     ) {
                    reMap = Maps.newHashMap();
                    reMap.put("productId", mapin.getKey().split("\\|")[0]);
                    reMap.put("productName", mapin.getKey().split("\\|")[1]);
                    reMap.put("productTflovr", listinin.getKey());
                    reMap.put("productNum", listinin.getValue());
                    reList.add(reMap);
                }
            }
        }
        return JSONUtils.toJSONString(reList);
    }

	public Result getproductInfo(ProductList productList) {
		if (productList.getId() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品id不能为空", null);
		}
		if (productList.getTableNumber() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号不能为空", null);
		}
		if (productList.getProductName() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品名称不能为空", null);
		}
		String tuserid = productList.getTuserId();
		String tablenumber = productList.getTableNumber();
		String productId = productList.getId();
		String productName = productList.getProductName();

		ProductList pro = productListMapper.getproductInfo(productList);
		List<TFlavor> tList = flavorMapper.getListByProductId(pro.getId());
		pro.setTFlavors(tList);
		String path = config.getServerUrl() + config.getProductImg() + pro.getProductImg();
		pro.setProductImg(path);
		Integer count = 0;
		// 设置口味数量
		Map<String, List<Map<String, Integer>>> getMap;
		getMap = (Map<String, List<Map<String, Integer>>>)redisUtils.get(tuserid+tablenumber);
		if (getMap != null) {
			for (Map.Entry<String, List<Map<String, Integer>>> mapin : getMap.entrySet()
				 ) {
				// 单品配对
				if ((productId+"|"+productName).equals(mapin.getKey())) {
					for (Map<String, Integer> mapinin : mapin.getValue()
						 ) {
						for (Map.Entry<String, Integer> inmap : mapinin.entrySet()
						) {
							if (tList != null) {
								for (TFlavor tFlavor : tList
								) {
									if (tFlavor.getName().equals(inmap.getKey())) {
										tFlavor.setFlovrInitNumber(inmap.getValue());
										count += inmap.getValue();
									}
								}
							}
						}
					}
				}
			}
		}
		pro.setInitNumber(count);
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, pro);
	}

}
