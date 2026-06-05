package com.seekweb4.chat.enumUtil;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author coderpwh
 */

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {


    /**
     * 处理中 - 刚入库，非成功非失败的中间状态
     */
    PROCESSING(0, "处理中"),


    /**
     * 成功
     */
    SUCCESS(1, "成功"),

    /**
     * 失败
     */
    FAIL(2, "失败")


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
    public static PaymentStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
