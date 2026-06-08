package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomMomentQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomMoment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomMomentMapper extends BaseMapper<PiamomMoment> {

    List<PiamomMoment> selectAdminPageList(PiamomMomentQueryDto queryDto);

    Long selectAdminCount(PiamomMomentQueryDto queryDto);

    PiamomMoment selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);

    int updateTop(@Param("id") Long id, @Param("isTop") Integer isTop);

    /** 举报成立等场景：下架隐藏并取消置顶 */
    int hideByPrimaryKey(@Param("id") Long id);

    /** 按点赞表实际条数重算 like_count */
    int syncLikeCount(@Param("id") Long id);

    /** 按评论表实际条数重算 comment_count */
    int syncCommentCount(@Param("id") Long id);
}
