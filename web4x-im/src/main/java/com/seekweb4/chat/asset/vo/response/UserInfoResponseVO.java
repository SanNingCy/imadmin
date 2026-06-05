package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class UserInfoResponseVO implements Serializable {


    private  String userId;

    private  String userName;

    private  String receivingAddress;

}
