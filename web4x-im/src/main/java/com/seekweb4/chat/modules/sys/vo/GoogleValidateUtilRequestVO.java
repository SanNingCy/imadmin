package com.seekweb4.chat.modules.sys.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class GoogleValidateUtilRequestVO implements Serializable {


    @NotBlank(message = "验证码不能为空")
    private String inputGoogleCode;



}
