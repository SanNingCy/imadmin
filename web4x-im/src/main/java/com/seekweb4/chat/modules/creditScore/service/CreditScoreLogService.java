package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreLogQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogDetailVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogUserSummaryVo;

public interface CreditScoreLogService {

    /**
     * 按用户查询：用户 idno/靓号/总信用分 + 分页日志
     */
    CreditScoreLogUserSummaryVo getUserSummary(String userId);

    /**
     * 分页列表：不传条件则查全部；每条含 userId、idno、靓号、总信用分。
     * 可选筛选：userId、idno、靓号、type、subtype。
     */
    Page<CreditScoreLogDetailVo> pageByUser(CreditScoreLogQueryDto queryDto);

    /**
     * 未传 userId 时，用 idno/靓号 在 t_member 中解析一个用户 id（LIMIT 1），供列表页 summary 使用
     */
    String resolveUserIdForSummary(String idno, String lianghao);

    /**
     * 根据日志主键查询详情（含 idno、靓号、总信用分）
     */
    CreditScoreLogDetailVo getDetailById(Long id);
}
