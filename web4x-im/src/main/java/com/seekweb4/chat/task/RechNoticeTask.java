package com.seekweb4.chat.task;

import com.seekweb4.chat.common.websocket.service.system.SystemInfoSocketHandler;
import com.seekweb4.chat.modules.monitor.entity.Task;
import com.seekweb4.chat.modules.rechagelog.entity.RechageLog;
import com.seekweb4.chat.modules.rechagelog.service.RechageLogService;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 充值通知
 */
@DisallowConcurrentExecution
public class RechNoticeTask extends Task {
    @Autowired
    private RechageLogService rechageLogService;
    @Resource
    private SystemInfoSocketHandler systemInfoSocketHandler;

    @Override
    public void run() {
        RechageLog log = new RechageLog();
        log.setState("1");
        List<RechageLog> list = rechageLogService.findList(log);
        for(RechageLog ll:list){
            systemInfoSocketHandler.sendMessageToUser("admin", "用户（"+ll.getU().getNickname()+"）充值"+ll.getMoney());
        }
    }
}
