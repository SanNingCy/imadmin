package com.seekweb4.chat.modules.chainPayOrder.service.impl;

import com.seekweb4.chat.common.utils.OrderByUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.chainPayOrder.dto.ChainPayOrderQueryDto;
import com.seekweb4.chat.modules.chainPayOrder.entity.ChainPayOrder;
import com.seekweb4.chat.modules.chainPayOrder.mapper.ChainPayOrderMapper;
import com.seekweb4.chat.modules.chainPayOrder.service.ChainPayOrderAdminService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ChainPayOrderAdminServiceImpl implements ChainPayOrderAdminService {

    private static final Set<String> ORDER_COLUMNS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "id", "order_id", "user_id", "scene", "chain_id", "amount", "odic_amount", "raw_amount", "token_symbol",
            "token_address", "payment_type", "tx_hash", "user_address", "status", "reconcile_status",
            "expire_time", "pay_time", "complete_time", "create_time", "update_time"
    )));

    @Autowired
    private ChainPayOrderMapper chainPayOrderMapper;

    @Autowired
    private MemberService memberService;

    @Override
    public Page<ChainPayOrder> page(ChainPayOrderQueryDto queryDto) {
        Page<ChainPayOrder> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        int pageNo = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int pageSize = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        queryDto.setPageNo((pageNo - 1) * pageSize);
        queryDto.setPageSize(pageSize);

        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(OrderByUtils.sanitizeOrderBy(queryDto.getOrderBy(), ORDER_COLUMNS));
        }

        Long count = chainPayOrderMapper.selectAdminCount(queryDto);
        page.setCount(count);

        List<ChainPayOrder> list = chainPayOrderMapper.selectAdminPageList(queryDto);
        if (list != null) {
            for (ChainPayOrder item : list) {
                fillMemberInfo(item);
            }
        }
        page.setList(list);
        return page;
    }

    @Override
    public ChainPayOrder getById(String id) {
        ChainPayOrder order = chainPayOrderMapper.selectByPrimaryKey(id);
        fillMemberInfo(order);
        return order;
    }

    private void fillMemberInfo(ChainPayOrder order) {
        if (order == null || StringUtils.isBlank(order.getUserId())) {
            return;
        }
        Member member = memberService.selectBasicById(order.getUserId());
        if (member == null) {
            return;
        }
        order.setIdno(member.getIdno());
        order.setNickname(member.getNickname());
    }
}
