package com.XZYHOrderingFood.back.weixin.wxtest;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TestWXPay {

    private WXPay wxpay;
    private WXPayConfigImpl config;
    private String out_trade_no;
    private String total_fee;

    public TestWXPay() throws Exception {
        config = WXPayConfigImpl.getInstance();
        wxpay = new WXPay(config);
        total_fee = "1";
        // out_trade_no = "201701017496748980290321";
        out_trade_no = "20170101749674898029032125";
    }

    /**
     * 扫码支付  下单
     */
    public void doUnifiedOrder() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("body", "途客思支付押金");
        data.put("out_trade_no", out_trade_no);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", "100");
        data.put("sub_mch_id", "1487688232");
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", "http://test.letiantian.me/wxpay/notify");
        data.put("trade_type", "NATIVE");
        data.put("product_id", "12");
        // data.put("time_expire", "20170112104120");

        try {
            Map<String, String> r = wxpay.unifiedOrder(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * app支付
     */
    public void doUnifiedOrderapp() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("body", "番茄出行支付押金");
        data.put("out_trade_no", out_trade_no);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");
        data.put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM69DS");
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", "http://test.letiantian.me/wxpay/notify");
        data.put("trade_type", "APP");
        data.put("product_id", "12");
        //data.put("appid", "wx6e67a1fe27f6051d");
        //data.put("mch_id", "1496391772");
       //  data.put("time_expire", "20170112104120");
        long timeMillis = System.currentTimeMillis();

        //String timestamp = "2147483647";

        try {
            Map<String, String> r = wxpay.unifiedOrder(data);
            SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
            parameters.put("appid",r.get("appid"));
            parameters.put("noncestr",r.get("nonce_str")); 
            parameters.put("package","Sign=WXPay");
            parameters.put("partnerid",r.get("mch_id"));
            parameters.put("prepayid",r.get("prepay_id"));
            parameters.put("timestamp",timeMillis);
            String sign = PayCommonUtil.createSign("UTF-8",parameters);

            r.put("sign",sign);
            //System.out.println(sign);
            //System.out.println(r);
            System.out.println("调起支付参数："+parameters);
            System.out.println("签名："+sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void doOrderClose() {
        System.out.println("关闭订单");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
        try {
            Map<String, String> r = wxpay.closeOrder(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doOrderQuery() {
        System.out.println("查询订单");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
       // data.put("sub_mch_id", "1487688232");
//        data.put("transaction_id", "4008852001201608221962061594");
        try {
            Map<String, String> r = wxpay.orderQuery(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doOrderReverse() {
        System.out.println("撤销");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
       // data.put("sub_mch_id", "1487688232");
//        data.put("transaction_id", "4008852001201608221962061594");
        try {
            Map<String, String> r = wxpay.reverse(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 长链接转短链接
     * 测试成功
     */
    public void doShortUrl() {
        String long_url = "weixin://wxpay/bizpayurl?pr=etxB4DY";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("long_url", long_url);
        try {
            Map<String, String> r = wxpay.shortUrl(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退款
     * 已测试
     */
    public void doRefund() {
        HashMap<String, String> data = new HashMap<String, String>();
       /* data.put("out_trade_no", out_trade_no);
        data.put("out_refund_no", out_trade_no);
        data.put("total_fee", total_fee);
        data.put("refund_fee", total_fee);
        data.put("refund_fee_type", "CNY");
        data.put("op_user_id", config.getMchID());*/
        data.put("out_trade_no", "20171113162223002");
        data.put("out_refund_no", "20171113162223003");
        data.put("total_fee", total_fee);
        data.put("refund_fee", total_fee);
        data.put("refund_fee_type", "CNY");
        data.put("op_user_id", config.getMchID());
        System.out.println(data);

        try {
            Map<String, String> r = wxpay.refund(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询退款
     * 已经测试
     */
    public void doRefundQuery() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_refund_no", out_trade_no);
        try {
            Map<String, String> r = wxpay.refundQuery(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对账单下载
     * 已测试
     */
    public void doDownloadBill() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("bill_date", "20161102");
        data.put("bill_type", "ALL");
        try {
            Map<String, String> r = wxpay.downloadBill(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doGetSandboxSignKey() throws Exception {
        WXPayConfigImpl config = WXPayConfigImpl.getInstance();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("mch_id", config.getMchID());
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        String sign = WXPayUtil.generateSignature(data, config.getKey());
        data.put("sign", sign);
        WXPay wxPay = new WXPay(config);
        String result = wxPay.requestWithoutCert("https://api.mch.weixin.qq.com/sandbox/pay/getsignkey", data, 10000, 10000);
        System.out.println(result);
    }


//    public void doReport() {
//        HashMap<String, String> data = new HashMap<String, String>();
//        data.put("interface_url", "20160822");
//        data.put("bill_type", "ALL");
//    }

    /**
     * 小测试
     */
    public void test001() {
        String xmlStr="<xml><return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<return_msg><![CDATA[OK]]></return_msg>\n" +
                "<appid><![CDATA[wx273fe72f2db863ed]]></appid>\n" +
                "<mch_id><![CDATA[1228845802]]></mch_id>\n" +
                "<nonce_str><![CDATA[lCXjx3wNx45HfTV2]]></nonce_str>\n" +
                "<sign><![CDATA[68D7573E006F0661FD2A77BA59124E87]]></sign>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<openid><![CDATA[oZyc_uPx_oed7b4q1yKmj_3M2fTU]]></openid>\n" +
                "<is_subscribe><![CDATA[N]]></is_subscribe>\n" +
                "<trade_type><![CDATA[NATIVE]]></trade_type>\n" +
                "<bank_type><![CDATA[CFT]]></bank_type>\n" +
                "<total_fee>1</total_fee>\n" +
                "<fee_type><![CDATA[CNY]]></fee_type>\n" +
                "<transaction_id><![CDATA[4008852001201608221983528929]]></transaction_id>\n" +
                "<out_trade_no><![CDATA[20160822162018]]></out_trade_no>\n" +
                "<attach><![CDATA[]]></attach>\n" +
                "<time_end><![CDATA[20160822202556]]></time_end>\n" +
                "<trade_state><![CDATA[SUCCESS]]></trade_state>\n" +
                "<cash_fee>1</cash_fee>\n" +
                "</xml>";
        try {
            System.out.println(xmlStr);
            System.out.println("+++++++++++++++++");
            System.out.println(WXPayUtil.isSignatureValid(xmlStr, config.getKey()));
            Map<String, String> hm = WXPayUtil.xmlToMap(xmlStr);
            System.out.println("+++++++++++++++++");
            System.out.println(hm);
            System.out.println(hm.get("attach").length());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testUnifiedOrderSpeed() throws Exception {
        TestWXPay dodo = new TestWXPay();

        for (int i=0; i<100; ++i) {
            long startTs = System.currentTimeMillis();
            out_trade_no = out_trade_no+i;
            dodo.doUnifiedOrder();
            long endTs = System.currentTimeMillis();
            System.out.println(endTs-startTs);
            Thread.sleep(1000);
        }

    }

   /* public static void main(String[] args) throws Exception {
        System.out.println("--------------->");
        TestWXPay dodo = new TestWXPay();
        dodo.doUnifiedOrderapp();
        // dodo.doGetSandboxSignKey();

        //dodo.doRefund();
        // dodo.doOrderQuery();
        // dodo.doDownloadBill();
        // dodo.doShortUrl();
        // dodo.test001();
        // dodo.doOrderQuery();
        // dodo.doOrderClose();
        // dodo.doRefund();
        // dodo.doRefundQuery();
        // dodo.doOrderReverse();
        // dodo.test001();
        // dodo.testUnifiedOrderSpeed();

        // dodo.doOrderQuery();
          dodo.doOrderReverse();
        dodo.doOrderQuery();
        dodo.doOrderReverse();
        dodo.doOrderQuery();



        System.out.println("<---------------"); // wx2016112510573077
    }*/
}
