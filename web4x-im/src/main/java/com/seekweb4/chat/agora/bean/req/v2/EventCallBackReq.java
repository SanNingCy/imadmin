package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class EventCallBackReq {
    //事件通知ID
    private String noticeId;
    //产品代码
    private Integer productId;
    //事件类型 104 主播退出房间
    private Integer eventType;
    //通知时间
    private Long notifyMs;
    //事件负载
    private Map<String,Object> payload;
}
