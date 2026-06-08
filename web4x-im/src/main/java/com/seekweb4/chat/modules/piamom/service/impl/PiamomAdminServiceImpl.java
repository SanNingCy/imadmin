package com.seekweb4.chat.modules.piamom.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.piamom.dto.*;
import com.seekweb4.chat.modules.piamom.entity.*;
import com.seekweb4.chat.modules.piamom.mapper.*;
import com.seekweb4.chat.modules.piamom.service.PiamomAdminService;
import com.seekweb4.chat.modules.piamom.util.PiamomPageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PiamomAdminServiceImpl implements PiamomAdminService {

    /** 质押中 */
    private static final int STAKE_ACTIVE = 0;
    /** 举报成立没收 */
    private static final int STAKE_CONFISCATED = 2;

    private static final String TARGET_TYPE_SQUARE = "square";
    private static final String TARGET_TYPE_MOMENT = "moment";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private PiamomMomentMapper momentMapper;
    @Resource
    private PiamomMomentCommentMapper momentCommentMapper;
    @Resource
    private PiamomMomentLikeMapper momentLikeMapper;
    @Resource
    private PiamomSquarePostMapper squarePostMapper;
    @Resource
    private PiamomSquareCommentMapper squareCommentMapper;
    @Resource
    private PiamomSquareLikeMapper squareLikeMapper;
    @Resource
    private PiamomHotConfigMapper hotConfigMapper;
    @Resource
    private PiamomReportConfigMapper reportConfigMapper;
    @Resource
    private PiamomSquarePublishQuotaMapper squarePublishQuotaMapper;
    @Resource
    private PiamomReportRecordMapper reportRecordMapper;
    @Resource
    private PiamomNotifyMessageMapper notifyMessageMapper;
    @Resource
    private PiamomUserProfileStatMapper profileStatMapper;
    @Resource
    private PiamomUserLikeRecordMapper userLikeRecordMapper;
    @Resource
    private PiamomUserFollowMapper userFollowMapper;
    @Resource
    private PiamomUserBlacklistMapper userBlacklistMapper;

    @Override
    public Page<PiamomMoment> momentPage(PiamomMomentQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, momentMapper::selectAdminCount, momentMapper::selectAdminPageList);
    }

    @Override
    public PiamomMoment momentGetById(Long id) {
        return momentMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean momentDelete(Long id) {
        return momentMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean momentUpdateTop(PiamomTopDto dto) {
        if (dto == null || dto.getId() == null || dto.getIsTop() == null) {
            throw new IllegalArgumentException("id与isTop不能为空");
        }
        return momentMapper.updateTop(dto.getId(), dto.getIsTop()) > 0;
    }

    @Override
    public Page<PiamomMomentComment> momentCommentPage(PiamomCommentQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, momentCommentMapper::selectAdminCount, momentCommentMapper::selectAdminPageList);
    }

    @Override
    public PiamomMomentComment momentCommentGetById(Long id) {
        return momentCommentMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean momentCommentDelete(Long id) {
        PiamomMomentComment comment = momentCommentMapper.selectByPrimaryKey(id);
        if (comment == null) {
            return false;
        }
        Long momentId = comment.getMomentId();
        int rows = momentCommentMapper.deleteByPrimaryKey(id);
        if (rows > 0 && momentId != null) {
            momentMapper.syncCommentCount(momentId);
        }
        return rows > 0;
    }

    @Override
    public Page<PiamomMomentLike> momentLikePage(PiamomMomentLikeQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, momentLikeMapper::selectAdminCount, momentLikeMapper::selectAdminPageList);
    }

    @Override
    public PiamomMomentLike momentLikeGetById(Long id) {
        return momentLikeMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean momentLikeDelete(Long id) {
        PiamomMomentLike like = momentLikeMapper.selectByPrimaryKey(id);
        if (like == null) {
            return false;
        }
        Long momentId = like.getMomentId();
        int rows = momentLikeMapper.deleteByPrimaryKey(id);
        if (rows > 0 && momentId != null) {
            momentMapper.syncLikeCount(momentId);
        }
        return rows > 0;
    }

    @Override
    public Page<PiamomSquarePost> squarePage(PiamomSquarePostQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, squarePostMapper::selectAdminCount, squarePostMapper::selectAdminPageList);
    }

    @Override
    public PiamomSquarePost squareGetById(Long id) {
        return squarePostMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squareDelete(Long id) {
        return squarePostMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squareUpdateTop(PiamomTopDto dto) {
        if (dto == null || dto.getId() == null || dto.getIsTop() == null) {
            throw new IllegalArgumentException("id与isTop不能为空");
        }
        return squarePostMapper.updateTop(dto.getId(), dto.getIsTop()) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squareUpdateStakeStatus(Long id, Integer odicStakeStatus) {
        if (id == null || odicStakeStatus == null) {
            throw new IllegalArgumentException("id与odicStakeStatus不能为空");
        }
        PiamomSquarePost post = new PiamomSquarePost();
        post.setId(id);
        post.setOdicStakeStatus(odicStakeStatus);
        if (odicStakeStatus == 1) {
            post.setStakeRefundTime(new Date());
        }
        return squarePostMapper.updateByPrimaryKeySelective(post) > 0;
    }

    @Override
    public Page<PiamomSquareComment> squareCommentPage(PiamomCommentQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, squareCommentMapper::selectAdminCount, squareCommentMapper::selectAdminPageList);
    }

    @Override
    public PiamomSquareComment squareCommentGetById(Long id) {
        return squareCommentMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squareCommentDelete(Long id) {
        PiamomSquareComment comment = squareCommentMapper.selectByPrimaryKey(id);
        if (comment == null) {
            return false;
        }
        Long squareId = comment.getSquareId();
        int rows = squareCommentMapper.deleteByPrimaryKey(id);
        if (rows > 0 && squareId != null) {
            squarePostMapper.syncCommentCount(squareId);
        }
        return rows > 0;
    }

    @Override
    public Page<PiamomSquareLike> squareLikePage(PiamomSquareLikeQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, squareLikeMapper::selectAdminCount, squareLikeMapper::selectAdminPageList);
    }

    @Override
    public PiamomSquareLike squareLikeGetById(Long id) {
        return squareLikeMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squareLikeDelete(Long id) {
        PiamomSquareLike like = squareLikeMapper.selectByPrimaryKey(id);
        if (like == null) {
            return false;
        }
        Long squareId = like.getSquareId();
        int rows = squareLikeMapper.deleteByPrimaryKey(id);
        if (rows > 0 && squareId != null) {
            squarePostMapper.syncLikeCount(squareId);
        }
        return rows > 0;
    }

    @Override
    public List<PiamomHotConfig> hotConfigList() {
        return hotConfigMapper.selectAll();
    }

    @Override
    public PiamomHotConfig hotConfigGetById(Long id) {
        return hotConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean hotConfigCreate(PiamomHotConfig config) {
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        return hotConfigMapper.insert(config) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean hotConfigUpdate(PiamomHotConfig config) {
        return hotConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean hotConfigDelete(Long id) {
        return hotConfigMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public List<PiamomReportConfig> reportConfigList() {
        return reportConfigMapper.selectAll();
    }

    @Override
    public PiamomReportConfig reportConfigGetById(Long id) {
        return reportConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean reportConfigCreate(PiamomReportConfig config) {
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        return reportConfigMapper.insert(config) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean reportConfigUpdate(PiamomReportConfig config) {
        return reportConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean reportConfigDelete(Long id) {
        return reportConfigMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public List<PiamomSquarePublishQuota> squarePublishQuotaList() {
        return squarePublishQuotaMapper.selectAll();
    }

    @Override
    public PiamomSquarePublishQuota squarePublishQuotaGetById(Long id) {
        return squarePublishQuotaMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squarePublishQuotaCreate(PiamomSquarePublishQuota quota) {
        if (quota == null || quota.getCreditMin() == null || quota.getDailyLimit() == null) {
            throw new IllegalArgumentException("creditMin与dailyLimit不能为空");
        }
        if (quota.getSortOrder() == null) {
            quota.setSortOrder(0);
        }
        if (quota.getStatus() == null) {
            quota.setStatus(1);
        }
        if (quota.getCreditMax() != null && quota.getCreditMax() < quota.getCreditMin()) {
            throw new IllegalArgumentException("creditMax不能小于creditMin");
        }
        return squarePublishQuotaMapper.insert(quota) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squarePublishQuotaUpdate(PiamomSquarePublishQuota quota) {
        if (quota == null || quota.getId() == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        if (quota.getCreditMin() != null && quota.getCreditMax() != null
                && quota.getCreditMax() < quota.getCreditMin()) {
            throw new IllegalArgumentException("creditMax不能小于creditMin");
        }
        return squarePublishQuotaMapper.updateByPrimaryKeySelective(quota) > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean squarePublishQuotaDelete(Long id) {
        return squarePublishQuotaMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public Page<PiamomReportRecord> reportRecordPage(PiamomReportRecordQueryDto queryDto) {
        Page<PiamomReportRecord> page = PiamomPageHelper.page(
                queryDto, reportRecordMapper::selectAdminCount, reportRecordMapper::selectAdminPageList);
        enrichReportMedia(page.getList());
        return page;
    }

    @Override
    public PiamomReportRecord reportRecordGetById(Long id) {
        PiamomReportRecord record = reportRecordMapper.selectByPrimaryKey(id);
        enrichReportMedia(record);
        return record;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean reportRecordAudit(PiamomReportAuditDto dto) {
        if (dto == null || dto.getId() == null || dto.getAuditStatus() == null) {
            throw new IllegalArgumentException("id与auditStatus不能为空");
        }
        if (dto.getAuditStatus() != 1 && dto.getAuditStatus() != 2) {
            throw new IllegalArgumentException("auditStatus仅支持1(通过)或2(驳回)");
        }
        PiamomReportRecord record = reportRecordMapper.selectByPrimaryKey(dto.getId());
        if (record == null) {
            throw new IllegalArgumentException("举报记录不存在");
        }
        if (record.getAuditStatus() != null && record.getAuditStatus() != 0) {
            throw new IllegalArgumentException("该举报已审核，不可重复操作");
        }
        Date auditTime = new Date();
        boolean updated = reportRecordMapper.updateAudit(
                dto.getId(), dto.getAuditStatus(), dto.getAuditUserId(), auditTime) > 0;
        if (!updated) {
            return false;
        }
        if (dto.getAuditStatus() == 1) {
            onReportApproved(record);
        }
        return true;
    }

    /**
     * 举报成立：下架隐藏被举报内容；广场帖且质押仍为「质押中」时改为「举报没收」，不退 ODIC。
     */
    private void onReportApproved(PiamomReportRecord record) {
        if (record == null || record.getTargetId() == null) {
            return;
        }
        hideReportedTarget(record);
        if (TARGET_TYPE_SQUARE.equalsIgnoreCase(record.getTargetType())) {
            confiscateSquareStakeIfActive(record);
        }
    }

    private void hideReportedTarget(PiamomReportRecord record) {
        Long targetId = record.getTargetId();
        String targetType = record.getTargetType();
        int rows = 0;
        if (TARGET_TYPE_SQUARE.equalsIgnoreCase(targetType)) {
            rows = squarePostMapper.hideByPrimaryKey(targetId);
        } else if (TARGET_TYPE_MOMENT.equalsIgnoreCase(targetType)) {
            rows = momentMapper.hideByPrimaryKey(targetId);
        }
        if (rows > 0) {
            log.info("举报成立下架隐藏: reportId={}, targetType={}, targetId={}",
                    record.getId(), targetType, targetId);
        } else {
            log.info("举报成立下架未命中(目标不存在或类型不支持): reportId={}, targetType={}, targetId={}",
                    record.getId(), targetType, targetId);
        }
    }

    /**
     * 仅当当前质押状态为「质押中」时改为「举报没收」，已退还(1)或已没收(2)不再变更。
     */
    private void confiscateSquareStakeIfActive(PiamomReportRecord record) {
        Long squareId = record.getTargetId();
        int rows = squarePostMapper.updateStakeStatusIf(squareId, STAKE_ACTIVE, STAKE_CONFISCATED);
        if (rows > 0) {
            PiamomSquarePost post = squarePostMapper.selectByPrimaryKey(squareId);
            log.info("举报成立没收质押: reportId={}, squareId={}, userId={}, odicStake={}, 仅更新状态不退余额",
                    record.getId(), squareId,
                    post != null ? post.getUserId() : null,
                    post != null ? post.getOdicStake() : null);
        } else {
            PiamomSquarePost post = squarePostMapper.selectByPrimaryKey(squareId);
            Integer currentStatus = post != null ? post.getOdicStakeStatus() : null;
            log.info("举报成立但质押未变更(非质押中): reportId={}, squareId={}, currentStakeStatus={}",
                    record.getId(), squareId, currentStatus);
        }
    }

    private void enrichReportMedia(List<PiamomReportRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        for (PiamomReportRecord record : records) {
            enrichReportMedia(record);
        }
    }

    private void enrichReportMedia(PiamomReportRecord record) {
        if (record == null) {
            return;
        }
        record.setImageUrlList(parseMediaList(record.getImageUrls()));
        record.setPostImageUrlList(parseMediaList(record.getPostImageUrls()));
        record.setPostVideoList(parseMediaList(record.getPostVideo()));
    }

    private List<String> parseMediaList(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String text = raw.trim();
        try {
            if (text.startsWith("[")) {
                return OBJECT_MAPPER.readValue(text, new TypeReference<List<String>>() {});
            }
            return Collections.singletonList(text);
        } catch (Exception e) {
            log.warn("解析媒体字段失败，按单值返回: {}", raw, e);
            return Collections.singletonList(raw);
        }
    }

    @Override
    public Page<PiamomNotifyMessage> notifyPage(PiamomNotifyQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, notifyMessageMapper::selectAdminCount, notifyMessageMapper::selectAdminPageList);
    }

    @Override
    public PiamomNotifyMessage notifyGetById(Long id) {
        return notifyMessageMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<PiamomUserProfileStat> profileStatPage(PiamomProfileStatQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, profileStatMapper::selectAdminCount, profileStatMapper::selectAdminPageList);
    }

    @Override
    public PiamomUserProfileStat profileStatGetById(Long id) {
        return profileStatMapper.selectByPrimaryKey(id);
    }

    @Override
    public PiamomUserProfileStat profileStatGetByUserId(String userId) {
        return profileStatMapper.selectByUserId(userId);
    }

    @Override
    public Page<PiamomUserLikeRecord> userLikeRecordPage(PiamomUserLikeRecordQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, userLikeRecordMapper::selectAdminCount, userLikeRecordMapper::selectAdminPageList);
    }

    @Override
    public PiamomUserLikeRecord userLikeRecordGetById(Long id) {
        return userLikeRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean userLikeRecordDelete(Long id) {
        return userLikeRecordMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public Page<PiamomUserFollow> followPage(PiamomFollowQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, userFollowMapper::selectAdminCount, userFollowMapper::selectAdminPageList);
    }

    @Override
    public PiamomUserFollow followGetById(Long id) {
        return userFollowMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean followDelete(Long id) {
        return userFollowMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public Page<PiamomUserBlacklist> blacklistPage(PiamomBlacklistQueryDto queryDto) {
        return PiamomPageHelper.page(queryDto, userBlacklistMapper::selectAdminCount, userBlacklistMapper::selectAdminPageList);
    }

    @Override
    public PiamomUserBlacklist blacklistGetById(Long id) {
        return userBlacklistMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean blacklistDelete(Long id) {
        return userBlacklistMapper.deleteByPrimaryKey(id) > 0;
    }
}
