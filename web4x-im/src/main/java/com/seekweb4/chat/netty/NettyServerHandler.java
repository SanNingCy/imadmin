package com.seekweb4.chat.netty;

import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * netty服务端处理类
 */
@Slf4j
@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    public NettyServerHandler() {

    }
    /**
     * 功能描述: 有客户端连接服务器会触发此函数
     * @param  ctx 通道
     * @return void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();
        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();
        log.info("客户端:{},[IP:{}-->PORT:{}]连接netty服务器",channelId, clientIp, clientPort);
        //如果map中不包含此连接，就保存连接
        if (ChannelMap.getChannelMap().containsKey(channelId)) {
            log.info("客户端:{},是连接状态，连接通道数量:{} ", channelId, ChannelMap.getChannelMap().size());
        } else {
            //保存连接
            ChannelMap.addChannel(channelId, ctx.channel());
            log.info("客户端:{},连接netty服务器[IP:{}-->PORT:{}]",channelId, clientIp, clientPort);
            log.info("连接通道数量: {}",ChannelMap.getChannelMap().size());
        }
    }
    /**
     * 功能描述: 有客户端终止连接服务器会触发此函数
     * @param  ctx 通道处理程序上下文
     * @return void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        log.info("客户端:{},[IP:{}-->PORT:{}]断开连接",channelId, clientIp,inSocket.getPort());
        //包含此客户端才去删除
        if (ChannelMap.getChannelMap().containsKey(channelId)) {
            //删除连接
            ChannelMap.removeChannelByName(channelId);
            //log.info("连接通道数量: " + ChannelMap.getChannelMap().size());
        }
    }
    /**
     * 功能描述: 有客户端发消息会触发此函数
     * @param  ctx 通道处理程序上下文
     * @param  msg 客户端发送的消息
     * @return void
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("加载客户端报文,客户端id:{},客户端消息:{}",ctx.channel().id(), msg);
        // 处理报文
        String result = handle(String.valueOf(msg), ctx);
        if (StringUtils.isNotBlank(result)) {
            //响应客户端
            this.channelWrite(ctx.channel(), result);
        }
    }

    /**
     * 功能描述: 服务端给客户端发送消息
     * @param  channelId 连接通道唯一id
     * @param  msg 需要发送的消息内容
     * @return void
     */
    public static void channelWrite(ChannelId channelId, String msg) {
        channelWrite(ChannelMap.getChannelMap().get(channelId), msg);
    }
    /**
     * 功能描述: 服务端给客户端发送消息
     * @param  channel 连接通道
     * @param  msg 需要发送的消息内容
     * @return void
     */
    public static void channelWrite(Channel channel, String msg) {
        if (channel == null) {
            log.info("通道不存在");
            return;
        }
        if (StringUtils.isBlank(msg)) {
            log.info("服务端响应空的消息");
            return;
        }
        log.info("服务器发送报文,客户端id:{},消息:{}", channel.id(), msg);
        //将客户端的信息直接返回写入ctx
        channel.write(msg);
        //刷新缓存区
        channel.flush();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Client:{},READER_IDLE 读超时",socketString);
                ctx.disconnect();
                ChannelMap.removeChannelByName(ctx.channel().id());
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Client:{}, WRITER_IDLE 写超时",socketString);
                ctx.disconnect();
                ChannelMap.removeChannelByName(ctx.channel().id());
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("Client:{},ALL_IDLE 总超时",socketString);
                ctx.disconnect();
                ChannelMap.removeChannelByName(ctx.channel().id());
            }
        }
    }
    /**
     * 功能描述: 发生异常会触发此函数
     * @param  ctx 通道处理程序上下文
     * @param  cause 异常
     * @return void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        ChannelMap.removeChannelByName(ctx.channel().id());
        log.info("{}:发生了错误,此连接被关闭。{}", ctx.channel().id(), cause.toString());
    }

    /**
     * 处理报文
     * @param data
     * @param ctx
     * @return
     */
    private String handle(String data, ChannelHandlerContext ctx) {
        String result = "";// 响应报文
        if (StringUtils.startsWithIgnoreCase(data, "68")) {// 根据协议规则处理指定头报文
            // 获取设备编码加入clientMap
            ChannelMap.addClient("aaa", ctx.channel().id());
        } else {// 非协议数据，关闭连接
            exceptionCaught(ctx, new Throwable("非法请求"));
        }
        return result;
    }
    /**
     * 向指定设备编号发送数据
     * @param id
     * @param data
     * @return
     */
    public static boolean send(String id, String data) {
        // 发送redis订阅消息
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("data", data);
        StringRedisUtils.getInstance().convertAndSend(NettyProperties.Topic, json.toString());
        return true;
    }
}
