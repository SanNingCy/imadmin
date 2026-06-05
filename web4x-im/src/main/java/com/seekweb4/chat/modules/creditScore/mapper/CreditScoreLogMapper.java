package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreLogQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreLog;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogDetailVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogUserSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditScoreLogMapper extends BaseMapper<CreditScoreLog> {

    CreditScoreLogUserSummaryVo selectUserSummary(@Param("userId") String userId);

    /**
     * 按 idno / 靓号 解析出一个用户 id（LIMIT 1），用于列表页 summary
     */
    String selectFirstUserIdByIdnoOrLianghao(@Param("idno") String idno, @Param("lianghao") String lianghao);

    CreditScoreLogDetailVo selectDetailById(@Param("id") Long id);

    List<CreditScoreLogDetailVo> selectPageList(CreditScoreLogQueryDto queryDto);

    Long selectCount(CreditScoreLogQueryDto queryDto);

    /**
     * 手动/系统加减信用分时写日志
     */
    int insertLog(CreditScoreLog record);
}
