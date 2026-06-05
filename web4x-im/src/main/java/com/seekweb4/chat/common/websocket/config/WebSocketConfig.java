package com.seekweb4.chat.common.websocket.config;

import com.seekweb4.chat.common.websocket.service.system.SystemInfoSocketHandler;
import com.seekweb4.chat.common.websocket.service.system.SystemInfoSocketHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
      //注册 系统通知socket服务
        registry.addHandler(systemInfoSocketHandler(),"/systemInfoSocketServer").addInterceptors(new SystemInfoSocketHandshakeInterceptor());
        registry.addHandler(systemInfoSocketHandler(), "/sockjs/systemInfoSocketServer").addInterceptors(new SystemInfoSocketHandshakeInterceptor())
                .withSockJS();
    }


    @Bean
    public WebSocketHandler systemInfoSocketHandler(){
        return new SystemInfoSocketHandler();
    }
}