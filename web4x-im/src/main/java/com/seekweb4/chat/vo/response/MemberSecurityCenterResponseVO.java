package com.seekweb4.chat.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author coderpwh
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MemberSecurityCenterResponseVO implements Serializable {


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


    /***
     * 谷歌验证码时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date twoFaceTime;


}
