package com.seekweb4.chat.task;

import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.monitor.entity.Task;
import com.seekweb4.chat.modules.signlog.service.SignLogService;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户签到任务
 */
@DisallowConcurrentExecution
public class MemberSignTask extends Task {
    @Autowired
    private MemberService memberService;
    @Autowired
    private SignLogService signLogService;

    @Override
    public void run() {
        System.out.println("已重写签到逻辑，注释该代码");
    }
}
