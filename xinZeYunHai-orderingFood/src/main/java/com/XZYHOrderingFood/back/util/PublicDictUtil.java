package com.XZYHOrderingFood.back.util;

public class PublicDictUtil {

    private PublicDictUtil(){}

	//redis_ip
	public static final String REDIS_IP = "localhost";


	//阿里支付地址
	public static final String aliPay_URL = "https://openapi.alipay.com/gateway.do";

	// KEY
	public static final String KEY = "result";// 返回结果key 

	public static final String DATA_KEY = "data";// 返回结果数据key

	public static final String MSG_KEY = "msg";// 返回消息key

	// VALUE

	public static final String SUCCESS_VALUE = "100";// 返回成功码

	public static final String ERROR_VALUE = "-100";// 返回失败码

	public static final String TOKEN_INVAALID = "-101";// 返回失败码

	public static final String LOGIN_ERRO_VALUE = "-1";// 重新登录

	public static final String ERROR_DOT_VALUE = "101";
	
	public static final String IDE_REPEAT_ERROR_VALUE= "10001";//身份验证重复

	public static final String IDE_1002_VALUE= "10002";//无订单欠款详情--附表为空

	public static final String IDE_1003_VALUE= "10003";//用户没有钱包

	public static final String IDE_1004_VALUE= "10004";//用户余额不足

	
	//obd 接口参数 uid
	public static final String OBD_UID = "ZHCX001";
	
	public static final String CHAR_SET="UTF-8";
	
    public static final String KEY_MD5 = "MD5";  
    
    // 盐
    public static final String SALT_MD5 = "JINRxingFu";
	
    /*************************************** ====  geet config start ====*****************************/
  
    
    public static final String WIDTH = "95";
    
    public static final String HEIGHT = "37";
    
public static final String industryWIDTH = "282";
    
    public static final String industryHEIGHT = "300";
    
    public static final String  ISFIRSTLOGIN = "001";  //第一次登录
    
    public static final String THUMBNAIL="th";

	public static final String INITPASSWORD = "123456";
    
    public static final String VERNAME="4.0";
    
    public static final String SDKLANG="java";

    public static final String APIURL="http://api.geetest.com";
    
    public static final String REGISTERURL="/register.php";
    
    public static final String VALIDATEURL="/validate.php";
    
    public static final String JSON_FORMAT="1";
    
    public static final String  FN_GEET_CHALLENGE = "geetest_challenge"; 
    
    public static final String FN_GEET_VALIDATE="geetest_validate";
    
    public static final String FN_GEET_SECCODE="geetest_seccode";
    
    public static final String GT_SERVER_STATUS_SESSION_KEY="gt_server_status";
    
    public static final String GT_USERID="gtuserid";
    
    public static final String USER_TOKE="token";
    
    public static final String USER = "user";

	public static final String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

	public static final int NO_DEL = 0; //没有删除
	
	public static final int DEL = 1; //删除
	
	public static final int CLOSE = 0; //停用
	
	public static final int OPEN = 1; //启用
	
	public static final int dealer_role = 1;//经销商
	
	public static final int crew_role = 2; //人员

    /*************************************** ====  geet config end ==== *****************************/  
    
    
    /*************  ===  polyv start ===== **************/ 
	public static final String POLYV_USER_ID = "c0b998058c"; 
   	
   	public static final String POLYV_WRITE_TOKEN = "c1a26cb8-37f2-4a83-a457-02f7bea651ac";
   	
   	public static final String POLYV_READ_TOKEN = "6b6832ab-88cd-41d7-82d5-b1f9e41e27d0";
   	
   	public static final String POLYV_SECRET_KEY  = "Y96RUqhiQX";
   	
   	public static final boolean POLYV_SGIN = false;
   	
   	public static final String POLYV_SDK_CODE = "d+2Rzlez7JmEXCyp0RXf22aZoG0RT1LybmKd1+HBMs1198MseV31MbV7NUmlYzA03cxoTrAazOK01ErEt+SS2C9qrvSmNxsAUOcpBNbW+Fx5jj0JlxGTVkYq+TkZvQzoQj9eOh7gi/I7FOKlDvE3lg==";
	
	/*************  ===  polyv end ===== **************/ 
    
    public static final int SEND_SMS_COUNT = 10;
    
    /*************  === pay bank start===== **************/ 
    public static final String APP_ID = "wx40eb9ab3869be94d";
    
    public static final String MCH_ID = "1509116551";  
    
    public static final String WX_KEY = "cc7b2de05ced926c124c4911232bb718";
    
    public static final String PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----" + 
    		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx24BIrMFrixmEGlWAbjJ" + 
    		"lNgSuXYwFl6EqHqQhfBhN+2suZAnLcIP6oD+7cYmhfYbhfBhwAu8enZqUUGWFG6J" + 
    		"gt34PDqbPGy0v4xdkQVtLedt3UnpysD1GDUt6VrUGWyFPLUF9KDt+np0rSi8BcA9" + 
    		"rLuIDHStlNePPDYCbnwVFBuGC6tkU6WMphfgHP3enI9tfu6zprULm7Lq0CIhCShb" + 
    		"+USssjIF8cDgUiWwa6E2A0tDm4Hz2VyEKcC129HqHiGz66BA1XclOqy4TUGZrzWZ" + 
    		"5BONy9/t2xILs7a+49wDtPwCRMr8uv9vVxdwxxFR2OOw6UJXVw3q0aGf9kdaTcCi" + 
    		"dwIDAQAB"+ 
    		"-----END PUBLIC KEY-----";
    
    /*************  === pay bank end===== **************/ 
    
    
    /*************  === 极验 start===== **************/ 
    public static String GEETEST_ID = "9952456ad9ef4573538a7cc116a41ae0";
    public static String GEETEST_KEY = "fe4399191c78f8cd3734589a1bb2fb06";
    public static boolean NEW_FAILBACK = true;
    /*************  === 极验 end===== **************/
    
    /*************  === 微信 start===== **************/
    public static String ACCESS_TOKEN = "access_token";
    /*************  === 微信 end===== **************/
    
    
    
    /*************  === 阿里短信 start===== **************/
    public static String ACCESSKEYID = "LTAIQlHxnhDgrWgs";
    public static String ACCESSSECRET = "7vOdirW2wQ6uXHntAgfxpgjhjdXuKH";
    /*************  === 阿里短信  end===== **************/
    
    
    /*************  === 课程图片类型接口===== **************/ 
    
    public interface CourseImg{
    	int IMG_POSTER = 1; //海报
    	int IMG_LOGO = 0;//logo
    	int IMG_COMMON = 2; //轮播图
    }
    
}
