package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreAvatarDisplayConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreAvatarDisplayConfig;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreAvatarBadgeVo;

public interface CreditScoreAvatarDisplayConfigService {

    Page<CreditScoreAvatarDisplayConfig> page(CreditScoreAvatarDisplayConfigQueryDto queryDto);

    /**
     * 库中最新一条有效配置；若无记录则返回 null（调用方可使用 {@link #getEffectiveCurrent()}）
     */
    CreditScoreAvatarDisplayConfig getCurrent();

    /**
     * 当前生效配置：无库表记录时使用内置默认值（不改变库）
     */
    CreditScoreAvatarDisplayConfig getEffectiveCurrent();

    CreditScoreAvatarDisplayConfig getById(Long id);

    boolean save(CreditScoreAvatarDisplayConfig config);

    boolean update(CreditScoreAvatarDisplayConfig config);

    boolean remove(Long id, String updateBy);

    /**
     * 根据用户注册时间与当前配置解析头像信用分角标底色
     */
    CreditScoreAvatarBadgeVo resolveBadgeForUser(String userId);
}
