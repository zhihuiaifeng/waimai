package com.XZYHOrderingFood.back.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
 
import lombok.extern.slf4j.Slf4j;



/**
 * ssl通信的client
 */
@Slf4j
public class HttpClientUtils {
 
    private static PoolingHttpClientConnectionManager secureConnectionManager;
    private static HttpClientBuilder secureHttpBulder = null;
    private static RequestConfig requestConfig = null;
    private static int MAXCONNECTION = 10;
    private static int DEFAULTMAXCONNECTION = 5;
   
    
    static {
        //设置http的状态参数
    	//120秒超时
        requestConfig = RequestConfig.custom().setAuthenticationEnabled(true)
        		.setCircularRedirectsAllowed(true)
        		.setRedirectsEnabled(true)
        		.setRelativeRedirectsAllowed(true)
                .setSocketTimeout(120000)
                .setConnectTimeout(120000)
                .setConnectionRequestTimeout(120000)
                .build();
 
        try {

        	
            ConnectionSocketFactory plainSocketFactory = new PlainConnectionSocketFactory();
            
           
            
//            SSLConnectionSocketFactory sslSocketFactoy = new SSLConnectionSocketFactory(sslContext,
//                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            
         
            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", plainSocketFactory)
           
                    .build();
 
            secureConnectionManager = new PoolingHttpClientConnectionManager(r);
           
            secureConnectionManager.setMaxTotal(MAXCONNECTION);
            //设置每个Route的连接最大数
            secureConnectionManager.setDefaultMaxPerRoute(DEFAULTMAXCONNECTION);
           
            secureHttpBulder = HttpClients.custom().setConnectionManager(secureConnectionManager);
                        
        } catch (Exception e) {
        	throw new Error("Failed to initialize the server-side SSLContext", e);
        }
    }
 
    public static CloseableHttpClient getSecureConnection() throws Exception {
        return secureHttpBulder.build();
    }
 
 
    public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> e : entrySet) {
            String name = e.getKey();
            String value = e.getValue();
            NameValuePair pair = new BasicNameValuePair(name, value);
            params.add(pair);
        }
        HttpUriRequest reqMethod = null;
        if ("post".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        } else if ("get".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        }
        return reqMethod;
    }
    
    
    /**发送get请求*/
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();// 打开和URL之间的连接
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
        
    /**
     * 发送post请求
     * @param url
     * @param map
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
	public static String doBodyPost(String url, Map<String, String> map) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		//装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(map!=null){
            for (Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		
		// 取得HTTP response
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpPost);
		// 若状态码为200 ok
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// 取出回应字串
			return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		} else {
			throw new RuntimeException("doPost Error Response: " + httpResponse.getStatusLine().toString());
		}
	}
}
