package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.dao.*;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.myEnum.UserEnum;
import com.XZYHOrderingFood.back.pojo.*;
import com.XZYHOrderingFood.back.redis.RedisUtils;
import com.XZYHOrderingFood.back.sms.SendSms;
import com.XZYHOrderingFood.back.util.*;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.bingoohuang.utils.lang.Str;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TUserService {

	@Autowired
	private TUserMapper tUserMapper;

	@Autowired
	private OrderTableMapper orderTableMapper;

	@Autowired
	private OrderTableService orderTableService;

	@Autowired
	private ClassifyDateService classifyDateService;

	@Autowired
	private UserPermissionRefMapper userPermissionRefMapper;

	@Autowired
	private TicketMachineService ticketMachineService;

	@Autowired
	private ApplyResetMapper applyResetMapper;

	@Autowired
	private CacheConfig cacheConfig;

	@Autowired
	private ProductListService productListService;

	@Autowired
	private Config config;

	@Autowired
	private OrderDetailsService orderDetailsService;

	@Autowired
	private TFlavorMapper tFlavorMapper;
	
	@Autowired
	private RedisUtils redis;

	@Autowired
	private ProductListMapper productListMapper;

	public Result add(TUser user, HttpServletRequest request) throws Exception {
//        if (user.getPerList().size() <= 0 || user.getPerList().isEmpty()) {
//            return Result.resultData(PublicDictUtil.ERROR_VALUE, "至少选择一个权限名称", null);
//        }
		user.setIsShops(UserEnum.NO_SHOPS.getCode());
		// 判断用户的账号是否存在
		TUser oldUser = tUserMapper.findUserIsExis(user);
		if (oldUser != null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "账号:" + user.getUsername() + "已经存在", null);
		}
		user.setId(Sid.nextShort());
		user.setIsFirstLogin(UserEnum.IS_FIRST_LOGIN.getCode());
		user.setCreateTime(new Date());
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser != null) {
			user.setCreateId(currentUser.getId());
		}
		user.setStatus(UserEnum.Y_STATUS.getCode());
		user.setSalt(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + System.nanoTime());
		user.setPassword(Coder.encodeMD5(user.getPassword(), user.getSalt()));
		int cont = tUserMapper.insertSelective(user);

		if (cont > 0) {
			// 添加权限
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "添加成功", null);
			/*
			 * cont = userPermissionRefMapper.batchInsert(user); if (cont > 0) { return
			 * Result.resultData(PublicDictUtil.SUCCESS_VALUE, "添加成功", null); }
			 */
		}

		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	/**
	 * 查询TUser的全部数据
	 */
	public Result<List<TUser>> findAll() {
		List<TUser> findAllList = tUserMapper.findAll();
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "你请求成功了", findAllList);
	}

	public Result del(String id) {
		if (id == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "类型id不能为空", null);
		}
		// 判断该类型是否被引用了
		int count = tUserMapper.deleteByPrimaryKey(id);
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	/**
	 * @description: 获取综合信息
	 * @author: zpy
	 * @date: 2019-07-22
	 * @params:
	 * @return:
	 */
	public Result<Map<String, BigDecimal>> getGeneralInfoList(HttpServletRequest request) {
		// 创建返回类
		Integer shopNumber = null;
		Integer expireShop = null;
		BigDecimal moneyCount = null;
		// session中获取用户信息
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "从缓存中获取用户信息失败", null);
		}
		// 返回的list列表
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		try {
			// 查询商户数量
			shopNumber = tUserMapper.countShopNumber();
			// 查询到期商户
			expireShop = tUserMapper.countExpireShop();
			// 查询流水总计
			moneyCount = orderTableMapper.countAllMoney(currentUser);
			// 查询微信接口粉丝数

		} catch (Exception e) {
//            e.printStackTrace();
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "综合信息查询失败，请联系管理员", null);
		}

		map.put("shopNumber", new BigDecimal(shopNumber.toString()));
		map.put("expireShop", new BigDecimal(expireShop.toString()));
		map.put("moneyCount", moneyCount);
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "综合信息查询成功", map);
	}

	public Result edit(TUser user, HttpServletRequest request) throws Exception {
		if (user.getId() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "用户id不能为空", null);
		}
		// 判断账号是否存在
		TUser oldUser = tUserMapper.findUserIsExis(user);
		if (oldUser != null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "账号:" + user.getUsername() + "已经存在", null);
		}
		TUser tempUser = tUserMapper.selectByPrimaryKey(user.getId());
		if (StringUtils.isNotBlank(user.getPassword())) {
			user.setPassword(Coder.encodeMD5(user.getPassword(), tempUser.getSalt()));
		}

		int count = tUserMapper.updateByPrimaryKeySelective(user);
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
			/*
			 * //删除旧用户权限对应关系 count = userPermissionRefMapper.delByUserId(user.getId());
			 * if(count > 0) { return Result.resultData(PublicDictUtil.SUCCESS_VALUE,
			 * "操作成功", null); }
			 */
		}

		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	public Result<Map<String, String>> login(TUser user, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 账号不能为空
		if (StringUtils.isBlank(user.getUsername())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "账号不能为空", null);
		}
		//根据用户名查数据库查出来的密码
		TUser currentUser = tUserMapper.findUserByParam(user);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "该用户不存在", null);
		}
        if(!PublicDictUtil.INITPASSWORD.equals(user.getPassword())) {
            // 判断密码是否正确
            if (!currentUser.getPassword().equals(Coder.encodeMD5(user.getPassword(), currentUser.getSalt()))) {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "密码不正确", null);
            }
        }
		// 判断密码是否正确
		if (!currentUser.getPassword().equals(Coder.encodeMD5(user.getPassword(), currentUser.getSalt()))) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "密码不正确", null);
		}
