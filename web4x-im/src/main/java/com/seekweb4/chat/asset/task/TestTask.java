package com.seekweb4.chat.asset.task;

import com.seekweb4.chat.modules.monitor.entity.Task;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class TestTask extends Task {
    @Override
    public void run() {
        System.out.println("这是测试代码");
    }
}
