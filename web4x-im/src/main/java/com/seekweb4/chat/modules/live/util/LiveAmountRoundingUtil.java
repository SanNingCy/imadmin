package com.seekweb4.chat.modules.live.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LiveAmountRoundingUtil {

    private LiveAmountRoundingUtil() {
    }

    /**
     * roundingRule 支持：
     * - null/空：HALF_UP(2)
     * - "HALF_UP" / "HALF_DOWN" / "UP" / "DOWN" / "CEILING" / "FLOOR"
     */
    public static BigDecimal round(BigDecimal amount, String roundingRule) {
        if (amount == null) {
            return null;
        }
        String rule = roundingRule == null ? "" : roundingRule.trim().toUpperCase();
        RoundingMode mode;
        switch (rule) {
            case "HALF_DOWN":
                mode = RoundingMode.HALF_DOWN;
                break;
            case "UP":
                mode = RoundingMode.UP;
                break;
            case "DOWN":
                mode = RoundingMode.DOWN;
                break;
            case "CEILING":
                mode = RoundingMode.CEILING;
                break;
            case "FLOOR":
                mode = RoundingMode.FLOOR;
                break;
            case "HALF_UP":
            default:
                mode = RoundingMode.HALF_UP;
                break;
        }
        return amount.setScale(2, mode);
    }
}

