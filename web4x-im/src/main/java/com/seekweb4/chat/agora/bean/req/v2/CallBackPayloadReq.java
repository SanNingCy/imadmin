package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;

@Data
public class CallBackPayloadReq {
    //回调房间名称
    private String account;

    private String channelName;

    private Integer platform;

    private Integer reason;

    private Integer ts;
    //回调用户ID
    private Integer uid;
}
