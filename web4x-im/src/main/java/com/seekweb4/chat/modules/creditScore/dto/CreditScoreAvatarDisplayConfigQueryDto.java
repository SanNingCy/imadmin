package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditScoreAvatarDisplayConfigQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long id;

    private String orderBy;
}
