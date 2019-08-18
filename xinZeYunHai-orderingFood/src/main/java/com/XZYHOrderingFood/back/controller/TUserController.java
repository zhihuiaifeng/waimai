package com.XZYHOrderingFood.back.controller;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.bingoohuang.utils.net.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.TUserService;
import com.XZYHOrderingFood.back.util.Result;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/user")
 
public class TUserController {

    @Autowired
    private TUserService tUserService;


    /**
     * 添加用户
     * @throws Exception
     */
    @PostMapping("add")
    @Auth
    public Result add(@RequestBody TUser user,HttpServletRequest request) throws Exception {
    	return tUserService.add(user,request);
    }

    /**
     * 编辑用户
     */
    @PostMapping("edit")
    @Auth
    public Result edit(@RequestBody TUser user,HttpServletRequest request) throws Exception{
    	return tUserService.edit(user,request);
    }

    /**
     * 登录
     */
    @PostMapping("login")
    public Result<Map<String, String>> login(@RequestBody TUser user,HttpServletRequest request,HttpServletResponse response)throws Exception{
    	return tUserService.login(user,request,response);
    }

    /**
     * 修改密码
     * @throws Exception
     */
    @PostMapping("editPassword")
    public Result editPassword(@RequestBody TUser user) throws Exception {
    	return tUserService.editPassword(user);
    }

    /**
     * 申请重置密码
     */
    @GetMapping("applyResetPassword")
    @Auth
    public Result resetPassword(String username) {

    	return tUserService.applyResetPassword(username);
    }

    /**
     * 获取所有用户列表
     * @param request
     * @return
     */
    @RequestMapping("/getAllUser")
    public Result<List<TUser>> getAllUser(HttpServletRequest request,Model model){
//        List<TUser> tuser = tUserService.findAll();
//        model.addAttribute("userList", tuser);
//        request.setAttribute("userList", tuser);
        return tUserService.findAll();
    }


    /**
	 * 删除用户信息
	 */
	 @RequestMapping("/deleteTUser")
	 public Result deleteTUser(String id) {

	     return tUserService.del(id);
		 }


    /**
     * @description: 信心中心 综合信息查询接口
     * @author: zpy
     * @date: 2019-07-22
     * @params: [servletRequest]
     * @return: com.XZYHOrderingFood.back.util.Result<java.util.Map<java.lang.String,java.lang.Integer>>
     */
    @PostMapping("general/information")
    public Result<Map<String, BigDecimal>> getGeneralInformation(HttpServletRequest httpServletRequest){
        return tUserService.getGeneralInfoList(httpServletRequest);
    }

    /**
     * @description: 信息中心 新增商户折线图查询接口
     * @author: zpy
     * @date: 2019-07-22
     * @params:
     * @return:
     */
    @PostMapping("newshop/linechart")
    public Result<Map<String,Integer>> getNewShopChart(@RequestBody TUser tUser){
        return tUserService.getNewShopChart(tUser);
    }

    /**
     * @description: 省为单位用户数量图
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    @PostMapping("provincenum/select")
    public Result getUserNumberByPrivince (HttpServletRequest httpServletRequest) {
        return tUserService.getUserNumber(httpServletRequest);
    }

    /**
     * @description: 查询全部商户 通过传入的用户级别
     * @author: zpy
     * @date: 2019-07-24
     * @params: []
     * @return: com.XZYHOrderingFood.back.util.Result
     */
    @PostMapping("info/select")
    public Result getUserInfo (@RequestBody TUser tUser, HttpServletRequest httpServletRequest) {
        return tUserService.getAllUserInfo(tUser, httpServletRequest);
    }

    /**
     * @description: 查询后台账户
     * @author: zpy
     * @date: 2019-08-02
     * @params: []
     * @return: com.XZYHOrderingFood.back.util.Result
     */
    @PostMapping("selfinfo/select")
    public Result getUserInfoById (@RequestBody TUser tUser ) {
        return tUserService.getUserInfoById(tUser);
    }

    /**
     * @description: 查询全部后台账户
     * @author: zpy
     * @date: 2019-07-24
     * @params: []
     * @return: com.XZYHOrderingFood.back.util.Result
     */
    @PostMapping("backinfo/select")
    public Result getBackUserInfo (@RequestBody TUser tUser) {
        return tUserService.getBackUserInfo(tUser);
    }

    /**
     * @description: 创建新商户
     * @author: dell
     * @date: 2019-07-24
     * @params:
     * @return:
     */
    @PostMapping("newshop/create")
    public Result createNewUser (@RequestBody TUser tUser, HttpServletRequest request) throws Exception {
        return tUserService.createNewUser(tUser, request);
    }

    /**
     * @description: 修改商户信息
     * @author: zpy
     * @date: 2019-07-24
     * @params: 
     * @return: 
     */
    @PostMapping("info/edit")
    public Result updateUserInfo (@RequestBody TUser tUser) {
        return tUserService.updateUserInfo(tUser);
    }

    /**
     * @description: 商家查看个人信息接口  两接口合并
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
//    @PostMapping("shopself/select")
//    public Result getShopselfInfo (HttpServletRequest httpServletRequest) {
//        return tUserService.getAllUserInfo(httpServletRequest);
//    }

    /**
     * @description: 店铺信息详情保存
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    @PostMapping("shopdetails/add")
    public Result addShopDetailInfo (@RequestBody TUser tUser,  HttpServletRequest httpServletRequest) {
        return tUserService.addShopDetalInfo(tUser, httpServletRequest);
    }

    /**
     * @description: 收入折现图查询接口
     * @author: zpy
     * @date: 2019-07-25
     * @params:
     * @return:
     */
    @PostMapping("shopincome/line")
    public Result getShopIncomeLine (@RequestBody TUser tUser, HttpServletRequest httpServletRequest) {
        return tUserService.getShopIncomeLine(tUser, httpServletRequest);
    }
    
    /**
     * 发送验证码
     * @throws Exception 
     */
    @GetMapping("sendSmsCode")
    public Result sendSmsCode(String phone) throws Exception {
    	return tUserService.sendSmsCode(phone);
    }
    
   /**
    * 登出
    */
    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
    	return tUserService.logout(request);
    }

    /**
     * @description: 小票机状态改变接口
     * @author: zpy
     * @date: 2019-08-01
     * @params:
     * @return:
     */
    @PostMapping("ticketstatus/update")
    public Result updateTicketMachineStatus (@RequestBody TUser tUser) {
        return tUserService.updateTicketMachineStatus(tUser);
    }
    
    /**
     * 查询当前用户信息
     */
    @GetMapping("getUserInfo")
    public Result<TUser> getUserInfo(String id,HttpServletRequest request){
    	return tUserService.getUserInfo(id,request);
    	
    }

    /**
     * 修改商家营业状态
     * @return
     */
    @PostMapping("business/update")
    public Result updateUserBusinessStatus (@RequestBody TUser tUser, HttpServletRequest request) {
        return tUserService.updateUserBusinessStatus(tUser, request);
    }

    /**
     * 商铺上传图片
     * @param id
     * @param file
     * @return
     */
    @PostMapping("img/update")
    public Result uploadShopImg (MultipartFile pImg, HttpServletRequest httpServletRequest) {
        return tUserService.uploadShopImg(pImg, httpServletRequest);
    }

}
