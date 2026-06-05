package com.seekweb4.chat.agora.bean.req;

import lombok.Data;

import java.util.List;

@Data
public class BanRuleUpdateReq {
    
    private String appid;
    
    private String cname;
    
    private Long uid;
    
    private String ip;
    
    private Integer time;
    
    private Integer timeInSeconds;
    
    private List<String> privileges;
}