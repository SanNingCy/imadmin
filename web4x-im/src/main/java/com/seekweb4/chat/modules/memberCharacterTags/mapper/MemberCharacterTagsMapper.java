package com.seekweb4.chat.modules.memberCharacterTags.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.memberCharacterTags.entity.DTO.MemberCharacterTagsDTO;
import com.seekweb4.chat.modules.memberCharacterTags.entity.MemberCharacterTags;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import com.seekweb4.chat.vo.response.CharacterTagsResponseVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MemberCharacterTagsMapper继承基类
 *
 * @author coderpwh
 */
@Mapper
public interface MemberCharacterTagsMapper extends BaseMapper<MemberCharacterTags> {


    int insertSelective(MemberCharacterTags record);

    int updateByPrimaryKeySelective(MemberCharacterTags record);

    int updateByPrimaryKey(MemberCharacterTags record);

    int deleteByPrimaryKey(Long id);

    MemberCharacterTags selectByPrimaryKey(Long id);

    int insert(MemberCharacterTags record);


    List<CharacterTagsResponseVO> getCharacterTagsInfo();


    /**
     * 后台管理 - 分页查询用户兴趣
     */
    List<MemberCharacterTags> selectAdminPageList(MemberCharacterTagsDTO memberCharacterTagsDTO);
    /**
     * 后台管理 - 总数
     */
    Long selectAdminCount(MemberCharacterTagsDTO memberCharacterTagsDTO);

    /**
     * 根据主键查询
     * @param id 主键ID
     */
    MemberCharacterTags selectCharacterTagsByID(Long id);

    /**
     * 逻辑删除
     */
    int deleteCharacterTags(Long id);

}
