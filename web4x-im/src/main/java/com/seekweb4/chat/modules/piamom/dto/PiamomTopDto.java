package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;

@Data
public class PiamomTopDto {

    private Long id;
    /** 0取消置顶 1置顶 */
    private Integer isTop;
}
