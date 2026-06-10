package com.seekweb4.chat.common.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 后台列表排序：前端/bootstrap-table 传驼峰字段，MyBatis XML 使用数据库下划线列名。
 */
public final class OrderByUtils {

    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = Collections.unmodifiableSet(new HashSet<>(
            Collections.singletonList("coinId")
    ));

    private OrderByUtils() {
    }

    public static String toUnderscoreColumn(String orderBy) {
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
