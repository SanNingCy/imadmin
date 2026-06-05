package com.seekweb4.chat.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * @author: fxq
 * @Date 2025-06-05 18:26
 */
public class RedPacketUtils {
    /**
     * 拆红包核心接口
     *
     * @param amount 钱
     * @param count  数量
     * @return
     */
    public static String unpackRedPacket(String amount, int count) {
        String redPacketAmount = "0";
        try {
            //红包剩下一个
            if (count == 1) {
                redPacketAmount = amount;
            }
            BigDecimal wan = new BigDecimal("100");
            if (count > 1) {
                //红包金额以0.0001为单位, 使用random.nextInt(Integer)，最后在再除以10000
                //这样就可以保证每个人抢到的金额都可以精确到小数点后4位
                Integer redAmount = 0;
                Integer restAmount = new BigDecimal(amount).multiply(wan).intValue();
                Random random = new Random();
                // 随机范围：[1，剩余人均金额的两倍)，左闭右开
                redAmount = random.nextInt(restAmount / count * 2 - 1) + 1;
                //红包数除以10000
                redPacketAmount = new BigDecimal(redAmount).divide(wan, 2, RoundingMode.DOWN).toString();
            }
            return redPacketAmount;
        } catch (Exception e) {
            System.out.println("抢红包发生异常"+e);
            throw e;
        }
    }

//    public static void main(String[] args) {
//        String amount = "10";
//        for (int i = 10; i >0; i--) {
//            String chou = unpackRedPacket(amount, i);
//            amount = new BigDecimal(amount).subtract(new BigDecimal(chou)).toString();
//            System.out.println("抢红包发生异常"+chou);
//        }
//    }
}
