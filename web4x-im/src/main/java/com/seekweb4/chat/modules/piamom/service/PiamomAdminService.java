package com.seekweb4.chat.modules.piamom.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.piamom.dto.*;
import com.seekweb4.chat.modules.piamom.entity.*;

import java.util.List;

public interface PiamomAdminService {

    Page<PiamomMoment> momentPage(PiamomMomentQueryDto queryDto);

    PiamomMoment momentGetById(Long id);

    boolean momentDelete(Long id);

    boolean momentUpdateTop(PiamomTopDto dto);

    Page<PiamomMomentComment> momentCommentPage(PiamomCommentQueryDto queryDto);

    PiamomMomentComment momentCommentGetById(Long id);

    boolean momentCommentDelete(Long id);

    Page<PiamomMomentLike> momentLikePage(PiamomMomentLikeQueryDto queryDto);

    PiamomMomentLike momentLikeGetById(Long id);

    boolean momentLikeDelete(Long id);

    Page<PiamomSquarePost> squarePage(PiamomSquarePostQueryDto queryDto);

    PiamomSquarePost squareGetById(Long id);

    boolean squareDelete(Long id);

    boolean squareUpdateTop(PiamomTopDto dto);

    boolean squareUpdateStakeStatus(Long id, Integer odicStakeStatus);

    Page<PiamomSquareComment> squareCommentPage(PiamomCommentQueryDto queryDto);

    PiamomSquareComment squareCommentGetById(Long id);

    boolean squareCommentDelete(Long id);

    Page<PiamomSquareLike> squareLikePage(PiamomSquareLikeQueryDto queryDto);

    PiamomSquareLike squareLikeGetById(Long id);

    boolean squareLikeDelete(Long id);

    List<PiamomHotConfig> hotConfigList();

    PiamomHotConfig hotConfigGetById(Long id);

    boolean hotConfigCreate(PiamomHotConfig config);

    boolean hotConfigUpdate(PiamomHotConfig config);

    boolean hotConfigDelete(Long id);

    List<PiamomReportConfig> reportConfigList();

    PiamomReportConfig reportConfigGetById(Long id);

    boolean reportConfigCreate(PiamomReportConfig config);

    boolean reportConfigUpdate(PiamomReportConfig config);

    boolean reportConfigDelete(Long id);

    List<PiamomSquarePublishQuota> squarePublishQuotaList();

    PiamomSquarePublishQuota squarePublishQuotaGetById(Long id);

    boolean squarePublishQuotaCreate(PiamomSquarePublishQuota quota);

    boolean squarePublishQuotaUpdate(PiamomSquarePublishQuota quota);

    boolean squarePublishQuotaDelete(Long id);

    Page<PiamomReportRecord> reportRecordPage(PiamomReportRecordQueryDto queryDto);

    PiamomReportRecord reportRecordGetById(Long id);

    boolean reportRecordAudit(PiamomReportAuditDto dto);

    Page<PiamomNotifyMessage> notifyPage(PiamomNotifyQueryDto queryDto);

    PiamomNotifyMessage notifyGetById(Long id);

    Page<PiamomUserProfileStat> profileStatPage(PiamomProfileStatQueryDto queryDto);

    PiamomUserProfileStat profileStatGetById(Long id);

    PiamomUserProfileStat profileStatGetByUserId(String userId);

    Page<PiamomUserLikeRecord> userLikeRecordPage(PiamomUserLikeRecordQueryDto queryDto);

    PiamomUserLikeRecord userLikeRecordGetById(Long id);

    boolean userLikeRecordDelete(Long id);

    Page<PiamomUserFollow> followPage(PiamomFollowQueryDto queryDto);

    PiamomUserFollow followGetById(Long id);

    boolean followDelete(Long id);

    Page<PiamomUserBlacklist> blacklistPage(PiamomBlacklistQueryDto queryDto);

    PiamomUserBlacklist blacklistGetById(Long id);

    boolean blacklistDelete(Long id);
}
