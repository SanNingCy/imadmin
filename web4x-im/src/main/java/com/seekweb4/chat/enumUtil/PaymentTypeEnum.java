package com.seekweb4.chat.enumUtil;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author coderpwh
 */
@Getter
@AllArgsConstructor
public enum PaymentTypeEnum {


    /**
     * 入金wx
     */
    DEPOSIT_WX(1, "入金wx"),

    /**
     * 提现
     */
    WITHDRAW(2, "提现");

    private final Integer code;

    @JsonValue
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static PaymentTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
