package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.modules.creditScore.entity.CreditScoreConfig;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreLog;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreMemberDetails;
import com.seekweb4.chat.modules.creditScore.entity.MemberCreditScoreType;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreLogMapper;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberDetailsMapper;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberMapper;
import com.seekweb4.chat.modules.creditScore.mapper.MemberCreditScoreTypeMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreConfigService;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreTypeConfig;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreTypeConfigService;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreMemberInfoVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserOperateResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Slf4j
@Service
@Transactional
public class CreditScoreUserServiceImpl implements CreditScoreUserService {

    private static final int SCORE_SCALE = 2;
    private static final int RATE_SCALE = 4;

    // 默认类型：系统添加 7；系统扣减 8
    private static final Integer SYSTEM_ADD_TYPE = 7;
    private static final Integer SYSTEM_REDUCE_TYPE = 8;

    /** t_credit_score_log.remark：后台加减分固定文案 */
    private static final String LOG_REMARK_ADD = "系统增加信用分";
    private static final String LOG_REMARK_REDUCE = "系统扣减信用分";

    @Resource
    private CreditScoreConfigService creditScoreConfigService;

    @Resource
    private CreditScoreTypeConfigService creditScoreTypeConfigService;

    @Resource
    private CreditScoreMemberMapper creditScoreMemberMapper;

    @Resource
    private MemberCreditScoreTypeMapper memberCreditScoreTypeMapper;

    @Resource
    private CreditScoreMemberDetailsMapper creditScoreMemberDetailsMapper;

    @Resource
    private CreditScoreLogMapper creditScoreLogMapper;

    @Override
    public CreditScoreUserOperateResultVo addSystem(String userId, Integer subtype, String desc) {
        return addScore(userId, SYSTEM_ADD_TYPE, subtype, desc);
    }

    @Override
    public CreditScoreUserOperateResultVo reduceSystem(String userId, Integer subtype, String desc) {
        return reduceScore(userId, SYSTEM_REDUCE_TYPE, subtype, desc);
    }

    @Override
    public CreditScoreUserOperateResultVo addScore(String userId, Integer type, Integer subtype, String desc) {
        return operate(userId, type, subtype, desc, null, true);
    }

    @Override
    public CreditScoreUserOperateResultVo reduceScore(String userId, Integer type, Integer subtype, String desc) {
        return operate(userId, type, subtype, desc, null, false);
    }

    @Override
    public CreditScoreUserOperateResultVo addScore(String userId,
                                                  Integer type,
                                                  Integer subtype,
                                                  String desc,
                                                  BigDecimal score) {
        return operate(userId, type, subtype, desc, score, true);
    }

    @Override
    public CreditScoreUserOperateResultVo reduceScore(String userId,
                                                     Integer type,
                                                     Integer subtype,
                                                     String desc,
                                                     BigDecimal score) {
        return operate(userId, type, subtype, desc, score, false);
    }

