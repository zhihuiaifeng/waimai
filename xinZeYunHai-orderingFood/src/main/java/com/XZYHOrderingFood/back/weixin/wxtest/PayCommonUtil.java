package com.XZYHOrderingFood.back.weixin.wxtest;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public  class PayCommonUtil {

    private static WXPayConfigImpl config;

    //定义签名，微信根据参数字段的ASCII码值进行排序 加密签名,故使用SortMap进行参数排序
    public static String createSign(String characterEncoding,SortedMap<Object, Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
       // System.out.println(config.getKey());
        sb.append("key=a1c6d88074d94199a1d25c2e80491234");//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
        System.out.println(sb);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }


}