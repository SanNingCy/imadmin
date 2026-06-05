package com.seekweb4.chat.modules.roomgift.dao;

import lombok.Data;

import java.util.List;

/**
 * 礼物配置响应DTO
 * @author lixinapp
 * @version 2024-10-16
 */
@Data
public class GiftResponseDto {
    /**
     * 礼物列表
     */
    private List<GiftDto> rewardGifts;

    /**
     * 礼物规则说明
     */
    private String giftGuidelines;

    /**
     * 是否免密支付
     */
    private Boolean isNonPayPwd;

    @Data
    public static class GiftDto {
        private String id;

        /**
         * 礼物图片
         */
        private String img;

        /**
         * 礼物名称
         */
        private String name;

        /**
         * 礼物价值（免费或代币数量）
         */
        private Object value;

        public GiftDto() {}

        public GiftDto(String img, String name, Object value) {
            this.img = img;
            this.name = name;
            this.value = value;
        }
    }
}
