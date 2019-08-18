package com.XZYHOrderingFood.back.weixin.wxpay;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import com.XZYHOrderingFood.back.weixin.wxsdk.IWXPayDomain;
import com.XZYHOrderingFood.back.weixin.wxsdk.WXPayConfig;
import com.XZYHOrderingFood.back.weixin.wxtest.WXPayDomainSimpleImpl;
 


public class WXPayConfigImpl extends WXPayConfig implements com.github.wxpay.sdk.WXPayConfig {

    private byte[] certData;
    private volatile static WXPayConfigImpl INSTANCE;

    private WXPayConfigImpl() throws Exception{
        String certPath = "cert_h5/apiclient_cert.p12";
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(certPath);
        File file = new File(url.getFile());
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static WXPayConfigImpl getInstance() throws Exception{
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }
    
    public String getAppID() {
    	return "wx9aca515824006c60";
    }

    public String getMchID() {
    	return "1483885202";
    }

    public String getKey() {
    	return "7a64f525b6e545139d073674b8e8de36";
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    protected IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }
}
