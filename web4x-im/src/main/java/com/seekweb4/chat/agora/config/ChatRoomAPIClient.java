package com.seekweb4.chat.agora.config;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import com.seekweb4.chat.agora.service.api.ChatRoomAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ChatRoomAPIClient {

    @Value("${chatRoom.domain}")
    private String domain;

    @Bean
    public ChatRoomAPIService chatRoomAPIService() {
        return Feign.builder()
                .logger(new Slf4jLogger())
                .logLevel(feign.Logger.Level.FULL)
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(ChatRoomAPIService.class, domain);
    }
}
