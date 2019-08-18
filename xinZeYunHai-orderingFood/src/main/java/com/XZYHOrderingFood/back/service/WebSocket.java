package com.XZYHOrderingFood.back.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.redis.RedisUtils;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Class:
 * @Description: 简单websocket demo
 * @author zpy
 */
@ServerEndpoint(value="/websocket/{userid_tablenum}/{openid}")
@Component
@Slf4j
public class WebSocket {
    private Logger logger = LoggerFactory.getLogger(WebSocket.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收商户id和桌号
    private String userid_tablenum="";

    //接收openid
    private String openid="";


    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocket.applicationContext = applicationContext;
    }

//    private static ApplicationContext applicationContext;
//
//    public static void setApplicationContext(ApplicationContext context) {
//        applicationContext = context;
//    }


//    @Autowired
//    private RedisUtils redisUtils;

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userid_tablenum") String userid_tablenum, @PathParam("openid") String openid){
        if ("undefined".equals(openid) || "undefinedundefined".equals(userid_tablenum)) {
            this.onClose();
        }
        this.session = session;
        webSocketSet.add(this);     //加入set中
        this.userid_tablenum=userid_tablenum;
        this.openid = openid;
        // 第一次登陆
        RedisUtils redisUtils = applicationContext.getBean(RedisUtils.class);
        ProductListService productListService = applicationContext.getBean(ProductListService.class);
//            productListService.sendAllInfoByUser();
        Map<String, List<Map<String, Integer>>> map = null;
        map = (Map<String, List<Map<String, Integer>>>) redisUtils.get(userid_tablenum);
        if (map != null) {
//            ObjectMapper json = new ObjectMapper();
//            String names = JSONUtils.toJSONString(map);
            String name = productListService.getStringByMap(map);
            try {
//                String names = json.(map);
                this.sendMessage(name);
            } catch (Exception e) {
                throw new CustomException(PublicDictUtil.ERROR_VALUE, "同步消息失败");
            }
        }
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("来自客户端的消息:" + message);
//        //群发消息
//        for(WebSocket item: webSocketSet){
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//                continue;
//            }
//        }
//    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发 首次登陆 将菜单全部推送
     */
//    public static void sendAllShopCart(String message, String userid_tablenum, String openid, String flag ) throws IOException {
//        for (WebSocket item : webSocketSet) {
//            try {
//                //这里可以设定只推送给这个sid的，为null则全部推送
//                if(item.openid.equals(openid)) {
//                    item.sendMessage(message);
//                }
//            } catch (IOException e) {
//                continue;
//            }
//        }
//    }

    /**
     * 群发 有人加餐全部提醒
     */
    public static void sendInfo(List list, String userid_tablenum, String openid) throws IOException {
        for (WebSocket item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if (list != null) {
                    String names = JSONUtils.toJSONString(list);
                    if (userid_tablenum.equals(item.userid_tablenum)) {
                        if (!openid.equals(item.openid)) {
                            item.sendMessage(names);
                        }
                    }
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }
}