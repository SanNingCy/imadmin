package com.seekweb4.chat.modules.member.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 谷歌验证服务类
 * @author system
 * @version 2024-12-10
 */
@Service
@Transactional(readOnly = true)
public class TwoFactorService {

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 查询已启用谷歌验证的用户列表（分页）
     * @param page 分页对象
     * @param member 查询条件
     * @return 分页结果
     */
    public Page<Member> findPage(Page<Member> page, Member member) {
        member.setPage(page);
        List<Member> list = memberMapper.findListWithTwoFactor(member);
        page.setList(list);
        return page;
    }

    /**
     * 重置用户的谷歌验证码
     * @param id 用户ID
     * @return 是否成功
     */
    @Transactional(readOnly = false)
    public boolean resetTwoFactorCode(String id) {
        int result = memberMapper.resetTwoFactorCode(id);
        return result > 0;
    }
}

