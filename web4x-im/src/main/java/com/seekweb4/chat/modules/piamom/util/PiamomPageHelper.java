package com.seekweb4.chat.modules.piamom.util;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.piamom.dto.PiamomAdminPageQueryDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * 后台分页工具
 */
public final class PiamomPageHelper {

    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>();

    private PiamomPageHelper() {
    }

    public static <T, Q extends PiamomAdminPageQueryDto> Page<T> page(
            Q queryDto,
            ToLongFunction<Q> countFn,
            Function<Q, List<T>> listFn) {
        Page<T> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        int offset = (queryDto.getPageNo() - 1) * queryDto.getPageSize();
        queryDto.setPageNo(offset);
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }
        page.setCount(countFn.applyAsLong(queryDto));
        page.setList(listFn.apply(queryDto));
        return page;
    }

    public static String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        for (String p : parts) {
            String part = p == null ? "" : p.trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
            String fieldName = fieldAndOrder[0].trim();
            String underscoreField;
            if (fieldName.contains("_") || CAMEL_CASE_COLUMN_WHITELIST.contains(fieldName)) {
                underscoreField = fieldName;
            } else {
                underscoreField = StringUtils.toUnderScoreCase(fieldName);
            }
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(underscoreField);
            if (fieldAndOrder.length > 1) {
                result.append(" ").append(fieldAndOrder[1].trim());
            }
        }
        return result.toString();
    }
}
