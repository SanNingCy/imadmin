package com.seekweb4.chat.modules.vipcode.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员码同步到链桥 - 签名块（与链桥 syncFromIm 入参 sign 一致）
 */
@Data
public class SyncFromImSignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;
    private String nonce;
    private String sign;
    private Long timestamp;
}
