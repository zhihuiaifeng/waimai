package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.ReserveInfomation;
import com.XZYHOrderingFood.back.service.ReserveInfomationService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("reserve")
@Auth
public class ReserveInfomationController {


    @Autowired
    private ReserveInfomationService reserveInfomationService;

    /**
     * @description: 添加寻求合作信息（手机号验证码未开发）
     * @author: zpy
     * @date: 2019-07-24
     * @params:
     * @return:
     */
    @PostMapping("info/add")
    public Result addReserveInfo (@RequestBody ReserveInfomation reserveInfomation) {
        return reserveInfomationService.addReserveInfo(reserveInfomation);
    }
    
    /**
     * @description: 发送验证码接口
     * @author: zpy
     * @date: 2019-07-31
     * @params: phone 手机号
     * @return: 
     */
    @PostMapping("info/send")
    public Result sendPhoneCode (@RequestBody ReserveInfomation reserveInfomation) {
        return reserveInfomationService.sendPhoneCode(reserveInfomation);
    }
}
