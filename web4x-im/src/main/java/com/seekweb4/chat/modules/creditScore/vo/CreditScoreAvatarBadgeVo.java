package com.seekweb4.chat.modules.creditScore.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 头像信用分角标解析结果（供客户端渲染底色）
 */
@Data
public class CreditScoreAvatarBadgeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * NEW_USER：新用户阶段；REGULAR：已超过配置天数
     */
    private String phase;

    /**
     * 当前应使用的角标底色（与 phase 对应）
     */
    private String badgeBg;

    /**
     * 配置中的阈值天数（便于客户端展示或缓存校验）
     */
    private Integer newUserDays;

    /**
     * 自注册日起至今日（按 Asia/Shanghai 自然日）经过的天数；无注册时间时为 null
     */
    private Long daysSinceRegister;
}
