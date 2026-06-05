package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ai返回图片
 */
@Component
@Slf4j
public class VipEndDelayedQueueListener implements RedisDelayedQueueListener<String> {

    @Autowired
    private MemberService memberService;
    @Autowired
    private FriendService friendService;

    @Override
    public void invoke(String id) {
        memberService.executeUpdateSql("update t_member set isvip = '0',viptime = null,autofanyi = '0',biandabianyi = '0' where id = '"+id+"'");
        friendService.executeUpdateSql("update t_friend set isfanyi = '0' where uid = '"+id+"'");

    }
}
