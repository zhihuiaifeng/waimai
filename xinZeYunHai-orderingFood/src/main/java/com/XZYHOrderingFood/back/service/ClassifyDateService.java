package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @description: 分类时间
 * @author: zpy
 * @create: 2019-07-23 11:52
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassifyDateService<T> {


    /**
     * @description: 对传入时间进行是哪周的判断
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    public int judgeIsWhichMonth(Date date, Date s1, Date e1, Date s2, Date e2, Date s3, Date e3, Date s4, Date e4){
        /**
         * 对传入时间进行判断，上周返回1，两周前返回2，三周前返回3，四周前返回4
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        System.out.println("ddd");
        System.out.println(simpleDateFormat.format(date));
        System.out.println(simpleDateFormat.format(s1));
        System.out.println(simpleDateFormat.format(e1));
        System.out.println(simpleDateFormat.format(e2));
        System.out.println(simpleDateFormat.format(e3));
        System.out.println("ddd");
        if ((date.compareTo(s1))<0) {
            if((date.compareTo(e1))>=0) {
                return 1;
            }else {
                if ((date.compareTo(e2))>=0) {
                    return 2;
                } else {
                    if (date.compareTo(e3)>=0) {
                        return 3;
                    } else {
                        if(date.compareTo(e4)>=0) {
                            return 4;
                        }
                    }
                }
            }
        }
        return 0;
    }


    public HashMap<String,Integer> getYearMap(List<OrderTable> orderTableList){
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


        // 遍历数据
        for (OrderTable orderTable1 : orderTableList
        ) {
            switch (DateUtil.getWhichMonthbyDate(orderTable1.getOrderTime())) {
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
        HashMap<String, Integer> weekMap = new HashMap<String, Integer>();
        weekMap.put("January", January);
        weekMap.put("February", February);
        weekMap.put("March", March);
        weekMap.put("April", April);
        weekMap.put("May", May);
        weekMap.put("June", June);
        weekMap.put("July", July);
        weekMap.put("August", August);
        weekMap.put("September", September);
        weekMap.put("October", October);
        weekMap.put("November", November);
        weekMap.put("December", December);
        return weekMap;
    }


}
