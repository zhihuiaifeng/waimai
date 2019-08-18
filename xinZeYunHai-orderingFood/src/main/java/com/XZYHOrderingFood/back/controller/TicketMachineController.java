package com.XZYHOrderingFood.back.controller;


import com.XZYHOrderingFood.back.annotion.Auth;
import com.XZYHOrderingFood.back.pojo.TicketMachine;
import com.XZYHOrderingFood.back.service.TicketMachineService;
import com.XZYHOrderingFood.back.util.Result;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/ticketMachine")
@Auth
public class TicketMachineController {


    @Autowired
    private TicketMachineService ticketMachineService;
    
    @PostMapping("bangding")
    public Result bangding(@RequestBody TicketMachine ticketMachine,HttpServletRequest request) {
    	return ticketMachineService.bangding(ticketMachine,request);
    }
    
    /**
     * 打印
     */
     @PostMapping("printStr")
     public Result printStr(@RequestBody TicketMachine ticketMachine) {
    	 return ticketMachineService.printStr(ticketMachine);
     }
     
     /**
      * 查询当前商户的小票机数据
      */
     @GetMapping("getTicket")
     public Result<TicketMachine> getTicket(HttpServletRequest request){
    	 return ticketMachineService.getTicket(request);
     }
     
     /**
      * 查询当前商户小票机 是否启用状态
      */
     @GetMapping("getTicketIsUsed")
     public Result getTicketIsUsed(HttpServletRequest request) {
    	 return ticketMachineService.getTicketIsUsed(request);
     }
     
     /**
      * 编辑小票机启用状态
      */
     @GetMapping("editTicketIsUsed")
     public Result editTicketIsUsed(Integer isUsed,HttpServletRequest request) {
    	 return ticketMachineService.editTicketIsUsed(isUsed,request);
     }

     
}
