package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomSquarePostQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomSquarePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomSquarePostMapper extends BaseMapper<PiamomSquarePost> {

    List<PiamomSquarePost> selectAdminPageList(PiamomSquarePostQueryDto queryDto);

    Long selectAdminCount(PiamomSquarePostQueryDto queryDto);

    PiamomSquarePost selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);

    int updateTop(@Param("id") Long id, @Param("isTop") Integer isTop);

    int updateByPrimaryKeySelective(PiamomSquarePost record);

    /**
     * 仅当当前质押状态为 fromStatus 时更新为 toStatus（用于举报成立 0→2，避免重复扣款）
     */
    int updateStakeStatusIf(@Param("id") Long id,
                            @Param("fromStatus") Integer fromStatus,
                            @Param("toStatus") Integer toStatus);

    /** 举报成立等场景：下架隐藏并取消置顶 */
    int hideByPrimaryKey(@Param("id") Long id);
}
