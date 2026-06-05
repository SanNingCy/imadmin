package com.seekweb4.chat.vo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class FriendshipPageRequestVO implements Serializable {


    private String remarkName;


    private Long characterTagsId;

    private String userId;




}
