package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 会议室时长/人数档位下拉项：主键 id + 展示名称 + 业务数值（分钟或人数上限）
 */
@Data
public class LiveConfigSelectOptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    /** 时长为分钟；人数档位为人数上限 */
    private Integer value;
}
