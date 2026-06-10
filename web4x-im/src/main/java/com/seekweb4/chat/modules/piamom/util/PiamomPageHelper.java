package com.seekweb4.chat.modules.piamom.util;

import com.seekweb4.chat.common.utils.OrderByUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.piamom.dto.PiamomAdminPageQueryDto;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * 后台分页工具
 */
public final class PiamomPageHelper {

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
            queryDto.setOrderBy(OrderByUtils.toUnderscoreColumn(queryDto.getOrderBy()));
        }
        page.setCount(countFn.applyAsLong(queryDto));
        page.setList(listFn.apply(queryDto));
        return page;
    }

    public static String convertOrderByToUnderscore(String orderBy) {
        return OrderByUtils.toUnderscoreColumn(orderBy);
    }
}
