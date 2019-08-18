package com.XZYHOrderingFood.back.service;


import com.XZYHOrderingFood.back.dao.ReserveInfomationMapper;
import com.XZYHOrderingFood.back.pojo.ReserveInfomation;
import com.XZYHOrderingFood.back.sms.SendSms;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.RandomUtils;
import com.XZYHOrderingFood.back.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.geom.AreaOp;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ReserveInfomationService {

    @Autowired
    private ReserveInfomationMapper reserveInfomationMapper;

    @Autowired
    private TUserService tUserService;

    public Result addReserveInfo (ReserveInfomation reserveInfomation) {
        if (StringUtils.isBlank(reserveInfomation.getNeedInfo())) {
            log.info("需求信息为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "需求信息为空", null);
        }
        if (StringUtils.isBlank(reserveInfomation.getCompanyName())) {
            log.info("公司名称为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "公司名称为空", null);
        }
        if (StringUtils.isBlank(reserveInfomation.getUsername())) {
            log.info("姓名为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "姓名为空", null);
        }
        if (StringUtils.isBlank(reserveInfomation.getPhone())) {
            log.info("手机号为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号为空", null);
        }
        if (StringUtils.isBlank(reserveInfomation.getVerificationCode())) {
            log.info("验证码不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号为空", null);
        }
        // 校验手机验证码
        /**
         *  通过手机号更新全部数据
         *  1. 获取手机号
         *  2. 通过手机号查询出这条数据
         *  3. 更新该数据信息
         */
        // 需求信息赋值
        if ("1".equals(reserveInfomation.getNeedInfo())) {
            reserveInfomation.setNeedInfo("网站定制开发");
        } else if ("2".equals(reserveInfomation.getNeedInfo())) {
            reserveInfomation.setNeedInfo("公众号定制开发");
        } else if ("3".equals(reserveInfomation.getNeedInfo())) {
            reserveInfomation.setNeedInfo("VIS视觉设计");
        } else if ("4".equals(reserveInfomation.getNeedInfo())) {
            reserveInfomation.setNeedInfo("入驻点餐宝平台");
        }
        ReserveInfomation reserveInfo = null;
        try {
            reserveInfo = reserveInfomationMapper.selectByPhoneNumber(reserveInfomation);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询预留信息失败", null);
        }
        // 更新预留信息
        if (reserveInfo != null) {
            if (reserveInfo.getVerificationCode() != null) {
                if (reserveInfo.getVerificationCode().equals(reserveInfomation.getVerificationCode())) {
                    // 设置创建时间
                    reserveInfomation.setModifyTime(new Date());
                    // 设置联络状态
                    reserveInfomation.setIsContact(0);
                    // 设置主键
                    reserveInfomation.setId(reserveInfo.getId());
                    // 初始化返回类
                    Integer count = null;
                    try {
                        count = reserveInfomationMapper.updateByPrimaryKeySelective(reserveInfomation);
                    } catch (Exception e) {
                        return Result.resultData(PublicDictUtil.ERROR_VALUE, "更新状态失败", null);
                    }
                    if (count > 0) {
                        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "预留信息成功", null);
                    } else {
                        return Result.resultData(PublicDictUtil.ERROR_VALUE, "更新状态失败", null);
                    }
                } else {
                    return Result.resultData(PublicDictUtil.ERROR_VALUE, "验证码输入有误", null);
                }
            } else {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "验证码不存在", null);
            }
        } else {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "预留信息无数据", null);
        }
    }

    /**
     * @description: 发送手机验证码接口
     * @author: zpy
     * @date: 2019-07-31
     * @params:
     * @return:
     */
    public Result sendPhoneCode(ReserveInfomation reserveInfomation) {
        if (StringUtils.isBlank(reserveInfomation.getPhone())) {
            log.info("手机号为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号为空", null);
        }
        // 校验手机号码格式
        Pattern compile = Pattern.compile(PublicDictUtil.PHONE_NUMBER_REG);
        Matcher matcher = compile.matcher(reserveInfomation.getPhone());
        if (!matcher.matches()) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "手机号格式不能正确", null);
        }
        //发送信息
        String tempCodeString = RandomUtils.getRandomStr(6, 3);
        // 初始化预留信息
        ReserveInfomation reserveInfo = new ReserveInfomation();
        // 主键
        reserveInfo.setId(Sid.nextShort());
        // 设置过期时间
        long endTime = new Date().getTime() + (5 * 60 * 1000);
        reserveInfo.setSaveTime(new Date(endTime));
        // 设置手机验证码
        String tempCode = RandomUtils.getRandomStr(6, 3);
        // 设置验证码
        reserveInfo.setVerificationCode(tempCode);
        // 设置手机号
        reserveInfo.setPhone(reserveInfomation.getPhone());
        // 初始化返回类
        Integer count = null;
        try {
            count = reserveInfomationMapper.insertSelective(reserveInfo);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "数据库插入预留信息失败", null);
        }
        if (count > 0) {
            //发送短息验证码
            boolean flag = SendSms.SendSms(reserveInfomation.getPhone(), tempCode);
            if (flag) {
                return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "发送成功", null);
            } else {
                return Result.resultData(PublicDictUtil.ERROR_VALUE, "发送失败", null);
            }
        } else {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "发送失败", null);
        }
    }
}