//		上面的判断都成立的情况下
		Map<String, String> resultMap = Maps.newHashMap();

		if (currentUser.getIsShops() == UserEnum.IS_SHOPS.getCode()) { // 0:公司人员 1:商户
			// 商户
			// 判断用户到期状态

			if (new Date().getTime() < currentUser.getStartTime().getTime()) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE,
						"您的开始使用时间为:" + DateUtil.getStrHms(currentUser.getStartTime()), null);
			}

			if (new Date().getTime() > currentUser.getEndTime().getTime()) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE,
						"您的系统使用已经过期,过期时间为:" + DateUtil.getStrHms(currentUser.getEndTime()), null);
			}

			// 判断用户是否是第一次登录
			if (currentUser.getIsFirstLogin() == UserEnum.IS_FIRST_LOGIN.getCode()) {
				resultMap.put("id", currentUser.getId());
				return Result.resultData(PublicDictUtil.ISFIRSTLOGIN, "第一次登录,请修改密码", resultMap);
			}
		}

		// 判断用户是否被停用
		if (currentUser.getStatus() == UserEnum.N_STATUS.getCode()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "抱歉,您的账号已经被停用", null);
		}
        
		 
		HttpSession session = request.getSession();

		Cache<Object, Object> cache = cacheConfig.cacheToken();
		String token = System.currentTimeMillis() + RandomUtils.getRandomStr(6, 2);
		cache.put(token, currentUser);
		cache.put(PublicDictUtil.USER_TOKE, token);
		log.info("========================当前token为:" + cache.getIfPresent(PublicDictUtil.USER_TOKE));

		// String token = GetCurrentUser.getToken(currentUser);
		// session.setMaxInactiveInterval(60 * 30); //超时时间 为30分钟 = 60 * 30
		/*
		 * session.setAttribute(PublicDictUtil.USER_TOKE, token);
		 * session.setAttribute(PublicDictUtil.USER, currentUser);
		 */
		resultMap.put(PublicDictUtil.USER_TOKE, token);
		resultMap.put("name", currentUser.getClientname());
		resultMap.put("userId", currentUser.getId());
		resultMap.put("userType", currentUser.getIsShops().toString());

		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, resultMap);
	}

	public Result editPassword(TUser user) throws Exception {

		if (StringUtils.isBlank(user.getUsername())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "用户名不能为空", null);
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "密码不能为空", null);
		}
		if (StringUtils.isBlank(user.getContactPhone())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号不能空", null);
		}
		// 判断验证码不能为空
		if (StringUtils.isBlank(user.getOtpCode())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "验证码不能为空", null);
		}
		// 用户名不能重复
		Integer countUser = tUserMapper.selectUsernameIsExist(user.getUsername());
		if (countUser != 1) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "账户存在多个或不存在", null);
		}
		TUser oldUser = tUserMapper.findUsernameIsExist(user);
		// 校验手机号码格式
		Pattern compile = Pattern.compile(PublicDictUtil.PHONE_NUMBER_REG);
		Matcher matcher = compile.matcher(user.getContactPhone());
		if (!matcher.matches()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号格式不正确", null);
		}
		// 判断手机号是否一致
		if (!user.getContactPhone().equals(Coder.decodeDES(oldUser.getContactPhone()))) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "抱歉,手机号:" + user.getContactPhone() + "和您预留的手机号不一致",
					null);
		}
		// 判断过期时间
		if (oldUser.getOtpOverTime().getTime() < new Date().getTime()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "验证码已经过期", null);
		}
		if (!oldUser.getOtpCode().equals(user.getOtpCode())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "验证码不正确", null);
		}
		user.setPassword(Coder.encodeMD5(user.getPassword(), oldUser.getSalt()));
		user.setIsFirstLogin(UserEnum.NO_FIRST_LOGIN.getCode());
		int cont = tUserMapper.updateByUsername(user);
		if (cont > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	public Result applyResetPassword(String username) {
		TUser user = new TUser();
		user.setUsername(username);
		user = tUserMapper.findUserByParam(user);
		if (user == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "该用户不存在", null);
		}

		if (user.getIsShops() == UserEnum.NO_SHOPS.getCode()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "后台用户不能申请重置密码", null);
		}

		ApplyReset applyReset = new ApplyReset();
		applyReset.setCreateTime(new Date());
		applyReset.setUserId(user.getId());
		int count = applyResetMapper.insertSelective(applyReset);

		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		}

		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	public Result resetPassword(String username) {
		TUser user = new TUser();
		user.setUsername(username);
		user = tUserMapper.findUserByParam(user);
		if (user == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "该用户不存在", null);
		}

		TUser tempUser = new TUser();
		tempUser.setId(user.getId());
		tempUser.setIsFirstLogin(UserEnum.IS_FIRST_LOGIN.getCode());
		int count = tUserMapper.updateByPrimaryKeySelective(tempUser);
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

	/**
	 * @description: 获取新增商户折线图数据
	 * @author: zpy
	 * @date: 2019-07-22
	 * @params:
	 * @return:
	 */
	public Result<Map<String, Integer>> getNewShopChart(TUser tUser) {
		/**
		 * 获取新增商户的接口 1. 首先得获取前台传入的时期 2. 获取完时段之后进行判断，不同的时段有不同的查询 3.
		 * 举例如果为天为单位，查询近七天的值，放入map中
		 */

		if (StringUtils.isBlank(tUser.getNewShopLinechart())) {
			log.info("折线图未定义单位");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "折线图未定义单位", null);
		}
		try {
			// 获取昨天日期
			Date dateEnd = DateUtil.yesterdayEndYmd();
			if ("day".equals(tUser.getNewShopLinechart())) {
				// 定义一周的每一天 初始值为0
				Integer Monday = 0;
				Integer Tuesday = 0;
				Integer Wednesday = 0;
				Integer Thursday = 0;
				Integer Friday = 0;
				Integer Saturday = 0;
				Integer Sunday = 0;
				// 初始化返回类
				HashMap<Integer, Integer> weekMap = new HashMap<Integer, Integer>();
				// 获取七天前日期
				Date dateStart = DateUtil.daysAgoYmd(dateEnd, -6, true);
				System.out.println(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateEnd.getTime()) + "---"
						+ new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateStart.getTime()));
				// 遍历商铺
				for (TUser userlist : getTUser(dateStart, dateEnd)) {
					// 对应每个星期数量加1
					switch (DateUtil.todatIsWhichDay(userlist.getStartTime())) {
					case 1:
						Sunday++;
						continue;
					case 2:
						Monday++;
						continue;
					case 3:
						Tuesday++;
						continue;
					case 4:
						Wednesday++;
						continue;
					case 5:
						Thursday++;
						continue;
					case 6:
						Friday++;
						continue;
					case 7:
						Saturday++;
					}
				}
				weekMap.put(1, Sunday);
				weekMap.put(2, Monday);
				weekMap.put(3, Tuesday);
				weekMap.put(4, Wednesday);
				weekMap.put(5, Thursday);
				weekMap.put(6, Friday);
				weekMap.put(7, Saturday);
				// 转换返回格式
				LinkedHashMap linkedHashMap = null;
				try {
					linkedHashMap = orderTableService.getDayMapInteger(weekMap);
				} catch (Exception e) {
					return Result.resultData(PublicDictUtil.ERROR_VALUE, "格式转换失败", null);
				}
				Map returnMap = orderTableService.getReturnMapInteger(linkedHashMap, "日期", "新增商户数量");
				return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以天为单位查询成功", returnMap);
			} else if ("week".equals(tUser.getNewShopLinechart())) {
				/**
				 * 查询近四个周的新增用户 1. 首先获取当前时间 2. 将四个周的数据全部获取 3. 将不同的时间段的数据统计
				 */
				// 初始化计数
				Integer fourWeekAgo = 0;
				Integer threeWeekAgo = 0;
				Integer twoWeekAgo = 0;
				Integer oneWeekAgo = 0;
				// 初始化上周时间
				// 初始化上周起始时间 lastWeekStart dateEnd
				Date lastWeekEnd = DateUtil.daysAgoYmd(dateEnd, -6, false);
				// 获取两周前截至时间 twoLastWeekStart twoLastWeekEnd
				Date twoLastWeekEnd = DateUtil.daysAgoYmd(lastWeekEnd, -1, false);
				// 获取两周前起始时间
				Date twoLastWeekStart = DateUtil.daysAgoYmd(twoLastWeekEnd, -6, true);
				// 获取三周前截至时间
				Date threeLastWeekEnd = DateUtil.daysAgoYmd(twoLastWeekStart, -1, false);
				// 获取三周前起始时间
				Date threeLastWeekStart = DateUtil.daysAgoYmd(threeLastWeekEnd, -6, true);
				// 获取四周前截至时间
				Date fourLastWeekEnd = DateUtil.daysAgoYmd(threeLastWeekStart, -6, false);
				// 获取四周前起始时间
				Date fourLastWeekStart = DateUtil.daysAgoYmd(fourLastWeekEnd, -6, true);

				// 获取四周前日期
				Date dateStart = DateUtil.daysAgoYmd(dateEnd, -27, true);
				// 遍历数据
				for (TUser userlist : getTUser(dateStart, dateEnd)) {
					switch (classifyDateService.judgeIsWhichMonth(userlist.getStartTime(), dateEnd, lastWeekEnd,
							twoLastWeekEnd, twoLastWeekStart, threeLastWeekEnd, threeLastWeekStart, fourLastWeekEnd,
							fourLastWeekStart)) {
					case 1:
						oneWeekAgo++;
						continue;
					case 2:
						twoWeekAgo++;
						continue;
					case 3:
						threeWeekAgo++;
						continue;
					case 4:
						fourWeekAgo++;
					}
				}
				// 初始化返回类
				LinkedHashMap<String, Integer> weekMap = new LinkedHashMap<String, Integer>();
				weekMap.put("近一周", oneWeekAgo);
				weekMap.put("近两周", twoWeekAgo);
				weekMap.put("近三周", threeWeekAgo);
				weekMap.put("近四周", fourWeekAgo);
				Map returnMap = orderTableService.getReturnMapInteger(weekMap, "日期", "新增商户数量");
				return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以周为单位查询成功", returnMap);
			} else if ("year".equals(tUser.getNewShopLinechart())) {
				// 初始化12个月
				// 一月
				int January = 0;
				// 二月
				int February = 0;
				// 三月
				int March = 0;
				// 四月
				int April = 0;
				// 五月
				int May = 0;
				// 六月
				int June = 0;
				// 七月
				int July = 0;
				// 八月
				int August = 0;
				// 九月
				int September = 0;
				// 十月
				int October = 0;
				// 十一月
				int November = 0;
				// 十二月
				int December = 0;

				// 获取一年前的时间
				Date lastYear = DateUtil.reckonYears(dateEnd, -1);
				// 初始化添加年份map
				Map map = new HashMap();
				// 遍历数据
				for (TUser tUser1 : getTUser(lastYear, dateEnd)) {
					switch (DateUtil.getWhichMonthbyDate(tUser1.getStartTime())) {
					case 1:
						January++;
						continue;
					case 2:
						February++;
						continue;
					case 3:
						March++;
						continue;
					case 4:
						April++;
						continue;
					case 5:
						May++;
						continue;
					case 6:
						June++;
						continue;
					case 7:
						July++;
						continue;
					case 8:
						August++;
						continue;
					case 9:
						September++;
						continue;
					case 10:
						October++;
						continue;
					case 11:
						November++;
						continue;
					case 12:
						December++;

					}
				}
				// 初始化月份集合
				Map<Integer, Integer> weekMap = new HashMap<Integer, Integer>();
				weekMap.put(1, January);
				weekMap.put(2, February);
				weekMap.put(3, March);
				weekMap.put(4, April);
				weekMap.put(5, May);
				weekMap.put(6, June);
				weekMap.put(7, July);
				weekMap.put(8, August);
				weekMap.put(9, September);
				weekMap.put(10, October);
				weekMap.put(11, November);
				weekMap.put(12, December);
				// 添加年月
				LinkedHashMap linkedHashMap = orderTableService.setMapYearAndMonthInteger(weekMap);
				// 转换为echarts格式
				Map returnMap = orderTableService.getReturnMapInteger(linkedHashMap, "日期", "新增商户数量");
				return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以年为单位查询成功", returnMap);
			} else {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "请传输正确时段", null);
			}
		} catch (ParseException e) {
			log.info(e.getMessage());
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "新增商户折线图查询失败", null);
		}
	}

	/**
	 * @description: 以省为单位查询用户数量
	 * @author: zpy
	 * @date: 2019-07-23
	 * @params:
	 * @return:
	 */
	public Result<Map<String, Integer>> getUserNumber(HttpServletRequest request) {
		// 查询用户
		/**
		 * 1. 首先从数据库查询所有数据 2. 把省存入set集合中 3. 最终生成list集合
		 */
		List<TUser> tUserList = null;
		// 初始化mybatis查询集合
		Map<String, Object> selectMap = new HashMap<String, Object>();
		// session缓存中取user
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		// 判断用户是否存在
		if (currentUser != null) {
			selectMap.put("id", currentUser.getId());
			selectMap.put("isshops", currentUser.getIsShops().toString());
			System.out.println(currentUser.getId() + "--" + currentUser.getIsShops().toString());
		} else {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "session中用户信息失效", null);
		}
		try {
			tUserList = tUserMapper.getShopInfo(selectMap);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "省商户环形图查询失败", null);
		}
		/**
		 * 1. 首先遍历数据取出所有省份 2. 然后把省份信息和省份名称放入map集合中
		 */
		// 初始化省份集合
		ArrayList<String> arrayLis = new ArrayList<String>();
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		// 遍历省份
		for (TUser tUser : tUserList) {
			try {
				if (tUser.getProvince() != null) {
					if (!arrayLis.contains(tUser.getProvince())) {
						arrayLis.add(tUser.getProvince());
						map.put(tUser.getProvince(), 1);
					} else {
						map.put(tUser.getProvince(), (map.get(tUser.getProvince())) + 1);
					}
				}
			} catch (Exception e) {
				Result.resultData(PublicDictUtil.ERROR_VALUE, "数据异常，请联系管理员", null);
			}
		}
		Map returnMap = orderTableService.getReturnMapInteger(map, "城市", "商户数量");
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "省商户环形图查询成功", returnMap);
	}

	/**
	 * @description: 查询所有商户信息
	 * @author: zpy
	 * @date: 2019-07-24
	 * @params:
	 * @return:
	 */
	public Result getAllUserInfo(TUser tUser, HttpServletRequest request) {
		if (tUser.getLocation() != null) {
			try {
				tUser.setProvince(tUser.getLocation().get(0));
				tUser.setCity(tUser.getLocation().get(1));
				tUser.setArea(tUser.getLocation().get(2));
			} catch (Exception e) {
				Result.resultData(PublicDictUtil.ERROR_VALUE, "省市区赋值失败", null);
			}
		}
		// 初始化返回集合
		List<TUser> tUserList = null;
		// 初始化mybatis查询集合
		Map<String, Object> map = new HashMap<String, Object>();
		// session缓存中取user
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		// 判断用户是否存在
		if (currentUser != null) {
			map.put("id", currentUser.getId());
			map.put("isShops", currentUser.getIsShops().toString());
		} else {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "session中用户信息失效", null);
		}
		// 手机号加密比对
		if (StringUtils.isNotBlank(tUser.getContactPhone())) {
			try {
				if(StringUtils.isNotBlank(tUser.getContactPhone())) {
					tUser.setContactPhone(Coder.encodeDES(tUser.getContactPhone()));
				}
			} catch (Exception e) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号加密失败", null);
			}
		}
		// 添加查询条件
		map.put("tuser", tUser);
		if (tUser.getExpireTimeFlag() != null) {
			try {
				if (tUser.getExpireTimeFlag() == 0) {
					map.put("endDate", DateUtil.daysAgoYmd(new Date(), -30, true));
				} else if (tUser.getExpireTimeFlag() == 1) {
					map.put("startDate", DateUtil.daysAgoYmd(new Date(), -31, false));
					map.put("endDate", DateUtil.daysAgoYmd(new Date(), -315, true));
				} else if (tUser.getExpireTimeFlag() == 2) {
					map.put("startDate", DateUtil.daysAgoYmd(new Date(), -316, false));
				}
			} catch (Exception e) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "时间转换失败", null);
			}
		}
		// 判断是否为商户，商户的话进行小票机状态查询
		Integer ticketStatus = null;
		if (currentUser.getIsShops() == 1) {
			TicketMachine ticketMachine = null;
			try {
				ticketMachine = ticketMachineService.getTicketMachineInfoByUserId(currentUser);
			} catch (Exception e) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "小票机状态查询失败", null);
			}
			if (ticketMachine != null) {
				ticketStatus = ticketMachine.getIsUsed();
			}
		}
		try {
			if (tUser.getPageNo() != null && tUser.getPageSize() != null) {
				PageHelper.startPage(tUser.getPageNo(), tUser.getPageSize());
			} else {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "分页未获取到数据", null);
			}
			// 数据库查询数据
			tUserList = tUserMapper.getShopInfo(map);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户信息查询失败", null);
		}
		// 如果为商家，即查出一条商户数据
		if (currentUser.getIsShops() == 1) {
			TUser tUser1 = tUserList.get(0);
			tUser1.setTicketIsUsed(ticketStatus);
			// 手机号解密
			try {
				if (StringUtils.isNotBlank(tUser1.getContactPhone())) {
					if(StringUtils.isNotBlank(tUser1.getContactPhone())) {
						tUser1.setContactPhone(Coder.decodeDES(tUser1.getContactPhone()));
					}
					
				}
			} catch (Exception e) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE,
						"手机号解密失败", null);
			}
			// 拼接省市区
			List<String> strings = new ArrayList<>();
			if (tUserList.get(0).getProvince() != null) {
				strings.add(tUserList.get(0).getProvince());
			}
			if (tUserList.get(0).getCity() != null) {
				strings.add(tUserList.get(0).getCity());
			}
			if (tUserList.get(0).getArea() != null) {
				strings.add(tUserList.get(0).getArea());
			}
			tUserList.get(0).setLocation(strings);
		} else {
			for (TUser user : tUserList) {
				try {
					/*
					 * // 手机号解密 if(StringUtils.isNotBlank(user.getContactPhone())) {
					 * user.setContactPhone(Coder.decodeDES(user.getContactPhone())); }
					 */

					// 拼接省市区
					List<String> strings = new ArrayList<>();
					if (user.getProvince() != null) {
						strings.add(user.getProvince());
					}
					if (user.getCity() != null) {
						strings.add(user.getCity());
					}
					if (user.getArea() != null) {
						strings.add(user.getArea());
					}
					user.setLocation(strings);
				} catch (Exception e) {
					e.printStackTrace();
					//return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号解密失败", null);
				}
			}
		}
		tUserList.forEach(ur -> {

			if (StringUtils.isNotBlank(ur.getContactPhone())) {
				try {
					ur.setContactPhone(Coder.decodeDES(ur.getContactPhone()));
					List<Integer> intLocations = new ArrayList<Integer>();
					List<String> strings = ur.getLocation();
					strings.forEach(str -> {
						if (StringUtils.isNotBlank(str)) {
							intLocations.add(Integer.valueOf(str));
						}

					});
					ur.setIntLocations(intLocations);
				} catch (Exception e) {
					//throw new CustomException(PublicDictUtil.ERROR_VALUE, "手机号解密失败");
				}
			}
		});
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "商户信息查询成功", new BasePage<TUser>(tUserList));
	}

	/**
	 * @description: 创建新增商户接口
	 * @author: zpy
	 * @date: 2019-07-24
	 * @params:
	 * @return:
	 */
	public Result createNewUser(TUser tUser, HttpServletRequest request) throws Exception {
		// 对前台传入参数进行校验
		if (StringUtils.isBlank(tUser.getUsername())) {
			log.info("后台账户名为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "后台账户名为空", null);
		}
		if (StringUtils.isBlank(tUser.getClientname())) {
			log.info("客户名称为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户名称为空", null);
		}
		if (!(tUser.getDateMap().size() > 1)) {
			log.info("传入时间有误");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "传入时间有误", null);
		}
		if (!(tUser.getLocation().size() > 2)) {
			log.info("省市区不为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "省市区不为空", null);
		}
		if (StringUtils.isBlank(tUser.getAddress())) {
			log.info("客户详细地址为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户详细地址为空", null);
		}
		if (StringUtils.isBlank(tUser.getContactName())) {
			log.info("联系人姓名为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "联系人姓名为空", null);
		}
		if (StringUtils.isBlank(tUser.getContactPhone())) {
			log.info("联系人电话为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户名称为空", null);
		}
		if (StringUtils.isBlank(tUser.getOpenId())) {
			log.info("openId为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "openId为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getTableMaxNumber()))) {
			log.info("最大餐桌数为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "最大餐桌数为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getStartTime()))) {
			log.info("使用起始时间不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "使用起始时间不能为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getTableMaxNumber()))) {
			log.info("使用截至时间不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "使用截至时间不能为空", null);
		}
		// 判断用户的账号是否存在
		try {
			TUser oldUser = tUserMapper.findUserIsExis(tUser);
			if (oldUser != null) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "账号:" + oldUser.getUsername() + "已经存在", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询账号是否存在失败", null);
		}
		Integer count = null;
		try {
			count = tUserMapper.findUserPhoneIsExis(tUser.getContactPhone());
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "用手机号查询失败", null);
		}
		if (count > 0) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号已经被注册", null);
		}
		// 设置主键
		tUser.setId(Sid.nextShort());
		// 设置第一次登陆
		tUser.setIsFirstLogin(UserEnum.IS_FIRST_LOGIN.getCode());
		// 创建时间
		tUser.setCreateTime(new Date());
		// seession 中获取操作用户信息
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser != null) {
			tUser.setCreateId(currentUser.getId());
		}
		// 校验手机号码格式
		Pattern compile = Pattern.compile(PublicDictUtil.PHONE_NUMBER_REG);
		Matcher matcher = compile.matcher(tUser.getContactPhone());
		if (!matcher.matches()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号格式不能正确", null);
		}
		// 手机号加密
		try {
			tUser.setContactPhone(Coder.encodeDES(tUser.getContactPhone()));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号加密失败", null);
		}
		// 设置为商户用户
		tUser.setIsShops(UserEnum.IS_SHOPS.getCode());
		tUser.setStatus(UserEnum.Y_STATUS.getCode());
		tUser.setSalt(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + System.nanoTime());
		tUser.setPassword(Coder.encodeMD5(tUser.getPassword(), tUser.getSalt()));
		// 设置省市区
		try {
			tUser.setProvince(tUser.getLocation().get(0));
			tUser.setCity(tUser.getLocation().get(1));
			tUser.setArea(tUser.getLocation().get(2));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "设置省市区失败", null);
		}
		// 判断起始时间早于结束时间
		if (!(tUser.getDateMap().get(0).compareTo(tUser.getDateMap().get(1)) < 0)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "起始日期晚于截至日期", null);
		}
		// 判断起始时间晚于今天
		try {
			if (!(DateUtil.getEndOrStartDate(new Date(), false).compareTo(tUser.getDateMap().get(0)) < 0)) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "起始日期不应早于今天", null);
			}
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "时间转换异常", null);
		}
		// 设置商户起始时间和结束时间
		try {
			tUser.setStartTime(tUser.getDateMap().get(0));
			tUser.setEndTime(tUser.getDateMap().get(1));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "设置时间异常", null);
		}
		// 插入数据
		try {
			int result = tUserMapper.insertSelective(tUser);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "新增商户失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "新增商户成功", null);
	}

	/**
	 * @description: 修改用户信息接口
	 * @author: zpy
	 * @date: 2019-07-24
	 * @params:
	 * @return:
	 */
	public Result updateUserInfo(TUser tUser) {
		if (StringUtils.isBlank(tUser.getId())) {
			log.info("后台账户名为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "后台账户名为空", null);
		}
		TUser oldusr = tUserMapper.selectByPrimaryKey(tUser.getId());
		if (StringUtils.isBlank(tUser.getUsername())) {
			log.info("后台账户名为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "后台账户名为空", null);
		}
		if (!(tUser.getDateMap().size() > 1)) {
			log.info("传入时间有误");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "传入时间有误", null);
		}
		if (!(tUser.getLocation().size() > 2)) {
			log.info("省市区不为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "省市区不为空", null);
		}
		if (StringUtils.isBlank(tUser.getClientname())) {
			log.info("客户名称为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户名称为空", null);
		}
		if (StringUtils.isBlank(tUser.getAddress())) {
			log.info("客户详细地址为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户详细地址为空", null);
		}
		if (StringUtils.isBlank(tUser.getContactName())) {
			log.info("联系人姓名为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "联系人姓名为空", null);
		}
		if (StringUtils.isBlank(tUser.getContactPhone())) {
			log.info("联系人电话为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "客户名称为空", null);
		}
		if (StringUtils.isBlank(tUser.getOpenId())) {
			log.info("openId为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "openId为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getTableMaxNumber()))) {
			log.info("最大餐桌数为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "最大餐桌数为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getStartTime()))) {
			log.info("使用起始时间不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "使用起始时间不能为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getTableMaxNumber()))) {
			log.info("使用截至时间不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "使用截至时间不能为空", null);
		}
		if (StringUtils.isBlank(String.valueOf(tUser.getStatus()))) {
			log.info("商户状态值为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户状态值为空", null);
		}
		// 设置省市区
		try {
			tUser.setProvince(tUser.getLocation().get(0));
			tUser.setCity(tUser.getLocation().get(1));
			tUser.setArea(tUser.getLocation().get(2));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "设置省市区失败", null);
		}
		// 判断起始时间早于结束时间
		if (!(tUser.getDateMap().get(0).compareTo(tUser.getDateMap().get(1)) < 0)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "起始日期晚于截至日期", null);
		}
		// 判断起始时间晚于今天
		try {
			  String paramTime = DateUtil.getStrHms(tUser.getDateMap().get(0));  
			  String startTime = DateUtil.getStrHms(oldusr.getStartTime()) ;
			if(!paramTime.equals(startTime)) {
				if (!(DateUtil.getEndOrStartDate(new Date(), false).compareTo(tUser.getDateMap().get(0)) < 0)) {
					return Result.resultData(PublicDictUtil.ERROR_VALUE, "起始日期不应早于今天", null);
				}
			} 
			
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "时间转换异常", null);
		}
		// 设置商户起始时间和结束时间
		try {
			tUser.setStartTime(tUser.getDateMap().get(0));
			tUser.setEndTime(tUser.getDateMap().get(1));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "设置时间异常", null);
		}
		try {
			tUserMapper.updateByPrimaryKeySelective(tUser);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改商户信息失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改商户信息成功", null);
	}

	/**
	 * @description: 添加商户详情
	 * @author: zpy
	 * @date: 2019-07-25
	 * @params:
	 * @return:
	 */
	public Result addShopDetalInfo(TUser tUser, HttpServletRequest request) {
		if (StringUtils.isBlank(tUser.getShopNotice())) {
			log.info("商家公告为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家公告为空", null);
		}
		if (StringUtils.isBlank(tUser.getParkSpace())) {
			log.info("商家停车位为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家公告为空", null);
		}
		if (StringUtils.isBlank(tUser.getBusinessHoursBegin())) {
			log.info("商家营业时间起始时间不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家营业时间起始时间不能为空", null);
		}
		if (StringUtils.isBlank(tUser.getBusinessHoursEnd())) {
			log.info("商家营业时间截至时间不能为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家营业时间截至时间不能为空", null);
		}
		if (StringUtils.isBlank(tUser.getSpecialService())) {
			log.info("商家特色服务不能为空 ");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家公告为空", null);
		}
		/*
		 * if (pImg == null) { log.info("店铺图片不能为空 "); return
		 * Result.resultData(PublicDictUtil.ERROR_VALUE, "店铺图片不能为空", null); }
		 */
		// seession 中获取操作用户信息
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		/*
		 * if(pImg != null) { //上传图片 String tempData =
		 * productListService.uploadFile(pImg); if("no".equals(tempData) ||
		 * "noType".equals(tempData)) { return
		 * Result.resultData(PublicDictUtil.ERROR_VALUE, "图片上传失败,请检查类型和网络", null); }else
		 * { tUser.setShopImg(tempData); } }
		 */
		if (tUser.getShopImg() != null) {
			String path = config.getUploadPath() + config.getShopImg() + tUser.getShopImg();
			if (productListService.delFile(path)) {
				log.info("文件删除成功");
			}
		}
		// 手机号加密
		try {
			tUser.setContactPhone(Coder.encodeDES(tUser.getContactPhone()));
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号加密失败", null);
		}
		// 设置id
		tUser.setId(currentUser.getId());
		int result = 0;
		try {
			result = tUserMapper.updateByPrimaryKeySelective(tUser);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "保存商户详细信息失败", null);
		}
		if (result > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "保存商户详细信息成功", null);
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "保存商户详细信息失败", null);
		}
	}

	/**
	 * @description: 商户收入折线图
	 * @author: zpy
	 * @date: 2019-07-25
	 * @params:
	 * @return:
	 */
	public Result getShopIncomeLine(TUser tUser, HttpServletRequest request) {
		// 判断传入判断时段值
		if (StringUtils.isBlank(tUser.getNewShopLinechart())) {
			log.info("时段不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "时段不能为空", null);
		}
		OrderTable orderTable = new OrderTable();
		orderTable.setOrderTableLineChart(tUser.getNewShopLinechart());
		return orderTableService.getOrderTableLineChart(orderTable, request);
	}

	/**
	 * @description: 从数据库查询数据
	 * @author: zpy
	 * @date: 2019-07-23
	 * @params:
	 * @return:
	 */
	List<TUser> getTUser(Date startdate, Date enndate) {
		HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("dateStart", startdate);
		map.put("dateEnd", enndate);
		return tUserMapper.getNewShopCharts(map);
	}

	public Result sendSmsCode(String phone) throws Exception {
		if (StringUtils.isBlank(phone)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号不能为空", null);
		}
		// 校验手机号码格式
		Pattern compile = Pattern.compile(PublicDictUtil.PHONE_NUMBER_REG);
		Matcher matcher = compile.matcher(phone);
		if (!matcher.matches()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号格式不能正确", null);
		}
		TUser usr = new TUser();
		usr.setContactPhone(Coder.encodeDES(phone));
		TUser oldUser = tUserMapper.findUserIsExis(usr);
		if (oldUser == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "当前手机号没有绑定任何用户", null);
		}
		// 判断手机号和预留手机号是否一致
		if (!phone.equals(Coder.decodeDES(oldUser.getContactPhone()))) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "抱歉,手机号:" + phone + "和您预留的手机号不一致", null);
		}
		// 发送信息
		String tempCodeString = RandomUtils.getRandomStr(6, 3);
		usr.setId(oldUser.getId());
		long endTime = new Date().getTime() + (5 * 60 * 1000);
		usr.setOtpOverTime(new Date(endTime));
		String tempCode = RandomUtils.getRandomStr(6, 3);
		usr.setOtpCode(tempCode);
		int count = tUserMapper.updateByPrimaryKeySelective(usr);
		if (count > 0) {
			// 发送短息验证码
			boolean flag = SendSms.SendSms(phone, tempCode);
			if (flag) {
				return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "发送成功", null);
			}
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "发送验证码失败", null);
	}

	/**
	 * @description: 通过id查询商户信息
	 * @author: zpy
	 * @date: 2019-08-01
	 */
	public Result getUserInfoById(TUser tUser) {
		if (StringUtils.isBlank(tUser.getId())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键id为空", null);
		}
		TUser tUserinfo = null;
		try {
			tUserinfo = tUserMapper.selectByPrimaryKey(tUser.getId());
			// 拼接省市区
			List<String> strings = new ArrayList<>();
			List<Integer> locatins = new ArrayList<Integer>();
			if (tUserinfo.getProvince() != null) {
				strings.add(tUserinfo.getProvince());
			}
			if (tUserinfo.getCity() != null) {
				strings.add(tUserinfo.getCity());
			}
			if (tUserinfo.getArea() != null) {
				strings.add(tUserinfo.getArea());
			}
			tUserinfo.setLocation(strings);

		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询商户失败", null);
		}
		if (tUserinfo != null) {
			 
			if (StringUtils.isNotBlank(tUserinfo.getContactPhone())) {
				try {
					
					if(StringUtils.isNotBlank(tUserinfo.getContactPhone())) {
						tUserinfo.setContactPhone(Coder.decodeDES(tUserinfo.getContactPhone()));
					}

				} catch (Exception e) {
					throw new CustomException(PublicDictUtil.ERROR_VALUE, "手机号解密失败");
				}
			}
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询成功", tUserinfo);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "通过id查询失败", null);
	}

	/**
	 * @description: 修改小票机状态
	 * @author: zpy
	 * @date: 2019-08-01
	 * @params:
	 * @return:
	 */
	public Result updateTicketMachineStatus(TUser tUser) {
		if (StringUtils.isBlank(tUser.getId())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键id为空", null);
		}
		if (tUser.getTicketIsUsed() == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "小票机状态不能空", null);
		}
		Integer count = null;
		try {
			count = ticketMachineService.updateTicketMachineStatus(tUser);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改状态失败", null);
		}
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改商铺失败", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改商铺失败", null);
	}

	public TUser getByUserId(String userId) {

		return tUserMapper.selectByPrimaryKey(userId);
	}

	public Result logout(HttpServletRequest request) {
		Cache<Object, Object> cache = cacheConfig.cacheToken();
		String token = request.getHeader("token");
		if (StringUtils.isNotBlank(token)) {
			cache.invalidate(token);
			TUser user = GetCurrentUser.getCurrentUser(request);
			if (user == null) {
				return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "退出成功", null);
			}
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "退出失败", null);
	}

	/**
	 * @description: 查询后台账户
	 * @author: zpy
	 * @date: 2019-08-02
	 */
	public Result getBackUserInfo(TUser tUser) {
		// 分页不能为空
		if (tUser.getPageNo() != null && tUser.getPageSize() != null) {
			PageHelper.startPage(tUser.getPageNo(), tUser.getPageSize());
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "分页未获取到数据", null);
		}
		List<TUser> tUserList = null;
		try {
			tUserList = tUserMapper.selectBackEndUser();
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询后台账户失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询成功", new BasePage<TUser>(tUserList));
	}

	public Result<TUser> getUserInfo(String id, HttpServletRequest request) {
		TUser user = null;
		if (StringUtils.isNotBlank(id)) {
			user = tUserMapper.selectByPrimaryKey(id);
		} else {
			TUser cUser = GetCurrentUser.getCurrentUser(request);
			user = tUserMapper.selectByPrimaryKey(cUser.getId());
		}
		if (user == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		}
		user.setPassword(null);
		try {
			if (StringUtils.isNotBlank(user.getContactPhone())) {
				user.setContactPhone(Coder.decodeDES(user.getContactPhone()));
			}
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE,
					"手机号解密失败", null);
		}
		// 获取图片路径
		if (user.getShopImg() != null) {
			user.setShopImg(config.getServerUrl() + config.getShopImg() + user.getShopImg());
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, user);
	}

    /**
     * @description: 查询后台账户
     * @author: zpy
     * @date: 2019-08-02
     */
    public Result selectShopInfo(TUser tUser) {
        if (StringUtils.isBlank(tUser.getId())) {
            log.info("主键不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键不能为空", null);
        }
        TUser user = null;
        try {
            user = tUserMapper.selectByPrimaryKey(tUser.getId());
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, " 查询商户失败", null);
        }
		// 获取图片路径
		if (user.getShopImg() != null) {
			user.setShopImg(config.getUploadPath() + config.getProductImg() + user.getShopImg());
		}
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, " 查询商户成功", user);
    }

    /**
     * @description: 通过openid和userid查询订单详情
     * @author: zpy
     * @date: 2019-08-05
     */
    public Result selectOrderInfo(OrderTable orderTable) {
        if (StringUtils.isBlank(orderTable.getTuserId())) {
            log.info("主键不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键不能为空", null);
        }
        if (StringUtils.isBlank(orderTable.getOpenId())) {
            log.info("openid不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "openid不能为空", null);
        }
        OrderTable ordertable = null;
        try {
            ordertable = orderTableMapper.selectByOpenIdAndUserId(orderTable);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单表失败", null);
        }
        List<String> list = new ArrayList<>();
		List<OrderDetails> details = null;
        if (orderTable != null) {
        	list.add(ordertable.getOrderCode());
			details = orderDetailsService.getOrderDetailsByListId(list);
		}
        List<String> tflovrList = new ArrayList<>();
        List<String> foodIdList = new ArrayList<>();
		for (OrderDetails details1 : details
			 ) {
			tflovrList.add(details1.getTflavorId());
			foodIdList.add(details1.getFoodId());
			if (details1.getPrice()!=null && details1.getFoodNumber()!=null) {
				details1.setMoneyCount(details1.getPrice().multiply(BigDecimal.valueOf(details1.getFoodNumber())));
			}
		}
		List<ProductList> productLists = productListMapper.selectProductListByIdList(foodIdList);
        if (details != null) {
			for (OrderDetails detail:details
				 ) {
				if (productLists != null) {
					for (ProductList pro : productLists) {
						if (pro.getId().equals(detail.getFoodId())) {
							detail.setProductPath(config.getServerUrl() + config.getProductImg().replaceAll("\\\\", "/")+pro.getProductImg()); // 添加图片路径
						}
					}
				}
			}
			ordertable.setOrderDetails(details);
		} else {
        	return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单详情查询失败", null);
		}
        // 通过单品查询口味
		List<TFlavor> flavors = null;
		if (tflovrList != null) {
			flavors = tFlavorMapper.selectTFavorList(tflovrList);
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询口味失败", null);
		}
		if (flavors != null) {
			for (OrderDetails deta : details
			) {
				for (TFlavor flavor: flavors
					 ) {
					if (deta.getTflavorId() != null) {
						if (deta.getTflavorId().equals(flavor.getId())) {
							deta.setTFlavor(flavor);
						}
					}
				}
			}
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "口味查询失败", null);
		}
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询订单表成功", ordertable);
    }

    /**
     * 商铺上传图片
     * @param request
     * @param pImg
     * @return
     */
    public Result uploadShopImg(MultipartFile pImg, HttpServletRequest request) {
		if (pImg == null) {
			log.info("文件不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "文件不能为空", null);
		}
		// 从session缓存中获取用户
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		if (currentUser.getShopImg() != null) {
			//删除原来图片
//			int count = productListMapper.updateByPrimaryKeySelective(productList);
			String path = config.getUploadPath() + config.getShopImg() + currentUser.getShopImg();
			if (!productListService.delFile(path)) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "原文件不存在或删除文件失败", null);
			}
		}
		//上传图片
		String tempData = uploadFile(pImg);
		if("no".equals(tempData) || "noType".equals(tempData)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "图片上传失败,请检查类型和网络", null);
		}
		currentUser.setModifyTime(new Date());
		currentUser.setShopImg(tempData);
		int count = tUserMapper.updateByPrimaryKeySelective(currentUser);
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改图片成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改图片失败", null);
    }

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
			path = config.getUploadPath() + config.getShopImg();
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
//			ThumbnailsUtil tu = new ThumbnailsUtil();
//			ThumbnailsUtil.shrinkToSize(Integer.parseInt(PublicDictUtil.WIDTH),Integer.parseInt(PublicDictUtil.HEIGHT), targetFile.getAbsolutePath(), path,newFileName);
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

	/**
	 * 修改
	 * @return
	 * @param tUser
	 * @param request
	 */
	public Result updateUserBusinessStatus(TUser tUser, HttpServletRequest request) {
		if (StringUtils.isBlank(tUser.getTicketStatus().toString())) {
			log.info("openid不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "openid不能为空", null);
		}
		// session中获取用户信息
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "从缓存中获取用户信息失败", null);
		}
		// 通过用户修改状态
		if (currentUser.getTicketStatus() == tUser.getTicketStatus()) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改后的状态相同", null);
		}
		currentUser.setTicketStatus(tUser.getTicketStatus());
		int count = tUserMapper.updateByPrimaryKeySelective(currentUser);
		if (count > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改营业状态成功", null);
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改营业状态失败", null);
		}
	}

	/**
	 * 通过单品查询 购物车
	 * @param orderTable
	 * @return
	 */                             // 商户id 桌号 单品id
	public Result selectProductInfo(OrderTable orderTable) {
		if (StringUtils.isBlank(orderTable.getTuserId())) {
			log.info("商户id不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id不能为空", null);
		}
		if (StringUtils.isBlank(orderTable.getTableNumber())) {
			log.info("桌号不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号不能为空", null);
		}
		if (StringUtils.isBlank(orderTable.getFoodId())) {
			log.info("单品不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "单品不能为空", null);
		}
		if (StringUtils.isBlank(orderTable.getFoodName())) {
			log.info("桌号不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号不能为空", null);
		}
		// 商户id
		String tuserid = orderTable.getTuserId();
		// 桌号
		String tablenumber = orderTable.getTableNumber();
		// 单品id
		String productid = orderTable.getFoodId();
		// 单品名称
		String productname = orderTable.getFoodName();

		// 通过id查询单品
		ProductList productList = productListMapper.selectByPrimaryKey(productid);

		BigDecimal price = null;
		if (productList.getPrice() != null) {
			price = productList.getPrice();
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "无法获取到单品价格，请联系管理员", null);
		}

		Map<String, List<Map<String, Integer>>> map = null;
		List<Map<String, Integer>> returnList = null;
		map = (Map<String, List<Map<String, Integer>>>) redis.get(tuserid + tablenumber);
		if (map != null) {
			for (Map.Entry<String, List<Map<String, Integer>>> mapin : map.entrySet()
			) {
				// 单品存在
				if ((productid+"|"+productname).equals(mapin.getKey())) {
					if (mapin.getValue() != null) {
						returnList = mapin.getValue();
					}
				}
			}
		}
		// 获取数量
//		Integer count = 0;
//		if (returnList != null) {
//			for (Map<String, Integer> list : returnList
//			) {
//				for (Map.Entry<String, Integer> inmap : list.entrySet()
//				) {
//					count += inmap.getValue();
//				}
//			}
//			if (count == null) {
//				return Result.resultData(PublicDictUtil.ERROR_VALUE, "存在单品不存在数量，数据异常请联系管理员", null);
//			}
//		}
//		List list = Lists.newArrayList();
//
//		if (count != null) {
//			price = price.add(price.multiply(BigDecimal.valueOf(count)));
//		} else {
//			price = null;
//		}
//		list.add(price);
//		list.add(returnList);
//		list.add(count);

        List relist = new ArrayList();
        Map<String, Object> inMap = null;
        // 做口味数量
        if (returnList != null) {
            for (Map<String, Integer> mapin : returnList
            ) {
                inMap = new HashMap<>();
                for (Map.Entry<String, Integer> mapinin : mapin.entrySet()) {
                    inMap.put("productId", productname);
                    inMap.put("productName", productname);
                    inMap.put("price", price.multiply(BigDecimal.valueOf(mapinin.getValue())));
                    inMap.put("productFlovr", mapinin.getKey());
                    inMap.put("productNum", mapinin.getValue());
                }
                relist.add(inMap);
            }
        }
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "通过该单品查询缓存成功", relist);
	}

	/**
	 * 通过商户和桌号查询 购物车全部数据
	 * @param orderTable
	 * @return
	 */                             // 商户id 桌号
	public Result selectShopCartInfo(OrderTable orderTable) {
		if (StringUtils.isBlank(orderTable.getTuserId())) {
			log.info("商户id不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商户id不能为空", null);
		}
		if (StringUtils.isBlank(orderTable.getTableNumber())) {
			log.info("桌号不能为空");
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号不能为空", null);
		}
		// 商户id
		String tuserid = orderTable.getTuserId();
		// 桌号
		String tablenumber = orderTable.getTableNumber();
		// 标志位
		boolean flag ;
		Map<String, List<Map<String, Integer>>> map = null;
		Map<String, Integer> moneyMap = null;
		Map inMap = null;
		List<Object> returnList = new ArrayList<>();
		map = (Map<String, List<Map<String, Integer>>>)redis.get(tuserid+tablenumber);
		// 总金额
		BigDecimal moneyCount = new BigDecimal("0");
		BigDecimal singleMoneyCount = null;
		Integer countNum = null;
		List<ProductList> productLists = productListService.selectProductListByUserId(orderTable);
		if (!(productLists.size() > 0)) {return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询该商家单品信息为空", null);}
		if (map != null) {
			for (Map.Entry<String, List<Map<String, Integer>>> mapin : map.entrySet()
			) {
				flag = false;
				for (ProductList productList : productLists
				) {
					countNum = 0;
					// 单品匹配成功
					singleMoneyCount = new BigDecimal("0");
					if ((productList.getId()+"|"+productList.getProductName()).equals(mapin.getKey())) {
						for (Map<String, Integer> flovrmap : mapin.getValue()
						) {
							for (Map.Entry<String, Integer> flovrmapin : flovrmap.entrySet()
							) {
								countNum += flovrmapin.getValue();
								flag = true;
								inMap = Maps.newHashMap();
								inMap.put("foodId", mapin.getKey().split("\\|")[0]);
								inMap.put("foodName", mapin.getKey().split("\\|")[1]);
								inMap.put("price", productList.getPrice().multiply(BigDecimal.valueOf(countNum)));
								inMap.put("tflavorName", flovrmapin.getKey());
								inMap.put("foodNumber", flovrmapin.getValue());
								returnList.add(inMap);
							}
						}
					}
					if (countNum != 0 && productList.getPrice() != null) {
						moneyMap = Maps.newHashMap();
						singleMoneyCount = singleMoneyCount.add(productList.getPrice().multiply(BigDecimal.valueOf(countNum)));
						moneyMap.put("moneyCount", singleMoneyCount.intValue());
						mapin.getValue().add(moneyMap);
						moneyCount = moneyCount.add(productList.getPrice().multiply(BigDecimal.valueOf(countNum)));
					}
				}
				if (!flag) {
					return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商家单品不存在", null);
				}
			}
		}
		Map monMap = Maps.newHashMap();
		monMap.put("moneyCount", moneyCount);
		returnList.add(monMap);
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询购物车成功", returnList);
	}
}
