package com.XZYHOrderingFood.back.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.XZYHOrderingFood.back.dao.QrCodeMapper;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.XZYHOrderingFood.back.dao.OrderTableMapper;
import com.XZYHOrderingFood.back.dao.TFlavorMapper;
import com.XZYHOrderingFood.back.myEnum.OrderTableEnum;
import com.XZYHOrderingFood.back.util.BasePage;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.Util;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class OrderTableService {

    @Autowired
    private OrderTableMapper orderTableMapper;

    @Autowired
    private ClassifyDateService classifyDateService;

    @Autowired
    private OrderDetailsService orderDetailsService;

    @Autowired
    private TFlavorMapper tFlavorMapper;

    @Autowired
    private QrCodeMapper qrCodeMapper;

    public Result getOrderTableLineChart(OrderTable orderTable, HttpServletRequest request) {

        if (StringUtils.isBlank(orderTable.getOrderTableLineChart())) {
            log.info("流水折线图未定义单位");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "折线图未定义单位", null);
        }
        // seession 中获取操作用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
        }
        try {
            // 获取昨天日期
            Date dateEnd = DateUtil.yesterdayEndYmd();
            if ("day".equals(orderTable.getOrderTableLineChart())) {
                // 获取七天前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -6,true);
                // 定义一周的每一天 初始值为0
                BigDecimal Monday = new BigDecimal("0");
                BigDecimal Tuesday = new BigDecimal("0");
                BigDecimal Wednesday = new BigDecimal("0");
                BigDecimal Thursday = new BigDecimal("0");
                BigDecimal Friday = new BigDecimal("0");
                BigDecimal Saturday = new BigDecimal("0");
                BigDecimal Sunday = new BigDecimal("0");
                // 初始化返回类
                HashMap<Integer, BigDecimal> weekMap = new HashMap<Integer, BigDecimal>();

                // 遍历商铺
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    // 对应每个星期数量加1
                    switch (DateUtil.todatIsWhichDay(orderTable1.getOrderTime())) {
                        case 1:
                            Sunday = Sunday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 2:
                            Monday = Monday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 3:
                            Tuesday = Tuesday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 4:
                            Wednesday = Wednesday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 5:
                            Thursday = Thursday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 6:
                            Friday = Friday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 7:
                            Saturday = Saturday.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
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
                    linkedHashMap = getDayMapBigDecimal(weekMap);
                } catch (Exception e) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "格式转换失败", null);
                }
                Map returnMap = getReturnMapBigDecimal(linkedHashMap, "日期", "单位收入总数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以天为单位查询成功", returnMap);
            } else if ("week".equals(orderTable.getOrderTableLineChart())) {
                /**
                 * 查询近四个周的新增用户
                 * 1. 首先获取当前时间
                 * 2. 将四个周的数据全部获取
                 * 3. 将不同的时间段的数据统计
                 */
                // 初始化计数
                BigDecimal fourWeekAgo = new BigDecimal("0");
                BigDecimal threeWeekAgo = new BigDecimal("0");
                BigDecimal twoWeekAgo = new BigDecimal("0");
                BigDecimal oneWeekAgo = new BigDecimal("0");
                // 初始化上周时间
                // 初始化上周起始时间  lastWeekStart dateEnd
                Date lastWeekEnd = DateUtil.daysAgoYmd(dateEnd, -6,false);
                // 获取两周前截至时间 twoLastWeekStart twoLastWeekEnd
                Date twoLastWeekEnd = DateUtil.daysAgoYmd(lastWeekEnd, -1,false);
                // 获取两周前起始时间
                Date twoLastWeekStart = DateUtil.daysAgoYmd(twoLastWeekEnd, -6,true);
                // 获取三周前截至时间
                Date threeLastWeekEnd = DateUtil.daysAgoYmd(twoLastWeekStart, -1,false);
                // 获取三周前起始时间
                Date threeLastWeekStart = DateUtil.daysAgoYmd(threeLastWeekEnd, -6,true);
                // 获取四周前截至时间
                Date fourLastWeekEnd = DateUtil.daysAgoYmd(threeLastWeekStart, -6,false);
                // 获取四周前起始时间
                Date fourLastWeekStart = DateUtil.daysAgoYmd(fourLastWeekEnd, -6,true);

                // 获取四周前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -27, true);
                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    switch (classifyDateService.judgeIsWhichMonth(orderTable1.getOrderTime(), dateEnd, lastWeekEnd, twoLastWeekEnd, twoLastWeekStart, threeLastWeekEnd, threeLastWeekStart, fourLastWeekEnd, fourLastWeekStart)) {
                        case 1:
                            oneWeekAgo = oneWeekAgo.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 2:
                            twoWeekAgo = twoWeekAgo.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 3:
                            threeWeekAgo = threeWeekAgo.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 4:
                            fourWeekAgo = fourWeekAgo.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                    }
                }
                // 初始化返回类
                LinkedHashMap<String, BigDecimal> weekMap = new LinkedHashMap<String, BigDecimal>();
                weekMap.put("近一周", oneWeekAgo);
                weekMap.put("近两周", twoWeekAgo);
                weekMap.put("近三周", threeWeekAgo);
                weekMap.put("近四周", fourWeekAgo);
                Map returnMap = getReturnMapBigDecimal(weekMap, "日期", "单位收入总数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以周为单位查询成功", returnMap);
            } else if ("year".equals(orderTable.getOrderTableLineChart())) {
                // 获取一年前的时间
                Date lastYear = DateUtil.reckonYears(dateEnd, -1);
                // 初始化12个月
                // 一月
                BigDecimal January = new BigDecimal("0");
                // 二月
                BigDecimal February = new BigDecimal("0");
                // 三月
                BigDecimal March = new BigDecimal("0");
                // 四月
                BigDecimal April = new BigDecimal("0");
                // 五月
                BigDecimal May = new BigDecimal("0");
                // 六月
                BigDecimal June = new BigDecimal("0");
                // 七月
                BigDecimal July = new BigDecimal("0");
                // 八月
                BigDecimal August = new BigDecimal("0");
                // 九月
                BigDecimal September = new BigDecimal("0");
                // 十月
                BigDecimal October = new BigDecimal("0");
                // 十一月
                BigDecimal November = new BigDecimal("0");
                // 十二月
                BigDecimal December = new BigDecimal("0");

                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(lastYear, dateEnd, currentUser)
                ) {
                    switch (DateUtil.getWhichMonthbyDate(orderTable1.getOrderTime())) {
                        case 1:
                            January = January.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 2:
                            February = February.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 3:
                            March = March.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 4:
                            April = April.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 5:
                            May = May.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 6:
                            June = June.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 7:
                            July = July.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 8:
                            August = August.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 9:
                            September = September.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 10:
                            October = October.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 11:
                            November = November.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));
                            continue;
                        case 12:
                            December = December.add(new BigDecimal(String.valueOf(orderTable1.getTotalMoney())));

                    }
                }
                HashMap<Integer, BigDecimal> weekMap = new HashMap<Integer, BigDecimal>();
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
                LinkedHashMap linkedHashMap = setMapYearAndMonthBigDecimal(weekMap);
                // 转换为echarts格式
                Map returnMap = getReturnMapInteger(linkedHashMap, "日期", "单位收入总数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以年为单位查询成功", returnMap);
            } else {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "请传输正确时段", null);
            }
        } catch (ParseException e) {
            log.info(e.getMessage());
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "流水折线图查询失败", null);
        }
    }

    public Result getAllBillMoneny (HttpServletRequest request) {
        // 获取商户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        // 初始化返回的map集合
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "获取商户信息失败", null);
        }
        try {
            BigDecimal moneyCount = orderTableMapper.countAllMoney(currentUser);
            map.put("moneyCount",moneyCount);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单总收入失败", null);
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询订单总收入成功", map);
    }

    public Result getSingleSumLineSelect (OrderTable orderTable, HttpServletRequest request) {
        /**
         * 获取订单单品数量
         * 1. 首先查询订单表，在特定时间段查询出全部数据
         * 2. 查询完成后获取订单id，将所有在订单详情表匹配的值数据查出来
         * 3. 进行按时间统计数
         */
        if (StringUtils.isBlank(orderTable.getOrderTableLineChart())) {
            log.info("单品销售数量折线图");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "折线图未定义单位", null);
        }
        // seession 中获取操作用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
        }
        try {
            // 初始化list用于存储订单id
            List<String> idList = new ArrayList<>();
            // 获取昨天日期
            Date dateEnd = DateUtil.yesterdayEndYmd();
            if ("day".equals(orderTable.getOrderTableLineChart())) {
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
                System.out.println(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateEnd.getTime()) + "---" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateStart.getTime()));
                // 遍历商铺
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    idList.add(orderTable1.getId());
                }
                // 查询订单详情表
                List<OrderDetails> detailsList = null;
                try {
                    detailsList = orderDetailsService.getOrderDetailsByListId(idList);
                } catch (Exception e) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情表失败", null);
                }
                // 遍历取到的数据
                for (OrderDetails details:detailsList
                ) {
                    if ((details.getFoodId()).equals(orderTable.getFoodId())) {
                        switch (DateUtil.todatIsWhichDay(details.getOrderTime())) {
                            case 1:
                                Sunday += details.getFoodNumber();
                                continue;
                            case 2:
                                Monday += details.getFoodNumber();
                                continue;
                            case 3:
                                Tuesday += details.getFoodNumber();
                                continue;
                            case 4:
                                Wednesday += details.getFoodNumber();
                                continue;
                            case 5:
                                Thursday += details.getFoodNumber();
                                continue;
                            case 6:
                                Friday += details.getFoodNumber();
                                continue;
                            case 7:
                                Saturday += details.getFoodNumber();
                        }
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
                    linkedHashMap = getDayMapInteger(weekMap);
                } catch (Exception e) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "格式转换失败", null);
                }
                Map returnMap = getReturnMapInteger(linkedHashMap, "日期", "单品销售总数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以天为单位查询成功", returnMap);
            } else if ("week".equals(orderTable.getOrderTableLineChart())) {
                /**
                 * 查询近四个周的新增用户
                 * 1. 首先获取当前时间
                 * 2. 将四个周的数据全部获取
                 * 3. 将不同的时间段的数据统计
                 */
                // 初始化计数
                Integer fourWeekAgo = 0;
                Integer threeWeekAgo = 0;
                Integer twoWeekAgo = 0;
                Integer oneWeekAgo = 0;
                // 初始化上周时间
                // 初始化上周起始时间  lastWeekStart dateEnd
                Date lastWeekEnd = DateUtil.daysAgoYmd(dateEnd, -6, false);
                // 获取两周前截至时间 twoLastWeekStart twoLastWeekEnd
                Date twoLastWeekEnd = DateUtil.daysAgoYmd(lastWeekEnd, -1, false);
                // 获取两周前起始时间
                Date twoLastWeekStart = DateUtil.daysAgoYmd(twoLastWeekEnd, -6,true);
                // 获取三周前截至时间
                Date threeLastWeekEnd = DateUtil.daysAgoYmd(twoLastWeekStart, -1,false);
                // 获取三周前起始时间
                Date threeLastWeekStart = DateUtil.daysAgoYmd(threeLastWeekEnd, -6,true);
                // 获取四周前截至时间
                Date fourLastWeekEnd = DateUtil.daysAgoYmd(threeLastWeekStart, -6,false);
                // 获取四周前起始时间
                Date fourLastWeekStart = DateUtil.daysAgoYmd(fourLastWeekEnd, -6,true);

                // 获取四周前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -27,  true);
                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    idList.add(orderTable1.getId());
                }
                // 查询订单详情表
                List<OrderDetails> detailsList = null;
                try {
                    detailsList = orderDetailsService.getOrderDetailsByListId(idList);
                } catch (Exception e) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情表失败", null);
                }
                // 遍历取到的数据
                for (OrderDetails details:detailsList
                ) {
                    if ((details.getFoodId()).equals(orderTable.getFoodId())) {
                        switch (classifyDateService.judgeIsWhichMonth(details.getOrderTime(), dateEnd, lastWeekEnd, twoLastWeekEnd, twoLastWeekStart, threeLastWeekEnd, threeLastWeekStart, fourLastWeekEnd, fourLastWeekStart)) {
                            case 1:
                                oneWeekAgo += details.getFoodNumber();
                                continue;
                            case 2:
                                twoWeekAgo += details.getFoodNumber();
                                continue;
                            case 3:
                                threeWeekAgo += details.getFoodNumber();
                                continue;
                            case 4:
                                fourWeekAgo += details.getFoodNumber();
                        }
                    }
                }
                // 初始化返回类
                LinkedHashMap<String, Integer> weekMap = new LinkedHashMap<String, Integer>();
                weekMap.put("近一周", oneWeekAgo);
                weekMap.put("近两周", twoWeekAgo);
                weekMap.put("近三周", threeWeekAgo);
                weekMap.put("近四周", fourWeekAgo);
                Map returnMap = getReturnMapInteger(weekMap, "日期", "单品销售总数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以周为单位查询成功", returnMap);
            } else if ("year".equals(orderTable.getOrderTableLineChart())) {
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
                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(lastYear, dateEnd, currentUser)
                ) {
                    idList.add(orderTable1.getId());
                }
                // 查询订单详情表
                List<OrderDetails> detailsList = null;
                try {
                    detailsList = orderDetailsService.getOrderDetailsByListId(idList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情表失败", null);
                }
                // 遍历取到的数据
                for (OrderDetails details:detailsList
                     ) {
                    if (details.getFoodId() != null) {
                        if ((details.getFoodId()).equals(orderTable.getFoodId())) {
                            switch (DateUtil.getWhichMonthbyDate(details.getOrderTime())) {
                                case 1:
                                    January += details.getFoodNumber();
                                    continue;
                                case 2:
                                    February += details.getFoodNumber();
                                    continue;
                                case 3:
                                    March += details.getFoodNumber();
                                    continue;
                                case 4:
                                    April += details.getFoodNumber();
                                    continue;
                                case 5:
                                    May += details.getFoodNumber();
                                    continue;
                                case 6:
                                    June += details.getFoodNumber();
                                    continue;
                                case 7:
                                    July += details.getFoodNumber();
                                    continue;
                                case 8:
                                    August += details.getFoodNumber();
                                    continue;
                                case 9:
                                    September += details.getFoodNumber();
                                    continue;
                                case 10:
                                    October += details.getFoodNumber();
                                    continue;
                                case 11:
                                    November += details.getFoodNumber();
                                    continue;
                                case 12:
                                    December += details.getFoodNumber();
                            }
                        }
                    }
                }
                HashMap<Integer, Integer> weekMap = new HashMap<Integer, Integer>();
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
                LinkedHashMap linkedHashMap = setMapYearAndMonthInteger(weekMap);
                // 转换为echarts格式
                Map returnMap = getReturnMapInteger(linkedHashMap, "日期", "单品销售总数");
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
     * @description: 客户数量折线图查询
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    public Result getMealsNumberLine (OrderTable orderTable, HttpServletRequest request) {
        if (StringUtils.isBlank(orderTable.getOrderTableLineChart())) {
            log.info("流水折线图未定义单位");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "折线图未定义单位", null);
        }
        // seession 中获取操作用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
        }
        try {
            // 获取昨天日期
            Date dateEnd = DateUtil.yesterdayEndYmd();
            if ("day".equals(orderTable.getOrderTableLineChart())) {
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
                System.out.println(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateEnd.getTime()) + "---" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateStart.getTime()));
                // 遍历商铺
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    if (orderTable1.getMealsNumber() != null) {
                        // 对应每个星期数量加1
                        switch (DateUtil.todatIsWhichDay(orderTable1.getOrderTime())) {
                            case 1:
                                Sunday += orderTable1.getMealsNumber();
                                continue;
                            case 2:
                                Monday += orderTable1.getMealsNumber();
                                continue;
                            case 3:
                                Tuesday += orderTable1.getMealsNumber();
                                continue;
                            case 4:
                                Wednesday += orderTable1.getMealsNumber();
                                continue;
                            case 5:
                                Thursday += orderTable1.getMealsNumber();
                                continue;
                            case 6:
                                Friday += orderTable1.getMealsNumber();
                                continue;
                            case 7:
                                Saturday += orderTable1.getMealsNumber();
                        }
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
                    linkedHashMap = getDayMapInteger(weekMap);
                } catch (Exception e) {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "格式转换失败", null);
                }
                Map returnMap = getReturnMapInteger(linkedHashMap, "日期", "单位新增商户数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以天为单位查询成功", returnMap);
            } else if ("week".equals(orderTable.getOrderTableLineChart())) {
                /**
                 * 查询近四个周的新增用户
                 * 1. 首先获取当前时间
                 * 2. 将四个周的数据全部获取
                 * 3. 将不同的时间段的数据统计
                 */
                // 初始化计数
                Integer fourWeekAgo = 0;
                Integer threeWeekAgo = 0;
                Integer twoWeekAgo = 0;
                Integer oneWeekAgo = 0;
                // 初始化上周时间
                // 初始化上周起始时间  lastWeekStart dateEnd
                Date lastWeekEnd = DateUtil.daysAgoYmd(dateEnd, -6,false);
                // 获取两周前截至时间 twoLastWeekStart twoLastWeekEnd
                Date twoLastWeekEnd = DateUtil.daysAgoYmd(lastWeekEnd, -1,false);
                // 获取两周前起始时间
                Date twoLastWeekStart = DateUtil.daysAgoYmd(twoLastWeekEnd, -6,true);
                // 获取三周前截至时间
                Date threeLastWeekEnd = DateUtil.daysAgoYmd(twoLastWeekStart, -1,false);
                // 获取三周前起始时间
                Date threeLastWeekStart = DateUtil.daysAgoYmd(threeLastWeekEnd, -6,true);
                // 获取四周前截至时间
                Date fourLastWeekEnd = DateUtil.daysAgoYmd(threeLastWeekStart, -6,false);
                // 获取四周前起始时间
                Date fourLastWeekStart = DateUtil.daysAgoYmd(fourLastWeekEnd, -6,true);

                // 获取四周前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -27, true);
                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(dateStart, dateEnd, currentUser)
                ) {
                    if (orderTable1.getMealsNumber() != null) {
                        switch (classifyDateService.judgeIsWhichMonth(orderTable1.getOrderTime(), dateEnd, lastWeekEnd, twoLastWeekEnd, twoLastWeekStart, threeLastWeekEnd, threeLastWeekStart, fourLastWeekEnd, fourLastWeekStart)) {
                            case 1:
                                oneWeekAgo += orderTable1.getMealsNumber();
                                continue;
                            case 2:
                                twoWeekAgo += orderTable1.getMealsNumber();
                                continue;
                            case 3:
                                threeWeekAgo += orderTable1.getMealsNumber();
                                continue;
                            case 4:
                                fourWeekAgo += orderTable1.getMealsNumber();
                        }
                    }
                }
                // 初始化返回类
                LinkedHashMap<String, Integer> weekMap = new LinkedHashMap<String, Integer>();
                weekMap.put("近一周", oneWeekAgo);
                weekMap.put("近两周", twoWeekAgo);
                weekMap.put("近三周", threeWeekAgo);
                weekMap.put("近四周", fourWeekAgo);
                Map returnMap = getReturnMapInteger(weekMap, "日期", "单位新增商户数");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以周为单位查询成功", returnMap);
            } else if ("year".equals(orderTable.getOrderTableLineChart())) {
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
                // 遍历数据
                for (OrderTable orderTable1 : getOrderTable(lastYear, dateEnd, currentUser)
                ) {
                    // 判断参数是否不为空
                    if (orderTable1.getMealsNumber() != null) {
                        switch (DateUtil.getWhichMonthbyDate(orderTable1.getOrderTime())) {
                            case 1:
                                January += orderTable1.getMealsNumber();
                                continue;
                            case 2:
                                February += orderTable1.getMealsNumber();
                                continue;
                            case 3:
                                March += orderTable1.getMealsNumber();
                                continue;
                            case 4:
                                April += orderTable1.getMealsNumber();
                                continue;
                            case 5:
                                May += orderTable1.getMealsNumber();
                                continue;
                            case 6:
                                June += orderTable1.getMealsNumber();
                                continue;
                            case 7:
                                July += orderTable1.getMealsNumber();
                                continue;
                            case 8:
                                August += orderTable1.getMealsNumber();
                                continue;
                            case 9:
                                September += orderTable1.getMealsNumber();
                                continue;
                            case 10:
                                October += orderTable1.getMealsNumber();
                                continue;
                            case 11:
                                November += orderTable1.getMealsNumber();
                                continue;
                            case 12:
                                December += orderTable1.getMealsNumber();
                        }
                    }
                }
                HashMap<Integer, Integer> weekMap = new HashMap<Integer, Integer>();
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
                /**
                 * 1. 首先判断当前是几月
                 * 2. 计算当前月份到年初map排序添加年份
                 */
                // 添加年月
                LinkedHashMap linkedHashMap = setMapYearAndMonthInteger(weekMap);
                // 转换为echarts格式
                Map returnMap = getReturnMapInteger(linkedHashMap, "日期", "单位新增商户数");
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
     * @description: 模糊查询订单管理-订单信息
     * @author: zpy
     * @date: 2019-07-26
     * @params: 
     * @return: 
     */
    public Result getOrderManageInfo (OrderTable orderTable, HttpServletRequest request) {
        if (orderTable.getPageNo()!=null && orderTable.getPageSize()!=null ) {

            PageHelper.startPage(orderTable.getPageNo(), orderTable.getPageSize());
        } else {return Result.resultData(PublicDictUtil.ERROR_VALUE, "分页未获取到数据", null);}
        // seession 中获取操作用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
        }
        // mybatis 查询用的map
        Map<String, Object> map = new HashMap<String, Object>();
        // 添加商户id
        map.put("shopId", currentUser.getId());
        // 添加是否是商户
        map.put("isShops", currentUser.getIsShops());
        // 判断订单金额范围
        if (orderTable.getOrderMoneyInterval() != null) {
            if (orderTable.getOrderMoneyInterval() == 1) {
                map.put("moneyBegin", null);
                map.put("moneyEnd", 100);
            } else if (orderTable.getOrderMoneyInterval() == 2) {
                map.put("moneyBegin", 101);
                map.put("moneyEnd", 300);
            } else if (orderTable.getOrderMoneyInterval() == 3) {
                map.put("moneyBegin", 301);
                map.put("moneyEnd", 500);
            } else if (orderTable.getOrderMoneyInterval() == 4) {
                map.put("moneyBegin", 501);
                map.put("moneyEnd", 1000);
            } else if (orderTable.getOrderMoneyInterval() == 5) {
                map.put("moneyBegin", 1001);
                map.put("moneyEnd", 2000);
            } else if (orderTable.getOrderMoneyInterval() == 6) {
                map.put("moneyBegin", 2000);
                map.put("moneyEnd", null);
            }
        }
        // 把日期放入
        if (orderTable.getOrderTime() != null) {
            try {
                map.put("timeBegin", DateUtil.daysAgoYmd(orderTable.getOrderTime(),0,true));
                map.put("timeEnd", DateUtil.daysAgoYmd(orderTable.getOrderTime(),0,false));
            } catch (Exception e) {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "请传输正确时段", null);
            }
        }
        // 把实体类放入
        map.put("orderTable",orderTable);
        // 初始化返回类
        List<OrderTable> list = null;
        try {
//            这里查询的是订单，传入的主要参数是商户ID
            list = orderTableMapper.getOrderTableByExample(map);
            for (OrderTable table : list) {
                //去详情里面查询如果查询的参数不存在直接返回为空，是否存在,返回前端值为 null的属性不会返回
               if(orderDetailsService.selectOrderDetailsByOrderCode(currentUser, orderTable)!=null) {
                   list.add(table);
               }
            }


        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询数据库失败", null);
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询订单信息成功", new BasePage<OrderTable>(list));
    }

    /**
     * @description: 通过订单编号和商户id修改订单状态
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    public Result updateOrderTableById (OrderTable orderTable, HttpServletRequest request) {
        // 从session中获取用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
        }
        // ordertable 需要携带订单编号和修改状态的字段
        if (StringUtils.isBlank(orderTable.getOrderCode())) {
            log.info("订单编号不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单编号不能为空", null);
        }
        // 0-已取消 1-待支付 2-已支付
        if (StringUtils.isBlank(orderTable.getOrderStatus().toString())) {
            log.info("订单状态不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单状态不能为空", null);
        }
        if (StringUtils.isBlank(orderTable.getTableNumber())) {
            log.info("桌号不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "桌号不能为空", null);
        }
        // 初始化查询条件
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", currentUser.getId());
        map.put("isShops", currentUser.getIsShops());
        map.put("orderCode", orderTable.getOrderCode());
        map.put("orderStatus", orderTable.getOrderStatus());
        Integer orderTables = null;
        try {
            orderTables = orderTableMapper.updateBytOrderCodeAndShopId(map);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改状态失败", null);
        }
        if (orderTables == 1) {
            // 通过商户id 和桌号锁定二维码
            List<QrCode> qrCodes = qrCodeMapper.selectByShopId(currentUser.getId());
            boolean flag = false;
            Integer count = null;
            for (QrCode qrcode:qrCodes
            ) {
                if (qrcode.getTableNumber().equals(orderTable.getTableNumber())) {
                    qrcode.setTableStatus(2);
                    count = qrCodeMapper.updateByPrimaryKeySelective(qrcode);
                }
            }
            if (!flag) {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商家没有对应桌号", null);
            }
            if (count < 1) {
                throw new CustomException(PublicDictUtil.ERROR_VALUE, "修改二维码状态失败");
            }
            return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "修改状态成功", null);
        }
        return Result.resultData(PublicDictUtil.ERROR_VALUE, "修改状态失败", orderTables);
    }

    /**
     * @description: 通过传入订单编码查询订单id
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    public OrderTable getOrderTableCode (String orderCode, String id) throws Exception {
        // 初始化查询条件
        Map map = Maps.newHashMap();
        map.put("orderCode", orderCode);
        map.put("id", id);
        // 查询结果
        OrderTable orderTable = null;
        try {
            orderTable = orderTableMapper.selectOrderTableIdByCode(map);
        } catch (Exception e) {
            throw new Exception("通过订单id查询订单编号失败");
        }
        return orderTable;
    }

    /**
     * @description: 向订单表插入一条订单数据
     * @author: zpy
     * @date: 2019-07-29
     * @params:
     * @return:
     */
    public String insertOrderTable (OrderTable orderTable) throws Exception {
        // 生成订单id
        String OrderCodeId = Util.makeOrderNo(orderTable.getOpenId().substring(0, 3));
        // 设置订单一系列值
        orderTable.setOrderTime(new Date());
        orderTable.setId(Sid.nextShort());
        orderTable.setOrderStatus(OrderTableEnum.PENDING_ORDER.getCode());
        orderTable.setOrderCode(OrderCodeId);
        orderTable.setTableNumber(orderTable.getTableNumber());
        orderTable.setIsDelete(OrderTableEnum.NO_DELETE.getCode());
        orderTable.setOpenId(orderTable.getOpenId());
        orderTable.setMealsNumber(orderTable.getMealsNumber());
        orderTable.setRemark(orderTable.getRemark());
        // 插入数据
        Integer count = null;
        try {
            count = orderTableMapper.insertSelective(orderTable);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("插入订单表数据失败");
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "插入订单表数据失败");
        }
        if (count < 1) {
            throw new CustomException(PublicDictUtil.ERROR_VALUE, "插入订单表数据失败");
        }
        return OrderCodeId;
    }

    /**
     * @description: 从数据库查询数据
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    public List<OrderTable> getOrderTable(Date startdate, Date enndate, TUser tUser) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("dateStart", startdate);
        map.put("dateEnd", enndate);
        map.put("id", tUser.getId());
        map.put("isShops", tUser.getIsShops());
        return orderTableMapper.getOrderTableByTime(map);
    }

    /**
     * @description: 通过传入map 生成指定map Integer
     * @author: zpy
     * @date: 2019-07-31
     * @params:
     * @return:
     */
    public Map getReturnMapInteger (LinkedHashMap<String, Integer> inMap, String left, String mid) {
        Map dataMap = null;
        List rowList = new ArrayList();
        for (Map.Entry map:inMap.entrySet()
        ) {
            dataMap = Maps.newHashMap();
            dataMap.put(left, map.getKey());
            dataMap.put(mid, map.getValue());
            rowList.add(dataMap);
        }
        // 初始化返回map
        Map returnMap = new HashMap();
        returnMap.put("rows", rowList);
        List tempList = new ArrayList();
        tempList.add(left);
        tempList.add(mid);
        returnMap.put("columns", tempList);
        return returnMap;
    }

    /**
     * @description: 通过传入map 生成指定map BigDecimal
     * @author: zpy
     * @date: 2019-07-31
     * @params:
     * @return:
     */
    public Map getReturnMapBigDecimal (Map<String, BigDecimal> inMap, String left, String mid) {
        Map dataMap = null;
        List rowList = new ArrayList();
        for (Map.Entry map:inMap.entrySet()
        ) {
            dataMap = Maps.newHashMap();
            dataMap.put(left, map.getKey());
            dataMap.put(mid, map.getValue());
            rowList.add(dataMap);
        }
        // 初始化返回map
        Map returnMap = new HashMap();
        returnMap.put("rows", rowList);
        List tempList = new ArrayList();
        tempList.add(left);
        tempList.add(mid);
        returnMap.put("columns", tempList);
        return returnMap;
    }

    /**
     * @description: 把map值 加上年月
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    public LinkedHashMap setMapYearAndMonthInteger (Map<Integer, Integer> map) {
        /**
         * 以月份为单位添加map
         * 1. 首先先判断当前月份
         * 2. 用12减去当前月份
         */
        // 获取当前时间的月份
        Integer month = DateUtil.getWhichMonthbyDate(new Date());
        // 获取当前年份
        Integer year = DateUtil.getWhichYearbyDate(new Date());
        // 初始化当年年份集合
        LinkedHashMap<String, Integer> linkedHashMapNow = new LinkedHashMap<String, Integer>();
        // 初始化去年年份集合
        LinkedHashMap<String, Integer> linkedHashMapLast = new LinkedHashMap<String, Integer>();
        // 遍历月份的数据
        for (HashMap.Entry<Integer, Integer> weekmap:map.entrySet()
        ) {
            if (weekmap.getKey() <= month) {
                linkedHashMapNow.put(year+"年"+weekmap.getKey()+"月", weekmap.getValue());
            } else {
                linkedHashMapLast.put((year-1)+"年"+weekmap.getKey()+"月", weekmap.getValue());
            }
        }
        for (HashMap.Entry<String, Integer> nowMap:linkedHashMapNow.entrySet()
        ) {
            linkedHashMapLast.put(nowMap.getKey(), nowMap.getValue());
        }
        return linkedHashMapLast;
    }

    /**
     * @description: 把map值 加上年月
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    public LinkedHashMap setMapYearAndMonthBigDecimal (Map<Integer, BigDecimal> map) {
        /**
         * 以月份为单位添加map
         * 1. 首先先判断当前月份
         * 2. 用12减去当前月份
         */
        // 获取当前时间的月份
        Integer month = DateUtil.getWhichMonthbyDate(new Date());
        // 获取当前年份
        Integer year = DateUtil.getWhichYearbyDate(new Date());
        // 初始化当年年份集合
        LinkedHashMap<String, BigDecimal> linkedHashMapNow = new LinkedHashMap<String, BigDecimal>();
        // 初始化去年年份集合
        LinkedHashMap<String, BigDecimal> linkedHashMapLast = new LinkedHashMap<String, BigDecimal>();
        // 遍历月份的数据
        for (HashMap.Entry<Integer, BigDecimal> weekmap:map.entrySet()
        ) {
            if (weekmap.getKey() <= month) {
                linkedHashMapNow.put(year+"年"+weekmap.getKey()+"月", weekmap.getValue());
            } else {
                linkedHashMapLast.put((year-1)+"年"+weekmap.getKey()+"月", weekmap.getValue());
            }
        }
        for (HashMap.Entry<String, BigDecimal> nowMap:linkedHashMapNow.entrySet()
        ) {
            linkedHashMapLast.put(nowMap.getKey(), nowMap.getValue());
        }
        return linkedHashMapLast;
    }

    /**
     * @description: 以日为单位 对数据封装
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    public LinkedHashMap getDayMapInteger (HashMap<Integer,Integer> hashMap) throws Exception {
        // 获取当前时间
        Integer week = DateUtil.getWeek(new Date());
        // 初始化存当前周的map
        LinkedHashMap<String,Integer> weekMapNow = new LinkedHashMap();
        // 初始化存上一周的map
        LinkedHashMap<String,Integer> weekMapLast = new LinkedHashMap();
        try {
        for (Map.Entry<Integer,Integer> weekm:hashMap.entrySet()
        ) {
            if (weekm.getKey() <= week) {
                weekMapNow.put(DateUtil.getDateByWeek(new Date(), weekm.getKey()), weekm.getValue());
            } else {
                weekMapLast.put(DateUtil.getDateByWeek(new Date(), weekm.getKey()), weekm.getValue());
            }
        }
        } catch (Exception e) {
            throw new Exception("格式转换异常");
        }
        for (Map.Entry<String, Integer> nowMap:weekMapNow.entrySet()
        ) {
            weekMapLast.put(nowMap.getKey(), nowMap.getValue());
        }
        return weekMapLast;
    }

    /**
     * @description: 以日为单位 对数据封装
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    public LinkedHashMap getDayMapBigDecimal (HashMap<Integer,BigDecimal> hashMap) throws Exception {
        // 获取当前时间
        Integer week = DateUtil.getWeek(new Date());
        // 初始化存当前周的map
        LinkedHashMap<String,BigDecimal> weekMapNow = new LinkedHashMap();
        // 初始化存上一周的map
        LinkedHashMap<String,BigDecimal> weekMapLast = new LinkedHashMap();
        try {
            for (Map.Entry<Integer,BigDecimal> weekm:hashMap.entrySet()
            ) {
                if (weekm.getKey() <= week) {
                    weekMapNow.put(DateUtil.getDateByWeek(new Date(), weekm.getKey()), weekm.getValue());
                } else {
                    weekMapLast.put(DateUtil.getDateByWeek(new Date(), weekm.getKey()), weekm.getValue());
                }
            }
        } catch (Exception e) {
            throw new Exception("格式转换异常");
        }
        for (Map.Entry<String, BigDecimal> nowMap:weekMapNow.entrySet()
        ) {
            weekMapLast.put(nowMap.getKey(), nowMap.getValue());
        }
        return weekMapLast;
    }

    /**
     * @description: 通过订单id查询订单详情
     * @author: zpy
     * @date: 2019-08-02
     */
    public Result getOrderDetailsInfo(OrderTable orderTable, HttpServletRequest request) {
        // session中获取用户信息
        TUser currentUser = GetCurrentUser.getCurrentUser(request);
        if (currentUser == null) {
            return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "从缓存中获取用户信息失败", null);
        }
        if (StringUtils.isBlank(orderTable.getOrderCode())) {
            log.info("订单编号不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单编号不能为空", null);
        }
        List<OrderDetails> orderDetails = null;
        try {
            //查询出来多个详情
            orderDetails = orderDetailsService.selectOrderDetailsByOrderCode(currentUser, orderTable);
            if (orderDetails == null&& 0 < orderDetails.size()) {
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "订单详情查询失败", null);
            }
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询订单详情失败", null);
        }
