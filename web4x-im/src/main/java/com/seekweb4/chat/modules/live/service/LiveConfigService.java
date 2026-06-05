package com.seekweb4.chat.modules.live.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveConfigSelectOptionVo;
import com.seekweb4.chat.modules.live.dto.LiveTimeDurationConfigQueryDto;
import com.seekweb4.chat.modules.live.dto.LiveUserTierConfigQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;

import java.util.List;

public interface LiveConfigService {

    Page<LiveTimeDurationConfig> pageDuration(LiveTimeDurationConfigQueryDto queryDto);

    LiveTimeDurationConfig getDurationById(Long id);

    boolean createDuration(LiveTimeDurationConfig config);

    boolean updateDuration(LiveTimeDurationConfig config);

    boolean deleteDuration(Long id, String updateBy);

    List<LiveTimeDurationConfig> listEnabledDurations();

    /** 未删除的时长下拉（id + 名称 + 分钟值），含禁用项 */
    List<LiveConfigSelectOptionVo> listDurationSelectOptions();

    Page<LiveUserTierConfig> pageTier(LiveUserTierConfigQueryDto queryDto);

    LiveUserTierConfig getTierById(Long id);

    boolean createTier(LiveUserTierConfig config);

    boolean updateTier(LiveUserTierConfig config);

    boolean deleteTier(Long id, String updateBy);

    List<LiveUserTierConfig> listEnabledTiers();

    /** 未删除的人数档位下拉（id + 名称 + 人数上限），含禁用项 */
    List<LiveConfigSelectOptionVo> listTierSelectOptions();
}

