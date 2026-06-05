package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

@Data
public class UserListResponseDto {
    
    private Boolean success;
    
    private UserListDataDto data;
}