//        List<String> strings = new ArrayList<>();
////        遍历订单详情准备进行查询口味
//        for (OrderDetails orderDet:orderDetails
//             ) {
//            strings.add(orderDet.getId());
////        }

        // 根据订单餐品查询口味
        /**
         * 1. 首先通过菜单idlist 查询出所有口味
         * 2. 通过菜单的口味id 匹配所有口味
         */
        List<String> detailIdList = null;
        // 遍历单品数据
        for (OrderDetails orderDe: orderDetails
             ) {
//            拿到商户的信息进行存储
            detailIdList.add(orderDe.getTuserId());

        }
//         初始化查询接口

        if (detailIdList == null&& 0 < detailIdList.size()) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "订单详情表商户id为空", null);
        }
        List<TFlavor> tFlavors = null;
        try {
//            根据商户的ID进行口味的查询
            tFlavors = tFlavorMapper.selectTFavorListById(detailIdList);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询口味集合失败", null);
        }
        // 将订单菜单单品遍历
        for (OrderDetails orderDetail:orderDetails
             ) {
//            对取出的商家的所有口味进行遍历
            for (TFlavor tFlavor:tFlavors
                 ) {
                // 如果订单详情里的口味ID不为空，并且和商家的口味ID匹配，则进行赋值操作。
                if (StringUtils.isNotBlank(orderDetail.getTflavorId())&&orderDetail.getTflavorId().equals(tFlavor.getId())) {
                    orderDetail.setTFlavor(tFlavor);
                }else{
                    TFlavor tf= new TFlavor();
                    tf.setName("正常口味");
                    orderDetail.setTFlavor(tf);
                }
            }
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "订单详情查询成功", orderDetails);
    }

}
