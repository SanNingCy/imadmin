package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.entity.PiamomSquarePublishQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomSquarePublishQuotaMapper extends BaseMapper<PiamomSquarePublishQuota> {

    List<PiamomSquarePublishQuota> selectAll();

    PiamomSquarePublishQuota selectByPrimaryKey(@Param("id") Long id);

    int insert(PiamomSquarePublishQuota record);

    int updateByPrimaryKeySelective(PiamomSquarePublishQuota record);

    int deleteByPrimaryKey(@Param("id") Long id);
}
