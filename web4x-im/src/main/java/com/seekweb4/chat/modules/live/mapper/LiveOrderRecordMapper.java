package com.seekweb4.chat.modules.live.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.live.dto.LiveOrderRecordQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveOrderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveOrderRecordMapper extends BaseMapper<LiveOrderRecord> {

    List<LiveOrderRecord> selectAdminPageList(LiveOrderRecordQueryDto queryDto);

    Long selectAdminCount(LiveOrderRecordQueryDto queryDto);

    LiveOrderRecord selectByPrimaryKey(@Param("id") Long id);

    LiveOrderRecord selectByOrderNo(@Param("orderNo") String orderNo);

    int updateByPrimaryKeySelective(LiveOrderRecord record);

    int insert(LiveOrderRecord record);
}
