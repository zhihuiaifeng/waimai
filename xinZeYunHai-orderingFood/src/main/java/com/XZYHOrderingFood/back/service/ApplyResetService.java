package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.dao.ApplyResetMapper;
import com.XZYHOrderingFood.back.dao.TUserMapper;
import com.XZYHOrderingFood.back.myEnum.UserEnum;
import com.XZYHOrderingFood.back.pojo.ApplyReset;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 密码重置业务层
 * @author: zpy
 * @create: 2019-07-23 16:14
 **/
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ApplyResetService {

    @Autowired
    private ApplyResetMapper applyResetMapper;

    @Autowired
    private TUserMapper tUserMapper;

    /**
     * @description: 查询商户申请重置密码表
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    public Result getUserApplyReset (ApplyReset applyReset) {
        if (applyReset.getIsAgree() == null) {
            log.info("查询条件获取失败");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询条件获取失败", null);
        }
        // 查询申请记录
        List<ApplyReset> applyResetList = null;
        try {
            applyResetList = applyResetMapper.selectUserApplyReset(applyReset);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询重置密码失败,请联系管理员", null);
        }
        List<String> strings = new ArrayList<>();
        if (applyResetList != null) {
            // 通过传入userid查询用户信息
            for (ApplyReset apply : applyResetList
            ) {
                strings.add(apply.getUserId());
            }
        }
        List<TUser> tUserList = tUserMapper.selectByIdList(strings);
        for (ApplyReset applyReset1:applyResetList
             ) {
            for (TUser tUser:tUserList
                 ) {
                if (applyReset1.getUserId().equals(tUser.getId())) {
                    applyReset1.setUsername(tUser.getUsername());
                    applyReset1.setClientname(tUser.getClientname());
                }
            }
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询重置密码成功", applyResetList);
    }

    /**
     * @description: 同意重置密码请求
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    public Result setUserApplyForReset (TUser tUser) {
        // 两个操作，一个将apply表的isdelete置0,一个将user表的is_first_login置为1
        if (StringUtils.isBlank(tUser.getId())) {
            log.info("主键不能为空");
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "主键不能为空", null);
        }
        TUser user = tUserMapper.findUserIsExis(tUser);
        if (user == null) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "传入id有误", null);
        }
        // 设置为已数据已删除
        ApplyReset applyReset = new ApplyReset();
        applyReset.setUserId(tUser.getId());
        applyReset.setIsAgree(1);
        applyReset.setModifyTime(new Date());
        // 设置user表为第一次登陆
        tUser.setIsFirstLogin(UserEnum.IS_FIRST_LOGIN.getCode());
        try {
            // 修改申请表的字段
            int appResult = applyResetMapper.updateByUserIdSelective(applyReset);
            // 修改user表的是否第一次登陆
            int userResult = tUserMapper.updateByPrimaryKeySelective(tUser);
            System.out.println(appResult+"----"+userResult);
        } catch (Exception e) {
            return Result.resultData(PublicDictUtil.ERROR_VALUE, "同意该申请失败,请联系管理员", null);
        }
        return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "已同意该申请", null);
    }

}
