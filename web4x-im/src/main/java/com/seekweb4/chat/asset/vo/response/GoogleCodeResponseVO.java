package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class GoogleCodeResponseVO implements Serializable {


    /**
     * 随机秘钥
     */
    private String randomSecretKey;

    /**
     * 二维码图片base64
     */
    private String qrCodeImageBase64;


}
