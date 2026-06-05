package com.seekweb4.chat.asset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class AddressKeyVO implements Serializable {


    @ApiModelProperty("钱包地址")
    private String address;


    @ApiModelProperty("钱包私钥")
    private String key;


}
