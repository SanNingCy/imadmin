package com.seekweb4.chat.asset.vo.request;

import lombok.Data;
import lombok.NonNull;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class UserInfoRequestVO implements Serializable {


    @NotBlank(message = "接收地址不能为空")
    private String receivingAddress;

}
