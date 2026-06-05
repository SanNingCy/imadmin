package com.seekweb4.chat.modules.creditScore.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信用分计算所需信息（VIP/靓号/靓号标识）
 */
@Data
public class CreditScoreMemberInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户idno
     */
    private String idno;

    /**
     * 用户靓号
     */
    private String lianghao;

    /**
     * 是否会员（项目里通常为字符串：1/0）
     */
    private String isvip;
}

