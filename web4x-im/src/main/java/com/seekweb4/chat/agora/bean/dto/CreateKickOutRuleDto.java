package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateKickOutRuleDto {
    private String status;
    private String id;
}
