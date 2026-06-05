package com.seekweb4.chat.task;

import com.seekweb4.chat.api.utils.MySQLBackupUtil;
import com.seekweb4.chat.modules.monitor.entity.Task;
import org.quartz.DisallowConcurrentExecution;

/**
 * MySQL定时备份任务
 */
@DisallowConcurrentExecution
public class MySQLBackupTask extends Task {
    @Override
    public void run() {
        MySQLBackupUtil.startBackUp();
    }
}
