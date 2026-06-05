package com.seekweb4.chat.modules.sys.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.sys.entity.DictValue;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 数据字典MAPPER接口
 * @author lgf
 * @version 2017-01-16
 */
@Mapper
public interface DictValueMapper extends BaseMapper<DictValue> {

    public List<DictValue> getDictValueByType();
}
