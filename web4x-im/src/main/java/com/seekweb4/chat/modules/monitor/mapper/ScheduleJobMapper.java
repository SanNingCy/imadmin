package com.seekweb4.chat.modules.monitor.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.monitor.entity.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务MAPPER接口
 *
 * @author lgf
 * @version 2017-02-04
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {


}
