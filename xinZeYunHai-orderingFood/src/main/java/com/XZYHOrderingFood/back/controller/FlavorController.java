package com.XZYHOrderingFood.back.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.TFlavor;
import com.XZYHOrderingFood.back.service.FlavorService;
import com.XZYHOrderingFood.back.util.Result;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/flavor")
@Auth
public class FlavorController {
 @Autowired
 private FlavorService flavorService;
 
 /**
  * 添加单品的口味
  */
 @PostMapping("add")
 public Result add(@RequestBody TFlavor flavor) {
	 return flavorService.add(flavor);
 }
 /**
  * 口味列表
  */
 @GetMapping("list")
 public Result<List<TFlavor>> list(String productId){
	 return flavorService.list(productId);
 }
 
 /**
  * 删除口味
  */
 @GetMapping("del")
 public Result del(String id) {
	 return flavorService.del(id);
 }
 
 /**
  * 口味排序
  */
 @PostMapping("editSort")
 public Result editSort(@RequestBody TFlavor flavor,HttpServletRequest request) {
	 return flavorService.editSort(flavor,request);
 }
 
}
