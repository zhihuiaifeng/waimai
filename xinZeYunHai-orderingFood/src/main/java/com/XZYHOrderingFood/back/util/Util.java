package com.XZYHOrderingFood.back.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.tika.Tika;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
 

/**
 * 工具类
 * @author lzg
 *
 */
public class Util {
	
	private Util(){}

	private static final DateFormat formatter =new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static final Tika TIKA = new Tika();
	
	/**
     * 获取文件扩展名
     * @return
     */
    public static String getFileSuffix(String fileName){
    	return fileName.substring(fileName.lastIndexOf("."));
    } 
    
    /**
     * 生成订单号
     * @param suffix 后缀
     * @return
     */
    public static String makeOrderNo(String suffix){
    	if(suffix == null) {
    		suffix="00";
    	}
    	int r1=(int)(Math.random()*(1000/10-1));//产生2个0-9的随机数
		int r2=(int)(Math.random()*(10000/10-2));
		long now = System.currentTimeMillis()+1000;//一个13位的时间戳
		String paymentID =String.valueOf(r1)+String.valueOf(r2)+String.valueOf(now);// 订单ID
		return paymentID+suffix;
	}
    
    
    /**
	 * 获取两个时间相差的天数,不足一天 按1天算
	 * @param d1
	 * @param d2
	 * @return 相差天数
	 */
	public static int daysBetween(Date d1,Date d2){
		DateTime f1 =new DateTime(d1);
		DateTime f2 =new DateTime(d2);
		int day=Days.daysBetween(f2, f1).getDays();//相差天数
		return day+1;
		
	}
	/**
	 * 计算两个时间相差的分钟数 不足一分钟按1分钟算
	 * @param d1 大时间
	 * @param d2 小时间
	 * @return 相差分钟数
	 */
	public static int minutesBetWeen(Date d1,Date d2) {
		DateTime f1 =new DateTime(d1);
		DateTime f2 =new DateTime(d2);
		int minutes=Minutes.minutesBetween(f2, f1).getMinutes();
		Days.daysBetween(f2, f1).getDays();//相差天数
		return minutes+1;
	}
	
	/**
	 * 加几天后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date timeAddition(Date date,int day) {
		
		DateTime f1 =new DateTime(date);
		DateTime time= f1.plusDays(day);
		return time.toDate();
	}
	
	/*
	public static JSONObject validate(HttpServletRequest request,String geetId,String geetKey){
		GeetestLib gtSdk = new GeetestLib(geetId,geetKey, true);
		
		String challenge = request.getParameter(PublicDictUtil.FN_GEET_CHALLENGE);
		String validate = request.getParameter(PublicDictUtil.FN_GEET_VALIDATE);
		String seccode = request.getParameter(PublicDictUtil.FN_GEET_SECCODE);
		//从session中获取gt-server状态
		int gt_server_status_code = (Integer) request.getSession().getAttribute(PublicDictUtil.GT_SERVER_STATUS_SESSION_KEY);
		//从session中获取userid
		String userid = (String)request.getSession().getAttribute("userid");
		int gtResult = 0;
		if (gt_server_status_code == 1) {
			//gt-server正常，向gt-server进行二次验证
			gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, userid);
			System.out.println(gtResult);
		} else {
			// gt-server非正常情况下，进行failback模式验证
			System.out.println("failback:use your own server captcha validate");
			gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
			System.out.println(gtResult);
		}
		JSONObject data = new JSONObject();
		if (gtResult == 1) {
			// 验证成功
			try {
				data.put("status", "success");
				data.put("version", gtSdk.getVersionInfo());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			// 验证失败
			try {
				data.put("status", "fail");
				data.put("version", gtSdk.getVersionInfo());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	*/
	
	
	/*public static String  startCaptcha(HttpServletRequest request,String geetId,String geetKey){
		GeetestLib gtSdk = new GeetestLib(geetId,geetKey , true);
		String resStr = "{}";
		//自定义userid
		String userid =System.currentTimeMillis()+RandomUtils.getRandomStr(10, 1);
		//进行验证预处理
		int gtServerStatus = gtSdk.preProcess(userid);
		//将服务器状态设置到session中
		request.getSession().setAttribute(PublicDictUtil.GT_SERVER_STATUS_SESSION_KEY, gtServerStatus);
		//将userid设置到session中
		request.getSession().setAttribute(PublicDictUtil.GT_USERID, userid);
		resStr = gtSdk.getResponseStr();
		return resStr;
	}*/
	
	
	/**
	 * 将请求过来的参数 转换为map
	 * @param parameterMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map transToMAP(Map requestParameterMap){
	      // 返回值Map
	      Map returnMap = new HashMap();
	      Iterator entries = requestParameterMap.entrySet().iterator();
	      Map.Entry entry;
	      String name = "";
	      String value = "";
	      while (entries.hasNext()) {
	          entry = (Map.Entry) entries.next();
	          name = (String) entry.getKey();
	          Object valueObj = entry.getValue();
	          if(null == valueObj){
	              value = "";
	          }else if(valueObj instanceof String[]){
	              String[] values = (String[])valueObj;
	              for(int i=0;i<values.length;i++){
	                  value = values[i] + ",";
	              }
	              value = value.substring(0, value.length()-1);
	          }else{
	              value = valueObj.toString();
	          }
	          returnMap.put(name, value);
	      }
	      return  returnMap;
	  }
	
	public static String getFileRealType(File file) throws IOException {
		return  Util.TIKA.detect(file);
	}
	
	public static String getFileRealType(byte [] bytes) {
		return Util.TIKA.detect(bytes);
	}
	
	public static String getFileRealType(InputStream inputStream) throws IOException {
		return Util.TIKA.detect(inputStream);
	}
	
	/**
     *  request获取真实ip地址
     * @return
     */
    public static String getRemoteHost(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }
	
}
