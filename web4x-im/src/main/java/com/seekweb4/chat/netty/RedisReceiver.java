package com.seekweb4.chat.netty;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisReceiver {
    public void receiveMessage(String message) {
        log.debug("消息来了：" + message);
        try {
            JSONObject json = JSONObject.parseObject(message);
            String id = json.getString("id");
            Channel channel = ChannelMap.getChannelById(id);
            if (channel != null) {
                NettyServerHandler.channelWrite(channel, json.getString("data"));
            }
        } catch (Exception e) {
            log.debug("", e);
        }
    }
}
