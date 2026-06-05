package com.seekweb4.chat.common.websocket.service.system;

import com.seekweb4.chat.common.websocket.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;

public class SystemInfoSocketHandler implements WebSocketHandler {

    private static final Logger logger;

    private static final ArrayList<WebSocketSession> users;

    static {
        users = new ArrayList<>();
        logger = LoggerFactory.getLogger(SystemInfoSocketHandler.class);
    }
    
    public ArrayList<String> getOnlineLoginNames(){
    	 ArrayList<String> onlineLoginNames = new ArrayList<String>();
    	 for(WebSocketSession user : users){
    		 String userName = (String) user.getAttributes().get(Constants.WEBSOCKET_LOGINNAME);
    		 if(userName!= null){
    			 onlineLoginNames.add(userName);
    		 }
    	 }
    	 return onlineLoginNames;
         
    }


    //建立websocket连接时触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("connect to the websocket success......");
        users.add(session);
        session.sendMessage(new TextMessage("pong"));
    }

    //接收js侧发送来的用户信息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> socketMessage) throws Exception {
    	String message = socketMessage.getPayload().toString();
        if ("ping".equals(message)) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        logger.debug("websocket connection closed......");
        users.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("websocket connection closed......");
        users.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所用户发送消息
     *
     * @param message
     */
    public static void sendMessageToAllUsers(String message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param loginName
     * @param message
     */
    public static boolean sendMessageToUser(String loginName, String message) {
    	boolean result = false;
        for (WebSocketSession user : users) {
            if (loginName.equals(user.getAttributes().get(Constants.WEBSOCKET_LOGINNAME))) {//允许用户多个浏览器登录，每个浏览器页面都会收到用户信息
                try {
                    if (user.isOpen()) {
                        user.sendMessage(new TextMessage(message));
                        result = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //break;//注释掉此处意味着遍历该用户打开的所有页面并发送信息，否则只会向用户登录的第一个网页发送信息。
            }
        }
        logger.debug("发送用户:{},发送内容:{},发送结果:{}", loginName, message, result);
        return result;
    }
    
}