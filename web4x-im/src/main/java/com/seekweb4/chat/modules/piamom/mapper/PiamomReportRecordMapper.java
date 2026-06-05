package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomReportRecordQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomReportRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PiamomReportRecordMapper extends BaseMapper<PiamomReportRecord> {

    List<PiamomReportRecord> selectAdminPageList(PiamomReportRecordQueryDto queryDto);

    Long selectAdminCount(PiamomReportRecordQueryDto queryDto);

    PiamomReportRecord selectByPrimaryKey(@Param("id") Long id);

    int updateAudit(@Param("id") Long id,
                    @Param("auditStatus") Integer auditStatus,
                    @Param("auditUserId") String auditUserId,
                    @Param("auditTime") Date auditTime);
}
