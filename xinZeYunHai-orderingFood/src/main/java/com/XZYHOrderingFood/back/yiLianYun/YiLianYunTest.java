package com.XZYHOrderingFood.back.yiLianYun;

/**
 * 小票机测试类型
 * @author dell
 *
 */
public class YiLianYunTest {
	
	public static String access_token = "7e2368e232ef4c2b8874a6ac319ea65a";
	
	public static  String  content = "<FH2><FB><center>快乐烧烤</center></FB></FH2>\r\n" + 
			"<FH><center>序号  名称  单价  数量  金额</center></FH>\r\n" + 
			
			"--------------------------------<FH>\r\n" + 
			
			"1 夏阳白 7.36 1 4.61\r\n" +
			 
			"2 折价地瓜  7.36 2 4.61\r\n" + 
			
			"</FH>\r\n" + 
			"--------------------------------\r\n" + 
			"<FH>\r\n" + 
			"<right>应付款：￥6.40 </right>\r\n" + 
		 
			"<right> 2017-09-26 21:09:03</right>\r\n" + 
			"</FH>\r\n";
	
	public static String tt = 
			"<audio>1357,5,0</audio>"+
			"<FH2><FW2>*鑫泽云海点餐宝*</FW2></FH2>\r\n" + 
					"\r\n"+
			"................................\r\n" + 
			"<FH2><FW2><center>--快乐烧烤--</center></FW2></FH2>\r\n" + 
			"下单时间：2017-05-08T10：39：56\r\n" + 
			"订单编号：1206479504417779722\r\n" + 
			"***************商品*************\r\n" + 
			"\r\n"+
			"<FH><FW><center>--1号桌--</center></FW></FH>\r\n"+
			"<FH><FW><table><tr><td>腊肠 </td><td>原味</td><td>x2</td></tr></table></FW></FH>\r\n" + 
			"<FH><FW><table><tr><td>里脊肉</td><td>原味</td><td>x1</td></tr></table></FW></FH>\r\n" + 
			"................................\r\n" + 
			"<FH><FW>合计:\t\t￥44</FW></FH>\r\n" + 
			"<FH><FW>用餐人数:\t\t2人</FW></FH>\r\n" + 
			"备  注:瞬间脸色看 \r\n" + 
			"*******************************\r\n" + 
			"<FH2><FW2><center>**完**</center></FW2></FH2>\r\n" + 
			"								";
	
	
	//{"error":"0","error_description":"success","body":{"access_token":"7e2368e232ef4c2b8874a6ac319ea65a","refresh_token":"5d9c07f5d6e9478ba58acb7eb575bb50","machine_code":"","expires_in":2592000,"scope":"all"}}

	
   public static void main(String[] args) {
	    
	   Methods.getInstance().init("1097777372","cdcce275b145df2a4b309ccbc2c1925e");
	   //String access_token =  Methods.getInstance().getFreedomToken();
	  // Methods.getInstance().refreshToken();
	  Methods.getInstance().addPrinter("4004623211","716167682985");
	  //String resultString =  Methods.getInstance().setIcon("4004623211", "http://xinzeyunhai.com/uploadFile/banner.png");
		 Methods.getInstance().print("4004623211",tt,"123");  
	  //System.out.println(resultString);
	  
}
   
}
