package com.XZYHOrderingFood.back.controller;

import com.XZYHOrderingFood.back.pojo.FanControl;
import com.XZYHOrderingFood.back.service.FanControlService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.XZYHOrderingFood.back.annotion.Auth;

import java.util.Map;

/**
 * banner控制器
 * @author dell
 *
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/fan")
@Auth
public class FanControlController {

    @Autowired
    private FanControlService fanControlService;

    /**
     * @description: 公众号新增粉丝折线图查询
     * @author: zpy
     * @date: 2019-07-25
     * @params: 
     * @return: 
     */
    @PostMapping("newfan/linechart")
    public Result getNewFanLineChart (@RequestBody FanControl fanControl) {
        return fanControlService.getNewFanLineChart(fanControl);
    }

    /**
     * @description: 粉丝模糊查询
     * @author: zpy
     * @date: 2019-07-31
     * @params:
     * @return:
     */
    @PostMapping("select")
    public Result selectFanFuzzyQuery (@RequestBody FanControl fanControl) {
        return fanControlService.selectFanFuzzyQuery(fanControl);
    }
}
