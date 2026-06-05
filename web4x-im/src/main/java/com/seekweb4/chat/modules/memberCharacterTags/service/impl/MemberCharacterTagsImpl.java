package com.seekweb4.chat.modules.memberCharacterTags.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.memberCharacterTags.entity.DTO.MemberCharacterTagsDTO;
import com.seekweb4.chat.modules.memberCharacterTags.entity.MemberCharacterTags;
import com.seekweb4.chat.modules.memberCharacterTags.mapper.MemberCharacterTagsMapper;
import com.seekweb4.chat.modules.memberCharacterTags.service.MemberCharacterTagsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberCharacterTagsImpl implements MemberCharacterTagsService {

    private static final Logger log = LoggerFactory.getLogger(MemberCharacterTagsImpl.class);
    @Autowired
    private MemberCharacterTagsMapper memberCharacterTagsMapper;

    @Override
    public Page<MemberCharacterTags> selectAdminPageList(MemberCharacterTagsDTO queryDto) {
        Page<MemberCharacterTags> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());

        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        queryDto.setCharacterTags(queryDto.getCharacterTags());
        queryDto.setCharacterExplanation(queryDto.getCharacterExplanation());

        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }

        // 查询总数
        Long count = memberCharacterTagsMapper.selectAdminCount(queryDto);
        page.setCount(count);

        // 查询数据
        List<MemberCharacterTags> list = memberCharacterTagsMapper.selectAdminPageList(queryDto);
        page.setList(list);

        return page;
    }

    @Override
    public MemberCharacterTags selectCharacterTagsByID(Long id) {
        return memberCharacterTagsMapper.selectCharacterTagsByID(id);
    }

    @Override
    public int deleteCharacterTags(Long id) {
        return memberCharacterTagsMapper.deleteCharacterTags(id);
    }

    @Override
    public boolean addCharacterTags(MemberCharacterTags memberCharacterTags) {
        try {
            memberCharacterTags.setCreateTime(new Date());
            memberCharacterTags.setUpdateTime(new Date());
            memberCharacterTags.setIsDeleted(0);
            return memberCharacterTagsMapper.insert(memberCharacterTags) > 0;
        } catch (IllegalStateException e) {
            log.error("添加人设标签管理失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("保存人设标签失败", e);
            throw new RuntimeException("保存失败：" + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateCharacterTags(MemberCharacterTags memberCharacterTags) {
        try {
            memberCharacterTags.setUpdateTime(new Date());
            return memberCharacterTagsMapper.updateByPrimaryKeySelective(memberCharacterTags) > 0;
        } catch (IllegalStateException e) {
            log.error("更新人设标签管理失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新人设标签失败", e);
            throw new RuntimeException("更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将orderBy字符串中的驼峰字段名转换为下划线命名（数据库列名）
     * 例如：paymentType asc -> payment_type asc
     *      createTime desc, id asc -> create_time desc, id asc
     *
     * @param orderBy 原始orderBy字符串
     * @return 转换后的orderBy字符串
     */
    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>(Arrays.asList(
            "coinId"
    ));

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }

        // 按逗号分割多个排序字段
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }

            // 分割字段名和排序方向（asc/desc）
            String[] fieldAndOrder = part.split("\\s+");
            if (fieldAndOrder.length > 0) {
                String fieldName = fieldAndOrder[0].trim();
                // 将驼峰转换为下划线
                String underscoreField;
                if (fieldName.contains("_") || CAMEL_CASE_COLUMN_WHITELIST.contains(fieldName)) {
                    underscoreField = fieldName;
                } else {
                    underscoreField = StringUtils.toUnderScoreCase(fieldName);
                }

                if (i > 0) {
                    result.append(", ");
                }
                result.append(underscoreField);

                // 保留排序方向
                if (fieldAndOrder.length > 1) {
                    result.append(" ").append(fieldAndOrder[1].trim());
                }
            }
        }

        return result.toString();
    }
}
