package com.seekweb4.chat.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
public class FriendshipPageResponseVO implements Serializable {


    private String id;

    private String first;

    private String userId;

    private String icon;

    private String nickName;

    private String sex;

    private String remarkName;

    private String city;

    private Long days;

    private Long characterTagsId=0L;

    private String characterTags;

    private String profession;

    private Integer grade=100;


}
