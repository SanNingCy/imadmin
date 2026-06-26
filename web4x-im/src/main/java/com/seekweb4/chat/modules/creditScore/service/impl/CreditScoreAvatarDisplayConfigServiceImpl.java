package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreAvatarDisplayConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreAvatarDisplayConfig;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreAvatarDisplayConfigMapper;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreAvatarDisplayConfigService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreAvatarBadgeVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
@Transactional
public class CreditScoreAvatarDisplayConfigServiceImpl implements CreditScoreAvatarDisplayConfigService {
    private static final Map<String, String> ORDER_BY_COLUMNS = new HashMap<>();
    static {
        ORDER_BY_COLUMNS.put("id", "id");
        ORDER_BY_COLUMNS.put("createTime", "create_time");
        ORDER_BY_COLUMNS.put("updateTime", "update_time");
        ORDER_BY_COLUMNS.put("newUserDays", "new_user_days");
        ORDER_BY_COLUMNS.put("newUserDaysV2", "new_user_days_v2");
        ORDER_BY_COLUMNS.put("newInGroupDays", "new_in_group_days");
        ORDER_BY_COLUMNS.put("badgeBgNewUser", "badge_bg_new_user");
        ORDER_BY_COLUMNS.put("badgeBgRegular", "badge_bg_regular");
        ORDER_BY_COLUMNS.put("isLianghao", "is_lianghao");
        ORDER_BY_COLUMNS.put("isVip", "is_vip");
        ORDER_BY_COLUMNS.put("newUserJoinDays", "new_user_join_days");
        ORDER_BY_COLUMNS.put("friendApplyCount", "friend_apply_count");
        ORDER_BY_COLUMNS.put("newUserCreateGroupDays", "new_user_create_group_days");
        ORDER_BY_COLUMNS.put("newUserCreateGroupMemberCount", "new_user_create_group_member_count");
        ORDER_BY_COLUMNS.put("createGroupCount", "create_group_count");
        ORDER_BY_COLUMNS.put("loginSecondVerifyMinPass", "login_second_verify_min_pass");
        ORDER_BY_COLUMNS.put("scanLoginCheckImCode", "scan_login_check_im_code");
        ORDER_BY_COLUMNS.put("vipRedeemEnabled", "vip_redeem_enabled");
        ORDER_BY_COLUMNS.put("vipPurchaseEnabled", "vip_purchase_enabled");
    }

    private static final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");

    private static final int DEFAULT_NEW_USER_DAYS = 60;
    /** 新入群天数阈值（全局配置默认） */
    private static final int DEFAULT_NEW_IN_GROUP_DAYS = 30;
    /** 与 t_credit_score_avatar_display_config 库 DEFAULT 一致（无记录时的内存默认） */
    private static final int DEFAULT_NEW_USER_CREATE_GROUP_DAYS = 30;
    private static final int DEFAULT_NEW_USER_CREATE_GROUP_MEMBER_COUNT = 10;
    private static final int DEFAULT_CREATE_GROUP_COUNT = 1;
    /** 与库表 DEFAULT 一致 */
    private static final int DEFAULT_LOGIN_SECOND_VERIFY_MIN_PASS = 1;
    private static final int DEFAULT_SCAN_LOGIN_CHECK_IM_CODE = 0;
    private static final int DEFAULT_VIP_SWITCH_ENABLED = 1;
    private static final String DEFAULT_BG_NEW = "#E53935";
    private static final String DEFAULT_BG_REGULAR = "#7B1FA2";

    @Resource
    private CreditScoreAvatarDisplayConfigMapper creditScoreAvatarDisplayConfigMapper;

    @Resource
    private CreditScoreMemberMapper creditScoreMemberMapper;

    @Override
    public Page<CreditScoreAvatarDisplayConfig> page(CreditScoreAvatarDisplayConfigQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        queryDto.setOrderBy(normalizeOrderBy(queryDto.getOrderBy()));
        Page<CreditScoreAvatarDisplayConfig> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        Long count = creditScoreAvatarDisplayConfigMapper.selectAdminCount(queryDto);
        page.setCount(count == null ? 0L : count);
        List<CreditScoreAvatarDisplayConfig> list = creditScoreAvatarDisplayConfigMapper.selectAdminPageList(queryDto);
        page.setList(list);
        return page;
    }

