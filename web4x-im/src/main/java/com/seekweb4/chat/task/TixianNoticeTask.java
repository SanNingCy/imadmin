package com.seekweb4.chat.task;

import com.seekweb4.chat.common.websocket.service.system.SystemInfoSocketHandler;
import com.seekweb4.chat.modules.monitor.entity.Task;
import com.seekweb4.chat.modules.tixian.entity.Tixian;
import com.seekweb4.chat.modules.tixian.service.TixianService;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 提现通知
 */
@DisallowConcurrentExecution
public class TixianNoticeTask extends Task {
    @Autowired
    private TixianService tixianService;
    @Resource
    private SystemInfoSocketHandler systemInfoSocketHandler;

    @Override
    public void run() {
        Tixian log = new Tixian();
        log.setState("1");
        List<Tixian> list = tixianService.findList(log);
        for(Tixian ll:list){
            systemInfoSocketHandler.sendMessageToUser("admin", "用户（"+ll.getU().getNickname()+"）充值"+ll.getMoney());
        }
    }
}
