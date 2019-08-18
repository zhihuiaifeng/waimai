package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.ApplyReset;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.ApplyResetService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 密码重置controller
 * @author: zpy
 * @create: 2019-07-23 16:12
 **/
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/applyreset")
@Auth
public class ApplyResetController {

    @Autowired
    private ApplyResetService applyResetService;

    /**
     * @description: 查询重置密码申请表数据
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    @PostMapping("select")
    public Result getUserWhoApplyForReset (@RequestBody ApplyReset applyReset) {
        return applyResetService.getUserApplyReset(applyReset);
    }

    /**
     * @description: 同意申请接口
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    @PostMapping("click")
    public Result setUserApplyForReset (@RequestBody  TUser tUser) {
        return applyResetService.setUserApplyForReset(tUser);
    }
    
}
