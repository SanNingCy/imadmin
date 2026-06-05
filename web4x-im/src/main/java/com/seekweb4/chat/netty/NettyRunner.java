package com.seekweb4.chat.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;

@Component
@Slf4j
public class NettyRunner implements CommandLineRunner {
    @Resource
    private NettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        // 使用Netty服务时需要放开注释
        /*ThreadUtil.execute(() -> {
            nettyServer.start();
        });*/
    }
}
