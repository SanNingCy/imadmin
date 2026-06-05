package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 靓号到期处理
 */
@Component
@Slf4j
public class LianghaoDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private MemberService memberService;

    @Override
    public void invoke(String id) {
        memberService.executeUpdateSql("update t_member set lianghao = '',endtime = null where id = '"+id+"'");
    }
}
