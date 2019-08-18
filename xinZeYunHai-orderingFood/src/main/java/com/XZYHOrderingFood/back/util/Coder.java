package com.XZYHOrderingFood.back.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
 

/**
 * 加密工具类
 * @author zhaoxinmin
 */
public class Coder {
	private Coder() {}
	
	public static final String CHAR_SET="UTF-8";
	public static final String KEY_SHA = "SHA";  
  
    /** 
     * MAC算法可选以下多种算法 
     *  
     * <pre> 
     * HmacMD5  
     * HmacSHA1  
     * HmacSHA256  
     * HmacSHA384  
     * HmacSHA512 
     * </pre> 
     */  
    public static final String KEY_MAC = "HmacMD5";  
  
    /** 
     * BASE64解密 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptBASE64(String key) throws Exception {  
    	return new Base64().decode(key);
    }  
  
    /** 
     * BASE64加密 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static String encryptBASE64(byte[] key) throws Exception {  
    	return new Base64().encodeAsString(key);
    }  
    
    /**
     * 排序方法 字典排序
     */
    public static String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
 
        return sb.toString();
    }
  
    /** 
     * MD5加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static String encryptMD5(byte[] data) throws Exception {  
        MessageDigest md5 = MessageDigest.getInstance(PublicDictUtil.KEY_MD5);
        md5.reset();
        md5.update(data);
        
        byte[] byteArray = md5.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1){
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			}else{
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
        return md5StrBuff.toString(); 
    }  
  
    /** 
     * SHA加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptSHA(byte[] data) throws Exception {  
  
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);  
        sha.update(data);  
  
        return sha.digest();  
  
    }  
    
    /**
     * 将字符串进行sha1加密
     *
     * @param str 需要加密的字符串
     * @return 加密后的内容
     */
    public static String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
  
    /** 
     * 初始化HMAC密钥 
     *  
     * @return 
     * @throws Exception 
     */  
    public static String initMacKey() throws Exception {  
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);  
  
        SecretKey secretKey = keyGenerator.generateKey();  
        return encryptBASE64(secretKey.getEncoded());  
    }  
  
    /** 
     * HMAC加密 
     *  
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {  
  
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);  
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
        mac.init(secretKey);  
  
        return mac.doFinal(data);  
  
    }
    
    
    public static final String keyDes="jingrixingfuDesdes@.*&";
    public static final String algorithm="DESede";
    
    public static byte[] encryptDES(byte[] src) throws Exception{
    	byte[] enk = hex(keyDes);
		SecretKey deskey = new SecretKeySpec(enk, algorithm);  
		Cipher c1 = Cipher.getInstance(algorithm);  
		c1.init(Cipher.ENCRYPT_MODE, deskey);  
		return c1.doFinal(src);  
   }  
     
	public static byte[] decryptDES(byte[] src) throws Exception{
		byte[] enk = hex(keyDes);
		SecretKey deskey = new SecretKeySpec(enk, algorithm);  
		Cipher c1 = Cipher.getInstance(algorithm);  
		c1.init(Cipher.DECRYPT_MODE, deskey);  
		return c1.doFinal(src);
	}
   
	public static byte[] hex(String key){  
		String f = DigestUtils.md5Hex(key);  
		byte[] bkeys = f.getBytes();
		byte[] enk = new byte[24];  
		for (int i=0;i<24;i++){  
			enk[i] = bkeys[i];  
		}  
		return enk;  
	}
	
	public static String encodeDES(String src) throws Exception{
		return encryptBASE64(encryptDES(src.getBytes(CHAR_SET)));
	}
    
    public static String decodeDES(String src) throws Exception{
    	return new String(decryptDES(decryptBASE64(src)),CHAR_SET);
    }
    
    public static String decodeBASE64(String str) throws Exception{
    	byte[] data=Coder.decryptBASE64(str);
    	return new String(data);
    }
    
    public static String encodeBASE64(String str) throws Exception{
    	byte[] key=str.getBytes(CHAR_SET);
    	return Coder.encryptBASE64(key);
    }
    
    public static String encodeMD5(String str,String salt) throws Exception{
    	if(str==null){
    		str="";
    	}
    	if(!StringUtils.hasLength(salt)) {
    		salt = PublicDictUtil.SALT_MD5 ;
    	}
    	str = str+salt;
    	String data=Coder.encryptMD5(str.getBytes(PublicDictUtil.CHAR_SET));
    	return data;
    }
    
    public static String encodeSHA(String str) throws Exception{
    	if(str==null){str="";}
    	byte[] data=Coder.encryptSHA(str.getBytes(CHAR_SET));
    	return Coder.bytesToString(data);
    }
    
    public static String bytesToString(byte[] data){
    	StringBuffer md5StrBuff = new StringBuffer();
		for (int i=0;i<data.length;i++){
			if (Integer.toHexString(0xFF & data[i]).length()==1) {
				md5StrBuff.append("0").append(
				Integer.toHexString(0xFF & data[i]));
			}else{
				md5StrBuff.append(Integer.toHexString(0xFF & data[i]));
			}
		}
		return md5StrBuff.toString();
    }
    
   public static void main(String[] args) throws Exception {
		
    	System.out.println(encodeMD5("juzhengjiaoyu", "juzhengjiaoyu"));
    	System.out.println(encodeDES("13920285501")+decodeDES("f864802e9be43b73eb2366725661add3"));
    	System.out.println(encodeDES("15620611773"));
    	System.out.println(encodeDES("13002208106"));
    	System.out.println(encodeDES("13102172652"));
    	System.out.println(encodeMD5("6ac6a79565663faff0bdcdf982680792", "AF34261587B44F7BB101FF91092A58704845038278523"));
    	
	} 
}
