package com.seekweb4.chat.netty;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class NettyProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    @Value("${socket.port}")
    private Integer port;
    @Value("${socket.poolMax}")
    private Integer poolMax;

    public static final String Topic = "netty";
}
