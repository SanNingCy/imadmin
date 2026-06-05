package com.seekweb4.chat.enumUtil;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author coderpwh
 */
@Getter
@AllArgsConstructor

public enum CoinTypeEnum {

    /**
     * 积分
     */
    POINTS(1, "积分"),

    /**
     * 代币
     */
    TOKEN(2, "代币");

    private final Integer code;

    @JsonValue
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static CoinTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CoinTypeEnum coinTypeEnum : values()) {
            if (coinTypeEnum.getCode().equals(code)) {
                return coinTypeEnum;
            }
        }
        return null;
    }
}
