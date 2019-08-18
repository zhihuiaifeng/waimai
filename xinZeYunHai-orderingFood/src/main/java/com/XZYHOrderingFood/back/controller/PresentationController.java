package com.XZYHOrderingFood.back.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.XZYHOrderingFood.back.annotion.Auth;

/**
 * banner控制器
 * @author dell
 *
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/banner")
@Auth
public class PresentationController {

}
