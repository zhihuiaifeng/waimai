package com.XZYHOrderingFood.back.service;

import com.XZYHOrderingFood.back.dao.TicketMachineMapper;
import com.XZYHOrderingFood.back.pojo.OrderDetails;
import com.XZYHOrderingFood.back.pojo.OrderTable;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.pojo.TicketMachine;
import com.XZYHOrderingFood.back.util.Config;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.Util;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.XZYHOrderingFood.back.yiLianYun.Methods;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.json.JSONObject;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TicketMachineService {

	@Autowired
	private TicketMachineMapper ticketMachineMapper;

	@Autowired
	private Config config;

	/**
	 * @description: 查询小票机是否启用
	 * @author: zpy
	 * @date: 2019-07-24
	 * @params:
	 * @return:
	 */
	public TicketMachine getTicketMachineInfoByUserId(TUser user) throws Exception {
		if (StringUtils.isBlank(user.getId())) {
			log.info("用户id为空");
			throw new Exception("user is null");
		}
		TicketMachine ticketMachine = null;
		try {
			ticketMachine = ticketMachineMapper.selectByUserId(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticketMachine;
	}

	public Result bangding(TicketMachine ticketMachine, HttpServletRequest request) {
		if (StringUtils.isBlank(ticketMachine.getMachineCode()) || StringUtils.isBlank(ticketMachine.getMsign())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "终端号,和密钥不能为空", null);
		}

		// 初始化应用
		Methods.getInstance().init(config.getClientId(), config.getClientSecret());
		// 绑定小票机
		String result = Methods.getInstance().addPrinter(ticketMachine.getMachineCode(), ticketMachine.getMsign());
		JSONObject json = new JSONObject(result);
		String error = json.getString("error");
		String des = json.getString("error_description");
		if (!"0".equals(error) || !"success".equals(des)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "绑定失败", null);
		}
		int cont = 0;
		if (StringUtils.isNotBlank(ticketMachine.getId())  && ticketMachine.getId() != "0") {
			cont = ticketMachineMapper.update(ticketMachine);
		} else {
			TUser user = GetCurrentUser.getCurrentUser(request);
			if (user == null) {
				return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请重新登录后操作", null);
			}
			ticketMachine.setId(Sid.nextShort());
			ticketMachine.setCreateTime(new Date());
			ticketMachine.setTuserId(user.getId());
			ticketMachine.setBangdingStatus(1);
			  cont = ticketMachineMapper.insertSelective(ticketMachine);
		}

		
		if (cont > 0) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "绑定成功", null);
		}

		return Result.resultData(PublicDictUtil.ERROR_VALUE, "绑定失败", null);
	}

	public Result printStr(TicketMachine ticketMachine) {
		if (StringUtils.isBlank(ticketMachine.getMachineCode()) || StringUtils.isBlank(ticketMachine.getShopsName())) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "终端号,和商店名称不能为空", null);
		}
		String orderCode = Util.makeOrderNo("cs");
		return commentPrint(ticketMachine.getMachineCode(), printContent(ticketMachine, orderCode), orderCode);
	}

	/**
	 * 打印测试内容模板
	 */
	public String printContent(TicketMachine ticketMachine, String orderCode) {
		String orderTime = DateUtil.getStrHms(new Date());
		String str = "<FH2><FW2>*鑫泽云海点餐宝*</FW2></FH2>\r\n" + "\r\n" + "................................\r\n"
				+ "<FH2><FW2><center>--" + ticketMachine.getShopsName() + "--</center></FW2></FH2>\r\n" + "下单时间："
				+ orderTime + "\r\n" + "订单编号：" + orderCode + "\r\n" + "***************商品*************\r\n" + "\r\n"
				+ "<FH><FW><center>--1号桌--</center></FW></FH>\r\n"
				+ "<FH><FW><table><tr><td>餐品一</td><td>原味</td><td>x2</td></tr></table></FW></FH>\r\n"
				+ "<FH><FW><table><tr><td>餐品二</td><td>辣味</td><td>x1</td></tr></table></FW></FH>\r\n"
				+ "................................\r\n" + "<FH><FW>合计:\t\t￥44</FW></FH>\r\n"
				+ "<FH><FW>用餐人数:\t\t2人</FW></FH>\r\n" + "备  注:瞬间脸色看 \r\n" + "*******************************\r\n"
				+ "<FH2><FW2><center>**完**</center></FW2></FH2>\r\n" + "								";
		return str;
	}

	/**
	 * commentPrint
	 */
	public Result commentPrint(String machineCode, String printStr, String orderCode) {
		// 初始化应用
		Methods.getInstance().init(config.getClientId(), config.getClientSecret());
		// String orderCode = Util.makeOrderNo(null);
		String result = Methods.getInstance().print(machineCode, printStr, orderCode);
		JSONObject json = new JSONObject(result);
		String error = json.getString("error");
		String des = json.getString("error_description");
		if (!"0".equals(error) || !"success".equals(des)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "打印失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "订单生成，打印成功", null);
	}

	/**
	 * @description: 打印内容
	 * @author: zpy
	 * @date: 2019-07-30
	 * @params:
	 * @return:
	 */
	public String printContentCommon(TicketMachine ticketMachine, OrderTable orderTable, Integer isAddOrder) { // idAddOrder
																												// 为0是下单
																												// 为1是加餐
		String orderTime = DateUtil.getStrHms(new Date());
		StringBuilder str = new StringBuilder();
		str.append("<FH2><FW2>*鑫泽云海点餐宝*</FW2></FH2>\r\n" + "\r\n" + "................................\r\n"
				+ "<FH2><FW2><center>--" + ticketMachine.getShopsName() + "--</center></FW2></FH2>\r\n" + "下单时间："
				+ orderTime + "\r\n" + "订单编号：" + orderTable.getOrderCode() + "\r\n"
				+ "***************商品*************\r\n" + "\r\n");
		if (isAddOrder == 0) {
			str.append("<FH><FW><center>--" + orderTable.getTableNumber() + "号桌--</center></FW></FH>\r\n");
		} else if (isAddOrder == 1) {
			str.append("<FH><FW><center>--" + orderTable.getTableNumber() + "号桌(加餐)--</center></FW></FH>\r\n");
		}
		for (OrderDetails details : orderTable.getOrderDetails()) {
			// 如果口味不为空
			if (details.getTFlavor() != null) {
				str.append(
						"<FH><FW><table><tr><td>" + details.getFoodName() + "</td><td>" + details.getTFlavor().getName()
								+ "</td><td>x" + details.getFoodNumber() + "</td></tr></table></FW></FH>\r\n");
			} else {
				str.append("<FH><FW><table><tr><td>" + details.getFoodName() + "</td><td>原味</td><td>x"
						+ details.getFoodNumber() + "</td></tr></table></FW></FH>\r\n");
			}
		}
		str.append("................................\r\n");
		str.append("<FH><FW>合计:\t\t￥" + orderTable.getTotalMoney() + "</FW></FH>\r\n");
		str.append("<FH><FW>用餐人数:\t\t" + orderTable.getMealsNumber() + "人</FW></FH>\r\n");
		if (orderTable.getRemark() != null) {
			str.append("备  注:" + orderTable.getRemark() + " \r\n");
		} else {
			str.append("备  注: 空 \r\n");
		}
		str.append("*******************************\r\n");
		str.append("<FH2><FW2><center>**完**</center></FW2></FH2>\r\n");
		str.append("								");
		return str.toString();
	}

	/**
	 * @description: 修改小票机状态
	 * @author: zpy
	 * @date: 2019-08-01
	 */
	public Integer updateTicketMachineStatus(TUser tUser) throws Exception {
		Integer count = null;
		try {
			count = ticketMachineMapper.updateTicketStautsByUserId(tUser);
		} catch (Exception e) {
			throw new Exception();
		}
		return count;
	}

	public Result<TicketMachine> getTicket(HttpServletRequest request) {
		TUser usrTUser = GetCurrentUser.getCurrentUser(request);
		if (usrTUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		}
		/*
		 * TicketMachine ticketMachine = new TicketMachine();
		 * ticketMachine.setTuserId(usrTUser.getId());
		 */

		TicketMachine currentData = ticketMachineMapper.selectByUserId(usrTUser.getId());
		if (currentData != null) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, null, currentData);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "当前商户没有小票机", null);
	}

	public Result getTicketIsUsed(HttpServletRequest request) {
		TUser usrTUser = GetCurrentUser.getCurrentUser(request);
		if (usrTUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		}
		TicketMachine ticketMachine = ticketMachineMapper.selectByUserId(usrTUser.getId());
		if (ticketMachine == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "该商户未绑定小票机", null);
		}
		if (usrTUser.getTicketStatus() != null) {
			ticketMachine.setIsBusiness(usrTUser.getTicketStatus());
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "商家营业状态获取失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询商户成功", ticketMachine);
	}

	public Result editTicketIsUsed(Integer isUsed, HttpServletRequest request) {
		 if(isUsed == null) {
			 return Result.resultData(PublicDictUtil.ERROR_VALUE, "启用状态值不能为空", null);
		 }
		 TUser usrTUser = GetCurrentUser.getCurrentUser(request);
		if (usrTUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请先登录", null);
		}
		 TicketMachine ticketMachineOld = ticketMachineMapper.selectByUserId(usrTUser.getId());
		if (ticketMachineOld == null) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "未绑定小票机", null);
		}
		 TicketMachine ticketMachine = new TicketMachine();
		 ticketMachine.setIsUsed(isUsed);
		 ticketMachine.setId(ticketMachineOld.getId());
		 int count = ticketMachineMapper.update(ticketMachine);
		 if(count > 0) {
			 return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
		 }
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}

}
