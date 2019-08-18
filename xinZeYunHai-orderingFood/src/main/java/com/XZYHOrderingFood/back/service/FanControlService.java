package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.dao.FanControlMapper;
import com.XZYHOrderingFood.back.pojo.FanControl;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.BasePage;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FanControlService {

    @Autowired
    private FanControlMapper fanControlMapper;

    @Autowired
    private ClassifyDateService classifyDateService;

    @Autowired
    private OrderTableService orderTableService;

    public Result getNewFanLineChart (FanControl fanControl) {
        if (StringUtils.isBlank(fanControl.getNewFanLineChart())) {
            log.info("新增粉丝折线图未定义单位");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "新增粉丝折线图未定义单位", null);
        }
        try {
            // 获取昨天日期
            Date dateEnd = DateUtil.yesterdayEndYmd();
            if ("day".equals(fanControl.getNewFanLineChart())) {
                // 定义一周的每一天 初始值为0
                Integer Monday = 0;
                Integer Tuesday = 0;
                Integer Wednesday = 0;
                Integer Thursday = 0;
                Integer Friday = 0;
                Integer Saturday = 0;
                Integer Sunday = 0;
                // 初始化返回类
                HashMap<Integer,Integer> weekMap = new HashMap<Integer, Integer>();
                // 获取七天前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -6, true);
                System.out.println(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateEnd.getTime()) + "---" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(dateStart.getTime()));
                // 遍历商铺
                for (FanControl fanControl1 : getFanController(dateStart,dateEnd)
                ) {
                    // 对应每个星期数量加1
                    switch (DateUtil.todatIsWhichDay(fanControl1.getCreateTime())) {
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
                Map returnMap = orderTableService.getReturnMapInteger(linkedHashMap, "日期", "新增粉丝数量");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以天为单位查询成功", returnMap);
            } else if ("week".equals(fanControl.getNewFanLineChart())) {
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
                Date lastWeekEnd = DateUtil.daysAgoYmd(dateEnd,-6,false);
                // 获取两周前截至时间 twoLastWeekStart twoLastWeekEnd
                Date twoLastWeekEnd = DateUtil.daysAgoYmd(lastWeekEnd,-1,false);
                // 获取两周前起始时间
                Date twoLastWeekStart = DateUtil.daysAgoYmd(twoLastWeekEnd,-6,true);
                // 获取三周前截至时间
                Date threeLastWeekEnd = DateUtil.daysAgoYmd(twoLastWeekStart,-1,false);
                // 获取三周前起始时间
                Date threeLastWeekStart = DateUtil.daysAgoYmd(threeLastWeekEnd,-6,true);
                // 获取四周前截至时间
                Date fourLastWeekEnd = DateUtil.daysAgoYmd(threeLastWeekStart,-6,false);
                // 获取四周前起始时间
                Date fourLastWeekStart = DateUtil.daysAgoYmd(fourLastWeekEnd,-6,true);

                // 获取四周前日期
                Date dateStart = DateUtil.daysAgoYmd(dateEnd, -27, true);
                // 遍历数据
                for (FanControl fanControl1:getFanController(dateStart,dateEnd)
                ) {
                    switch (classifyDateService.judgeIsWhichMonth(fanControl1.getCreateTime(),dateEnd,lastWeekEnd,twoLastWeekEnd,twoLastWeekStart,threeLastWeekEnd,threeLastWeekStart,fourLastWeekEnd,fourLastWeekStart)) {
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
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以周为单位查询成功", weekMap);
            } else if ("year".equals(fanControl.getNewFanLineChart())) {
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
                Date lastYear = DateUtil.reckonYears(dateEnd,-1);
                // 遍历数据
                for (FanControl fanControl1:getFanController(lastYear, dateEnd)
                ) {
                    switch (DateUtil.getWhichMonthbyDate(fanControl1.getCreateTime())) {
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
                HashMap<Integer,Integer> weekMap = new HashMap<Integer, Integer>();
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
                Map returnMap = orderTableService.getReturnMapInteger(linkedHashMap, "日期", "新增粉丝数量");
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "以年为单位查询成功", returnMap);
            } else {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "请传输正确时段", null);
            }
        } catch (ParseException e) {
            log.info(e.getMessage());
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "新增粉丝折线图查询失败", null);
        }
    }


    /**
     * @description: 查询数据  从微信
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    public List<FanControl> getFanController(Date startdate, Date enndate){
        // 初始化查询条件
        FanControl fanControl = new FanControl();
        fanControl.setStartTime(startdate);
        fanControl.setEndTime(enndate);
        // 传入起始和结束时间，从微信查询数据
        return fanControlMapper.selectFanFuzzyQuery(fanControl);
    }

    /**
     * @description: 模糊查询粉丝 
     * @author: zpy
     * @date: 2019-07-31
     * @params: 
     * @return: 
     */
    public Result selectFanFuzzyQuery(FanControl fanControl) {
        if (fanControl.getPageNo()!=null && fanControl.getPageSize()!=null ) {
            PageHelper.startPage(fanControl.getPageNo(), fanControl.getPageSize());
        } else {return Result.resultData(PublicDictUtil.ERROR_VALUE, "分页未获取到数据", null);}
        // 如果时间的条件不为空，增加判断条件
        if (fanControl.getFocusTime() != null) {
            try {
                fanControl.setStartTime(DateUtil.getEndOrStartDate(fanControl.getFocusTime(), false));
                fanControl.setEndTime(DateUtil.getEndOrStartDate(fanControl.getFocusTime(), true));
            } catch (Exception e) {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "时间转换失败", null);
            }
        }
        // 初始化查询结果
        List<FanControl> fanControls = null;
        try {
            fanControls = fanControlMapper.selectFanFuzzyQuery(fanControl);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "模糊查询失败", null);
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "模糊查询粉丝成功", new BasePage<FanControl>(fanControls));
    }
}
