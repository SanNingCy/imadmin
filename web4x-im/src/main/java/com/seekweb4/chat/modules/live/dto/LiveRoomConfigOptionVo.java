package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

/**
 * 下拉选项通用结构
 */
@Data
public class LiveRoomConfigOptionVo {
    private String name;
    private Integer value;

    public LiveRoomConfigOptionVo() {
    }

    public LiveRoomConfigOptionVo(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}

