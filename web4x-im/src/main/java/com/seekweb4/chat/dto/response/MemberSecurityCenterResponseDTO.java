package com.seekweb4.chat.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class MemberSecurityCenterResponseDTO implements Serializable {

    /***
     * 支付密码
     */
    private String payPassword;

    /***
     * 谷歌验证码
     */
    private String twoFactorCode;

    /***
     * 密保
     */
    private String securityId;

}
