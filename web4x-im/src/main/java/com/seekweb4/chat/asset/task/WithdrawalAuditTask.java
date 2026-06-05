package com.seekweb4.chat.asset.task;

import com.seekweb4.chat.asset.util.redissson.RedissonDistributedLock;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.assetAdmin.service.impl.AssetAdminService;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.mapper.ButtonConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 提现自动审核定时任务：仅当 withdraw_audit 按钮为 0（关闭）时执行。
 * 查询状态为 4（申请中）的提现记录，自动调用审核通过并请求外部提现接口。
 * 当 withdraw_audit=1（开启）时，走手动审核+谷歌验证码，本任务不执行。
 */
@Slf4j
@Component
public class WithdrawalAuditTask {

    private final RedissonDistributedLock lockTool;

    public WithdrawalAuditTask(RedissonDistributedLock lockTool) {
        this.lockTool = lockTool;
    }

    @Resource
    private AssetAdminService assetAdminService;

    @Resource
    private ButtonConfigMapper buttonConfigMapper;

    private static final String LOCK_KEY = "lock:withdrawal:audit:task";
    /** 提现审核模式：0=自动审核(本任务执行)，1=手动审核(需谷歌，本任务不执行) */
    private static final String BUTTON_KEY_WITHDRAW_AUDIT = "withdraw_audit";

    @Scheduled(cron = "0 * * * * ?")
    public void run() {
        // 仅当 withdraw_audit=0（自动审核模式）时执行
        ButtonConfig auditConfig = buttonConfigMapper.selectByButtonKey(BUTTON_KEY_WITHDRAW_AUDIT);
        if (auditConfig != null && auditConfig.getButtonStatus() != null && auditConfig.getButtonStatus() == 1) {
            log.debug("提现审核模式为手动(需谷歌)，自动审核任务跳过");
            return;
        }
        org.redisson.api.RLock lock = lockTool.tryLockNow(LOCK_KEY, 8, TimeUnit.MINUTES);
        if (lock == null) {
            log.debug("提现自动审核任务未获取到锁，跳过本次执行");
            return;
        }
        try {
            log.info("======== 提现自动审核定时任务开始执行 ========");
            List<WithdrawApply> list = assetAdminService.listWithdrawApplyByStatus(WithdrawStatusEnum.APPLY.getCode());
            if (list == null || list.isEmpty()) {
                log.info("暂无待审核(status=4)的提现记录");
                return;
            }
            log.info("共查询到 {} 条待审核提现记录，开始自动通过并调用外部接口", list.size());
            int success = 0;
            int fail = 0;
            for (WithdrawApply apply : list) {
                try {
                    Boolean ok = assetAdminService.auditWithdraw(
                            apply.getId(),
                            WithdrawStatusEnum.INITIATED.getCode(),
                            null,
                            "scheduledTask"
                    );
                    if (Boolean.TRUE.equals(ok)) {
                        success++;
                        log.info("自动审核通过, id: {}, transactionNumber: {}", apply.getId(), apply.getTransactionNumber());
                    } else {
                        fail++;
                        log.warn("自动审核未通过, id: {}", apply.getId());
                    }
                } catch (Exception e) {
                    fail++;
                    log.error("自动审核异常, id: {}, transactionNumber: {}, error: {}", apply.getId(), apply.getTransactionNumber(), e.getMessage(), e);
                }
            }
            log.info("======== 提现自动审核任务结束, 成功: {}, 失败: {} ========", success, fail);
        } catch (Exception e) {
            log.error("提现自动审核任务执行异常", e);
        } finally {
            lockTool.unlock(lock);
        }
    }
}
