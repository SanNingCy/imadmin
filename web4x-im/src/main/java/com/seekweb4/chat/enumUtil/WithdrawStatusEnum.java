package com.seekweb4.chat.enumUtil;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author coderpwh
 */

@Getter
@AllArgsConstructor
public enum WithdrawStatusEnum {

    /**
     * 发起提现
     */
    INITIATED(0, "发起提现"),

    /**
     * 正在提现
     */
    PROCESSING(1, "正在提现"),

    /**
     * 提现成功
     */
    SUCCESS(2, "提现成功"),

    /**
     * 提现失败
     */
    FAIL(3, "提现失败"),

    APPLY(4, "申请中"),

    REJECT(5, "拒绝提现"),
    ;

    private final Integer code;

    @JsonValue
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static WithdrawStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WithdrawStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
