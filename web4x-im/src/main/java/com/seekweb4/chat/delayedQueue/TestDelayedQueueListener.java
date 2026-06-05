package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试监听过期
 */
@Component
@Slf4j
public class TestDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private MemberService memberService;

    @Override
    public void invoke(String id) {
        Member member = memberService.get(id);
        if (member != null) {
            member.setState("1");
            memberService.save(member);
        }
    }
}
