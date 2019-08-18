package com.XZYHOrderingFood.back.controller;


import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.QrCode;
import com.XZYHOrderingFood.back.pojo.ReserveInfomation;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.service.QrCodeService;
import com.XZYHOrderingFood.back.util.Config;
import com.XZYHOrderingFood.back.util.QrCodeUtils;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.github.vioao.wechat.api.QrCodeApi;
import com.github.vioao.wechat.bean.MediaFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("qrcode")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;
    
    @Autowired
    private Config config;
    

    /**
     * @description: 查询二维码信息接口
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    @PostMapping("select")
    @Auth
    public Result getQrCodeService (@RequestBody QrCode qrCode, HttpServletRequest httpServletRequest) {
        return qrCodeService.getQrCodeService(qrCode, httpServletRequest);
    }

    /**
     * @description: 根据二维码id更改是否删除状态
     * @author: zpy
     * @date: 2019-07-26
     * @params:
     * @return:
     */
    @PostMapping("update")
    @Auth
    public Result updateQrCodeStatus (@RequestBody QrCode qrCode, HttpServletRequest httpServletRequest) {
        return qrCodeService.updateQrCodeStatusById(qrCode, httpServletRequest);
    }
    
    /**
     * 添加餐桌
     */
    @GetMapping("add")
    @Auth
    public Result add(HttpServletRequest request) {
    	return qrCodeService.add(request);
    }
    
    /**
     * 下载微信二维码
     * @throws IOException 
     */
    @GetMapping("uploadWXQR")
    public void uploadWXQR(String id,HttpServletResponse response) throws IOException {
    	  qrCodeService.uploadWXQR(id,response);
    }
    
    /**
     * 下载路径二维码
     * @throws Exception 
     */
    @GetMapping("uploadQR")
    public void uploadQR(String id,HttpServletResponse response) throws Exception {
    	qrCodeService.uploadQR(id, response);
    }
    

    /**
     * @description: 通过商户查询桌号
     * @author: zpy
     * @date: 2019-08-03
     * @params:
     * @return:
     */
    @PostMapping("tablenum/select")
    @Auth
    public Result selectQrCodeNumById (HttpServletRequest httpServletRequest){
        return qrCodeService.selectQrCodeNumById(httpServletRequest);
    }
    
    @RequestMapping("/createQrCode")
    public String createQrCode(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer url = request.getRequestURL();
        try {
            OutputStream os = response.getOutputStream();
            //从配置文件读取需要生成二维码的连接
//            String requestUrl = GraphUtils.getProperties("requestUrl");
            //requestUrl:需要生成二维码的连接，logoPath：内嵌图片的路径，os：响应输出流，needCompress:是否压缩内嵌的图片
           return  QrCodeUtils.encode("http://www.baidu.com",null, config.getUploadPath(), "1号桌", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "no";
    }
 
}
