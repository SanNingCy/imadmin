package com.seekweb4.chat.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author coderpwh
 */
@Data
public class CharacterTagsResponseVO implements Serializable {


    private Long  id;

    private String characterTags;

    private String characterExplanation;

    private Date createTime;

    private Date updateTime;


}
