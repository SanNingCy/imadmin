package com.seekweb4.chat.modules.member.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 密保问题管理服务类
 * @author system
 * @version 2024-12-10
 */
@Service
@Transactional(readOnly = true)
public class MibaoService {

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 根据用户ID查询用户（包含密保字段）
     * @param id 用户ID
     * @return 用户实体
     */
    public Member getById(String id) {
        return memberMapper.getMibaoId(id);
    }

    /**
     * 查询已设置密保问题的用户列表（分页）
     * @param page 分页对象
     * @param member 查询条件
     * @return 分页结果
     */
    public Page<Member> findPage(Page<Member> page, Member member) {
        member.setPage(page);
        List<Member> list = memberMapper.findListWithMibao(member);
        page.setList(list);
        return page;
    }

    /**
     * 修改用户的密保问题
     * @param id 用户ID
     * @param mbid 密保问题ID
     * @param mbname 密保问题名称
     * @param mbda 密保答案
     * @return 是否成功
     */
    @Transactional(readOnly = false)
    public boolean updateMibao(String id, String mbid, String mbname, String mbda) {
        int result = memberMapper.updateMibao(id, mbid, mbname, mbda);
        return result > 0;
    }

    /**
     * 重置用户的密保问题
     * @param id 用户ID
     * @return 是否成功
     */
    @Transactional(readOnly = false)
    public boolean resetMibao(String id) {
        int result = memberMapper.resetMibao(id);
        return result > 0;
    }
}

