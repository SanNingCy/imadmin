package com.seekweb4.chat.modules.vipcode.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 会员码同步到链桥 - 入参（仅含 data，sign 由后端自动生成）
 */
@Data
public class SyncFromImRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待同步的会员码列表 */
    private List<SyncFromImItemDTO> data;
}