    private String normalizeOrderBy(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(", ");
        String[] items = raw.split(",");
        for (String item : items) {
            if (item == null || item.trim().isEmpty()) {
                continue;
            }
            String[] parts = item.trim().split("\\s+");
            if (parts.length == 0) {
                continue;
            }
            String column = ORDER_BY_COLUMNS.get(parts[0]);
            if (column == null) {
                continue;
            }
            String direction = "desc";
            if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) {
                direction = "asc";
            }
            joiner.add(column + " " + direction);
        }
        return joiner.length() == 0 ? null : joiner.toString();
    }

    @Override
    public CreditScoreAvatarDisplayConfig getCurrent() {
        return creditScoreAvatarDisplayConfigMapper.selectCurrent();
    }

    @Override
    public CreditScoreAvatarDisplayConfig getEffectiveCurrent() {
        CreditScoreAvatarDisplayConfig c = getCurrent();
        if (c == null) {
            return buildDefaultConfigBean();
        }
        if (c.getNewUserDaysV2() == null || c.getNewUserDaysV2() < 1) {
            c.setNewUserDaysV2(DEFAULT_NEW_USER_DAYS);
        }
        if (c.getNewInGroupDays() == null || c.getNewInGroupDays() < 1) {
            c.setNewInGroupDays(DEFAULT_NEW_IN_GROUP_DAYS);
        }
        if (c.getIsLianghao() == null) {
            c.setIsLianghao(0);
        }
        if (c.getIsVip() == null) {
            c.setIsVip(0);
        }
        if (c.getLoginSecondVerifyMinPass() == null) {
            c.setLoginSecondVerifyMinPass(DEFAULT_LOGIN_SECOND_VERIFY_MIN_PASS);
        }
        if (c.getScanLoginCheckImCode() == null) {
            c.setScanLoginCheckImCode(DEFAULT_SCAN_LOGIN_CHECK_IM_CODE);
        }
        if (c.getVipRedeemEnabled() == null) {
            c.setVipRedeemEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        }
        if (c.getVipPurchaseEnabled() == null) {
            c.setVipPurchaseEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        }
        return c;
    }

    @Override
    public CreditScoreAvatarDisplayConfig getById(Long id) {
        return creditScoreAvatarDisplayConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean save(CreditScoreAvatarDisplayConfig config) {
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        if (config.getIsDeleted() == null) {
            config.setIsDeleted(0);
        }
        applyDefaultsForSave(config);
        return creditScoreAvatarDisplayConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean update(CreditScoreAvatarDisplayConfig config) {
        config.setUpdateTime(new Date());
        return creditScoreAvatarDisplayConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean remove(Long id, String updateBy) {
        CreditScoreAvatarDisplayConfig config = new CreditScoreAvatarDisplayConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return creditScoreAvatarDisplayConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public CreditScoreAvatarBadgeVo resolveBadgeForUser(String userId) {
        CreditScoreAvatarDisplayConfig cfg = getEffectiveCurrent();
        CreditScoreAvatarBadgeVo vo = new CreditScoreAvatarBadgeVo();
        int threshold = cfg.getNewUserDays() != null && cfg.getNewUserDays() > 0
                ? cfg.getNewUserDays()
                : (cfg.getNewUserDaysV2() != null && cfg.getNewUserDaysV2() > 0
                ? cfg.getNewUserDaysV2() : DEFAULT_NEW_USER_DAYS);
        vo.setNewUserDays(threshold);

        Date reg = creditScoreMemberMapper.selectMemberCreateDateByUserId(userId);
        if (reg == null) {
            vo.setPhase("REGULAR");
            vo.setBadgeBg(cfg.getBadgeBgRegular() != null ? cfg.getBadgeBgRegular() : DEFAULT_BG_REGULAR);
            vo.setDaysSinceRegister(null);
            return vo;
        }

        LocalDate regDay = reg.toInstant().atZone(ZONE_CN).toLocalDate();
        LocalDate today = LocalDate.now(ZONE_CN);
        long days = ChronoUnit.DAYS.between(regDay, today);
        vo.setDaysSinceRegister(days);

        boolean isNew = days < threshold;
        if (isNew) {
            vo.setPhase("NEW_USER");
            vo.setBadgeBg(cfg.getBadgeBgNewUser() != null ? cfg.getBadgeBgNewUser() : DEFAULT_BG_NEW);
        } else {
            vo.setPhase("REGULAR");
            vo.setBadgeBg(cfg.getBadgeBgRegular() != null ? cfg.getBadgeBgRegular() : DEFAULT_BG_REGULAR);
        }
        return vo;
    }

    private static void applyDefaultsForSave(CreditScoreAvatarDisplayConfig config) {
        if (config.getNewUserDays() == null || config.getNewUserDays() < 1) {
            config.setNewUserDays(DEFAULT_NEW_USER_DAYS);
        }
        if (config.getNewUserDaysV2() == null || config.getNewUserDaysV2() < 1) {
            config.setNewUserDaysV2(DEFAULT_NEW_USER_DAYS);
        }
        if (config.getBadgeBgNewUser() == null || config.getBadgeBgNewUser().trim().isEmpty()) {
            config.setBadgeBgNewUser(DEFAULT_BG_NEW);
        }
        if (config.getBadgeBgRegular() == null || config.getBadgeBgRegular().trim().isEmpty()) {
            config.setBadgeBgRegular(DEFAULT_BG_REGULAR);
        }
        if (config.getNewInGroupDays() == null || config.getNewInGroupDays() < 1) {
            config.setNewInGroupDays(DEFAULT_NEW_IN_GROUP_DAYS);
        }
        if (config.getIsLianghao() == null) {
            config.setIsLianghao(0);
        }
        if (config.getIsVip() == null) {
            config.setIsVip(0);
        }
        if (config.getLoginSecondVerifyMinPass() == null) {
            config.setLoginSecondVerifyMinPass(DEFAULT_LOGIN_SECOND_VERIFY_MIN_PASS);
        }
        if (config.getScanLoginCheckImCode() == null) {
            config.setScanLoginCheckImCode(DEFAULT_SCAN_LOGIN_CHECK_IM_CODE);
        }
        if (config.getVipRedeemEnabled() == null) {
            config.setVipRedeemEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        }
        if (config.getVipPurchaseEnabled() == null) {
            config.setVipPurchaseEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        }
    }

    private static CreditScoreAvatarDisplayConfig buildDefaultConfigBean() {
        CreditScoreAvatarDisplayConfig c = new CreditScoreAvatarDisplayConfig();
        c.setNewUserDays(DEFAULT_NEW_USER_DAYS);
        c.setNewUserDaysV2(DEFAULT_NEW_USER_DAYS);
        c.setNewInGroupDays(DEFAULT_NEW_IN_GROUP_DAYS);
        c.setBadgeBgNewUser(DEFAULT_BG_NEW);
        c.setBadgeBgRegular(DEFAULT_BG_REGULAR);
        c.setIsLianghao(0);
        c.setIsVip(0);
        c.setNewUserCreateGroupDays(DEFAULT_NEW_USER_CREATE_GROUP_DAYS);
        c.setNewUserCreateGroupMemberCount(DEFAULT_NEW_USER_CREATE_GROUP_MEMBER_COUNT);
        c.setCreateGroupCount(DEFAULT_CREATE_GROUP_COUNT);
        c.setLoginSecondVerifyMinPass(DEFAULT_LOGIN_SECOND_VERIFY_MIN_PASS);
        c.setScanLoginCheckImCode(DEFAULT_SCAN_LOGIN_CHECK_IM_CODE);
        c.setVipRedeemEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        c.setVipPurchaseEnabled(DEFAULT_VIP_SWITCH_ENABLED);
        return c;
    }
}
