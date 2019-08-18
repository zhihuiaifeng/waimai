package com.XZYHOrderingFood.back.sms;

import org.springframework.stereotype.Component;

import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.RandomUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送
 * 
 * @author dell
 *
 */
@Component
@Slf4j
public class SendSms {
	
	  
	/*
	 * public static void main(String[] args) {
	 * 
	 * String tempCode = RandomUtils.getRandomStr(6, 3);
	 * SendSms.SendSms("18202587139", tempCode);
	 * 
	 * 
	 * }
	 */
	 

	public static boolean SendSms(String phone, String tempCode) {
		boolean flag = false;
		DefaultProfile profile = DefaultProfile.getProfile("default", PublicDictUtil.ACCESSKEYID,
				PublicDictUtil.ACCESSSECRET);
		IAcsClient client = new DefaultAcsClient(profile);
		String jsonCode = "{" + "\"code\":" + "\"" + tempCode + "\"}";

		log.info("jsonCode=========" + jsonCode);
		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
		request.setDomain("dysmsapi.aliyuncs.com");
		request.setVersion("2017-05-25");
		request.setAction("SendSms");
		request.putQueryParameter("RegionId", "default");
		request.putQueryParameter("PhoneNumbers", phone);
		request.putQueryParameter("SignName", "鑫泽云海");
		request.putQueryParameter("TemplateCode", "SMS_171076881");
		request.putQueryParameter("TemplateParam", jsonCode);
		request.putQueryParameter("SmsUpExtendCode", " ");
		request.putQueryParameter("OutId", " ");
		// {"Message":"OK","RequestId":"B1844C3A-63CB-44BF-B0C4-85AC1D369EF7","BizId":"194001564540907806^0","Code":"OK"}
		try {
			CommonResponse response = client.getCommonResponse(request);
			JSONObject jb = JSON.parseObject(response.getData());
			log.info((String)jb.get("Message"));
			if("ok".equals((String)jb.get("Message"))) {
				flag = true;
			}else {
				flag = false;
			}
			log.info(jb.toString());
			
		} catch (ServerException e) {
			flag = false;
		} catch (ClientException e) {
			flag = false;
		}
		return flag;
	}
}