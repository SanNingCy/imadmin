package com.seekweb4.chat.modules.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class GoogleCodeUtilResponseVO implements Serializable {


    /**
     * 随机秘钥
     */
    private String randomSecretKey;

    /**
     * 二维码图片base64
     */
    private String qrCodeImageBase64;


}
