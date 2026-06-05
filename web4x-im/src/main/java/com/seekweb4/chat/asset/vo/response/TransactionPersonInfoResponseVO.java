package com.seekweb4.chat.asset.vo.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data

public class TransactionPersonInfoResponseVO implements Serializable {

    private String userName;

    private String uid;

    private String userId;

    @JSONField(serialize = true)
    private String icon;

}
