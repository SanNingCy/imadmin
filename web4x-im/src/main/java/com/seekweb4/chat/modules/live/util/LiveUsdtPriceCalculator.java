package com.seekweb4.chat.modules.live.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会议室 USDT 定价：1 USDT = 1000 分钟通话时长。
 * 成本价 = 会议时长(分钟) × 人数 / 1000
 */
public final class LiveUsdtPriceCalculator {

    public static final BigDecimal MINUTES_PER_USDT = new BigDecimal("1000");

    private LiveUsdtPriceCalculator() {
    }

    public static BigDecimal calcCostPriceUsdt(int durationMinutes, int peopleCount) {
        if (durationMinutes <= 0 || peopleCount <= 0) {
            throw new IllegalArgumentException("会议时长与人数必须大于0");
        }
        return BigDecimal.valueOf(durationMinutes)
                .multiply(BigDecimal.valueOf(peopleCount))
                .divide(MINUTES_PER_USDT, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcProfitUsdt(BigDecimal salePriceUsdt, BigDecimal costPriceUsdt) {
        if (salePriceUsdt == null || costPriceUsdt == null) {
            throw new IllegalArgumentException("销售价与成本价不能为空");
        }
        return salePriceUsdt.subtract(costPriceUsdt).setScale(4, RoundingMode.HALF_UP);
    }
}
