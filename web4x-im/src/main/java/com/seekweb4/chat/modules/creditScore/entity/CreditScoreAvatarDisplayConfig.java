package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 信用分相关全局展示配置（单表取最新一条有效记录），表 t_credit_score_avatar_display_config
 */
@Data
public class CreditScoreAvatarDisplayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    /**
     * 是否删除（0:否1:是）
     */
    private Integer isDeleted;

    /**
     * 注册后未满该自然日天数则使用新用户角标底色
     */
    private Integer newUserDays;

    /**
     * 新用户注册天数(备用)
     */
    private Integer newUserDaysV2;

    /**
     * 新入群天数阈值（全局）：入群未满该自然日天数视为「新入群」等场景，默认 30
     */
    private Integer newInGroupDays;

    /**
     * 新用户信用分角标底色（如 #E53935）
     */
    private String badgeBgNewUser;

    /**
     * 超过阈值后信用分角标底色（如 #7B1FA2）
     */
    private String badgeBgRegular;

    /**
     * 是否靓号配置（1:是 0:否）
     */
    private Integer isLianghao;

    /**
     * 是否会员配置（1:是 0:否）
     */
    private Integer isVip;

    /**
     * 新用户加入天数（业务配置，可为空表示未配置）
     */
    private Integer newUserJoinDays;

    /**
     * 加好友申请次数（业务配置，可为空表示未配置）
     */
    private Integer friendApplyCount;

    /**
     * 新人建群天数（库表 NOT NULL DEFAULT 30）
     */
    private Integer newUserCreateGroupDays;

    /**
     * 新人建群人数（库表 NOT NULL DEFAULT 10）
     */
    private Integer newUserCreateGroupMemberCount;

    /**
     * 建群数量（库表 NOT NULL DEFAULT 1）
     */
    private Integer createGroupCount;

    /**
     * 登录二次验证至少通过几项(仅计用户已启用项); 1=任选其一; 2=至少两项; 0=全部验证
     */
    private Integer loginSecondVerifyMinPass;

    /**
     * 扫码登录校验 IM 验证码：1 开 0 关
     */
    private Integer scanLoginCheckImCode;
}
