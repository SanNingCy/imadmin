package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

@Data
public class UserStatusResponseDto {
    
    private Boolean success;
    
    private UserStatusDataDto data;
}