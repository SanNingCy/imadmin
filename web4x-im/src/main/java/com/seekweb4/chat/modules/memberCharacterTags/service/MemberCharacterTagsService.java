package com.seekweb4.chat.modules.memberCharacterTags.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.memberCharacterTags.entity.DTO.MemberCharacterTagsDTO;
import com.seekweb4.chat.modules.memberCharacterTags.entity.MemberCharacterTags;

public interface MemberCharacterTagsService {
    /**
     * 后台管理 - 分页查询人设标签管理
     */
    Page<MemberCharacterTags> selectAdminPageList(MemberCharacterTagsDTO memberCharacterTagsDTO);

    /**
     * 根据ID查询人设标签管理
     */
    MemberCharacterTags selectCharacterTagsByID(Long id);
    /**
     * 删除人设标签管理
     */
    int deleteCharacterTags(Long id);
    /**
     * 添加人设标签管理
     */
    boolean addCharacterTags(MemberCharacterTags memberCharacterTags);
    /**
     * 修改人设标签管理
     */
    boolean updateCharacterTags(MemberCharacterTags memberCharacterTags);
}