    private CreditScoreUserOperateResultVo operate(String userId,
                                                  Integer type,
                                                  Integer subtype,
                                                  String desc,
                                                  BigDecimal inputScore,
                                                  boolean isAdd) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("type不能为空");
        }
        if (subtype == null) {
            subtype = 0;
        }
        // desc 为 NOT NULL：非 type=5 时允许不传，服务端会生成默认描述
        // type=5 平台贡献：desc 必须填写（严格校验）
        boolean descBlank = StringUtils.isBlank(desc);
        if (descBlank && Integer.valueOf(5).equals(type)) {
            throw new IllegalArgumentException("type=5(平台贡献)必须输入描述desc");
        }
        String finalDesc = desc;
        if (descBlank) {
            finalDesc = (isAdd ? "系统增加信用分" : "系统减少信用分");
        }

        Date now = new Date();

        // 1) 获取基础配置（加成比例）
        CreditScoreConfig baseConfig = creditScoreConfigService.getCurrent();
        if (baseConfig == null) {
            throw new IllegalStateException("信用分基础配置不存在");
        }

        // 2) 获取启用的类型配置（单次分数/封顶上限）
        CreditScoreTypeConfig typeConfig = creditScoreTypeConfigService.getEnabledByTypeSubtype(type, subtype);
        if (typeConfig == null) {
            throw new IllegalStateException("信用分类型配置不存在或未启用，type=" + type + ", subtype=" + subtype);
        }

        BigDecimal maxLimit = nvl(typeConfig.getMaxLimit()).setScale(SCORE_SCALE, RoundingMode.HALF_UP);

        // type=5 平台贡献：每次分值为类型配置 score（与提示「每次最大分值」一致）；不叠加会员/靓号；手动填 score 时须与配置一致
        BigDecimal platformSingleScore = null;
        if (Integer.valueOf(5).equals(type)) {
            BigDecimal raw = typeConfig.getScore();
            if (raw == null || raw.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("「平台贡献」需在信用分类型配置中设置有效的单次分值（score）");
            }
            platformSingleScore = raw.setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            if (inputScore != null) {
                BigDecimal in = inputScore.setScale(SCORE_SCALE, RoundingMode.HALF_UP);
                if (in.compareTo(platformSingleScore) != 0) {
                    String cfg = platformSingleScore.stripTrailingZeros().toPlainString();
                    throw new IllegalArgumentException(
                            String.format("「平台贡献」每次最大分值为 %s 分", cfg));
                }
            }
        }

        // 手动输入分数时的校验提示：超过类型上限则直接提示（避免“输入1000只加150”的困惑）；type=5 不走此规则
        if (!Integer.valueOf(5).equals(type) && isAdd && inputScore != null && maxLimit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal in = inputScore.setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            if (in.compareTo(maxLimit) > 0) {
                throw new IllegalArgumentException("当前输入分数" + in + " 超过该类型最大上限" + maxLimit);
            }
        }

        BigDecimal baseScore;
        BigDecimal vipRateUsed;
        BigDecimal lianghaoRateUsed;
        BigDecimal finalScore;

        // type=5 平台贡献：每次加/减分值 = 类型配置 score，并且不计算会员/靓号加成
        if (Integer.valueOf(5).equals(type)) {
            baseScore = platformSingleScore;
            vipRateUsed = BigDecimal.ZERO.setScale(RATE_SCALE);
            lianghaoRateUsed = BigDecimal.ZERO.setScale(RATE_SCALE);
            finalScore = platformSingleScore;
        } else {
            // 不传 inputScore 则走类型配置 score；传了则按输入分数计算
            baseScore = nvl(inputScore != null ? inputScore : typeConfig.getScore())
                    .setScale(RATE_SCALE, RoundingMode.HALF_UP);

            // 3) 会员/靓号加成判断
            CreditScoreMemberInfoVo memberInfo = creditScoreMemberMapper.selectMemberInfoByUserId(userId);
            if (memberInfo == null) {
                throw new IllegalStateException("用户不存在: " + userId);
            }

            // 会员、靓号分别判断：仅「加分」叠加加成；「扣分」按基础分（类型 score 或手动输入）扣，不叠加会员/靓号
            boolean isVip = "1".equals(memberInfo.getIsvip());
            boolean hasLianghao = StringUtils.isNotBlank(memberInfo.getLianghao());

            vipRateUsed = BigDecimal.ZERO.setScale(RATE_SCALE);
            lianghaoRateUsed = BigDecimal.ZERO.setScale(RATE_SCALE);
            if (isAdd) {
                if (isVip) {
                    vipRateUsed = nvl(baseConfig.getVipBonusRate()).setScale(RATE_SCALE, RoundingMode.HALF_UP);
                }
                if (hasLianghao) {
                    lianghaoRateUsed = nvl(baseConfig.getLianghaoBonusRate()).setScale(RATE_SCALE, RoundingMode.HALF_UP);
                }
                BigDecimal bonusRateTotal = vipRateUsed.add(lianghaoRateUsed);
                finalScore = baseScore.add(baseScore.multiply(bonusRateTotal))
                        .setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            } else {
                finalScore = baseScore.setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            }
        }

        // 4) 获取累计分（当前 type/subtype）
        MemberCreditScoreType currentRow = memberCreditScoreTypeMapper.selectByUserIdTypeSubtype(userId, type, subtype);
        BigDecimal oldScore = currentRow != null && currentRow.getCurrentScore() != null
                ? currentRow.getCurrentScore().setScale(SCORE_SCALE, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(SCORE_SCALE, RoundingMode.HALF_UP);

        BigDecimal newScore;
        BigDecimal deltaScoreSigned;

        if (isAdd) {
            // 增加时按 max_limit 封顶；负分允许逐步加回，不再强制归零。
            BigDecimal target = oldScore.add(finalScore);
            if (maxLimit.compareTo(BigDecimal.ZERO) > 0) {
                if (target.compareTo(maxLimit) > 0) {
                    target = maxLimit;
                }
            }
            newScore = target.setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            deltaScoreSigned = newScore.subtract(oldScore); // >=0
        } else {
            BigDecimal target = oldScore.subtract(finalScore);
            newScore = target;
            deltaScoreSigned = newScore.subtract(oldScore); // <=0
        }

        if (deltaScoreSigned.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("本次操作无实际变更分数");
        }

        // 5) 更新 t_member_credit_score_type.current_score（扣减允许为负数；无记录时也允许插入负数）
        if (currentRow == null) {
            MemberCreditScoreType insert = new MemberCreditScoreType();
            insert.setCreateTime(now);
            insert.setUpdateTime(now);
            insert.setIsDeleted(0);
            insert.setType(type);
            insert.setSubtype(subtype);
            insert.setCurrentScore(newScore);
            insert.setUserId(userId);
            memberCreditScoreTypeMapper.insert(insert);
        } else {
            memberCreditScoreTypeMapper.updateCurrentScoreByKeys(
                    userId, type, subtype, newScore, null, now
            );
        }

        // 6) 更新用户总分：在当前 t_member_details.credit_score 基础上增量累加/累减（允许为负数）
        CreditScoreMemberDetails detailsRow = creditScoreMemberDetailsMapper.selectByUserId(userId);
        BigDecimal totalCreditScore;
        if (detailsRow == null) {
            totalCreditScore = deltaScoreSigned;
            totalCreditScore = totalCreditScore.setScale(SCORE_SCALE, RoundingMode.HALF_UP);

            CreditScoreMemberDetails insertDetails = new CreditScoreMemberDetails();
            insertDetails.setCreateTime(now);
            insertDetails.setUpdateTime(now);
            insertDetails.setIsDeleted(0);
            insertDetails.setUserId(userId);
            insertDetails.setCreditScore(totalCreditScore);
            creditScoreMemberDetailsMapper.insert(insertDetails);
        } else {
            BigDecimal oldTotal = nvl(detailsRow.getCreditScore()).setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            totalCreditScore = oldTotal.add(deltaScoreSigned).setScale(SCORE_SCALE, RoundingMode.HALF_UP);

            creditScoreMemberDetailsMapper.updateCreditScoreByUserId(
                    userId, totalCreditScore, null, now
            );
        }

        // 7) 写入日志：t_credit_score_log
        CreditScoreLog log = new CreditScoreLog();
        log.setCreateTime(now);
        log.setUpdateTime(now);
        log.setIsDeleted(0);
        log.setType(type);
        log.setSubtype(subtype);
        log.setScore(deltaScoreSigned.setScale(SCORE_SCALE, RoundingMode.HALF_UP));
        log.setUserId(userId);
        log.setLogDesc(finalDesc);
        log.setRemark(isAdd ? LOG_REMARK_ADD : LOG_REMARK_REDUCE);
        // 写入“实际使用”的加成比例：未达条件时就是 0
        log.setVipBonusRate(vipRateUsed);
        log.setLianghaoBonusRate(lianghaoRateUsed);
        log.setBaseScore(baseScore);
        // create_by/update_by 暂不传，走 null
        creditScoreLogMapper.insertLog(log);

        CreditScoreUserOperateResultVo result = new CreditScoreUserOperateResultVo();
        result.setUserId(userId);
        result.setType(type);
        result.setSubtype(subtype);
        result.setDeltaScore(deltaScoreSigned.setScale(SCORE_SCALE, RoundingMode.HALF_UP));
        result.setCurrentScore(newScore);
        result.setTotalCreditScore(totalCreditScore);
        return result;
    }

    @Override
    public CreditScoreUserOperateResultVo activateCredit(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId不能为空");
        }
        CreditScoreMemberInfoVo memberInfo = creditScoreMemberMapper.selectMemberInfoByUserId(userId);
        if (memberInfo == null) {
            throw new IllegalStateException("用户不存在: " + userId);
        }
        CreditScoreConfig baseConfig = creditScoreConfigService.getCurrent();
        if (baseConfig == null) {
            throw new IllegalStateException("信用分基础配置不存在");
        }
        BigDecimal initScore = nvl(baseConfig.getInitScore()).setScale(SCORE_SCALE, RoundingMode.HALF_UP);

        Date now = new Date();
        CreditScoreMemberDetails detailsRow = creditScoreMemberDetailsMapper.selectByUserId(userId);
        if (detailsRow != null && Integer.valueOf(1).equals(detailsRow.getCreditStatus())) {
            throw new IllegalStateException("已开通信用分，请勿重复开通");
        }

        BigDecimal totalCreditScore;
        if (detailsRow == null) {
            totalCreditScore = initScore;
            CreditScoreMemberDetails insert = new CreditScoreMemberDetails();
            insert.setCreateTime(now);
            insert.setUpdateTime(now);
            insert.setIsDeleted(0);
            insert.setUserId(userId);
            insert.setCreditScore(totalCreditScore);
            insert.setCreditStatus(1);
            creditScoreMemberDetailsMapper.insertActivate(insert);
        } else {
            BigDecimal oldTotal = nvl(detailsRow.getCreditScore()).setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            totalCreditScore = oldTotal.add(initScore).setScale(SCORE_SCALE, RoundingMode.HALF_UP);
            creditScoreMemberDetailsMapper.updateCreditActivateByUserId(
                    userId, totalCreditScore, 1, null, now);
        }

        CreditScoreUserOperateResultVo result = new CreditScoreUserOperateResultVo();
        result.setUserId(userId);
        result.setDeltaScore(initScore);
        result.setTotalCreditScore(totalCreditScore);
        return result;
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

