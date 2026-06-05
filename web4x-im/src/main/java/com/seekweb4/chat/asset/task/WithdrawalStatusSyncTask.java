package com.seekweb4.chat.asset.task;

import com.alibaba.fastjson2.JSON;
import com.seekweb4.chat.api.config.WithdrawalConfig;
import com.seekweb4.chat.api.constant.WithdrawalRequestConstant;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.asset.util.redissson.RedissonDistributedLock;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.WithdrawApply.mapper.WithdrawApplyMapper;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.monitor.entity.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.asset.dto.response.WithdrawalResultResponseDTO;
import com.seekweb4.chat.asset.util.TaskThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author coderpwh
 */
@Slf4j
@Component
@DisallowConcurrentExecution
public class WithdrawalStatusSyncTask extends Task {

    private final RedissonDistributedLock lockTool;

    public WithdrawalStatusSyncTask(RedissonDistributedLock lockTool) {
        this.lockTool = lockTool;
    }

    @Resource
    private WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    private WithdrawalConfig withdrawalConfig;

    @Resource
    private WithdrawalRequestConstant withdrawalRequestConstant;

    @Resource
    private MemberService memberService;


    /**
     * 线程名称前缀
     */
    private static final String THREAD_PREFIX = "withdrawal-sync";

    /**
     * 每批处理的记录数
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 线程池大小
     */
    private static final int THREAD_POOL_SIZE = 10;

    /**
     * 单次任务最大执行时间(分钟)
     */
    private static final int MAX_EXECUTION_MINUTES = 10;

    /**
     * HTTP请求超时时间(秒)
     */
    private static final int HTTP_TIMEOUT_SECONDS = 5;

    /**
     * 需要同步的状态列表: 0-发起提现, 1-正在提现
     */
    private static final List<Integer> SYNC_STATUS_LIST = Arrays.asList(0, 1);

    /**
     * 线程池实例
     */
    private ExecutorService executorService;


//    @Scheduled(cron = "*/5 * * * * ?")
//    public void myTask() {
//        String lockKey = "lock:myScheduledJob";
//        RLock lock = lockTool.tryLock(lockKey, 1, 60, TimeUnit.SECONDS);
//        if (lock == null) {
//            return;
//        }
//        try {
//            // 每2分钟执行一次的代码
//            log.info("任务执行时间: " + new Date());
//        } catch (Exception e) {
//            Thread.currentThread().interrupt();
//        } finally {
//            lockTool.unlock(lock);
//
//        }
//    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void run() {
        String lockKey = "lock:withdrawal:status:task";

        long startTime = System.currentTimeMillis();
        log.info("======== 提现状态同步定时任务开始执行 ========");
        try {
            // 初始化线程池
            initThreadPool();

            // 查询待同步的提现记录总数
            int totalCount = countPendingWithdrawals();
            if (totalCount == 0) {
                log.info("暂无需要同步的提现记录");
                return;
            }

            log.info("共查询到 {} 条待同步的提现记录", totalCount);

            // 执行批量同步
            SyncResult syncResult = executeBatchSync(totalCount, startTime);

            // 输出执行结果
            logSyncResult(syncResult, startTime);

        } catch (Exception e) {
            log.error("提现状态同步任务执行异常", e);
        } finally {
            // 关闭线程池
            shutdownThreadPool();
        }
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = TaskThreadPoolUtil.builder()
                    .corePoolSize(THREAD_POOL_SIZE)
                    .maxPoolSize(THREAD_POOL_SIZE)
                    .queueCapacity(BATCH_SIZE * 2)
                    .threadPrefix(THREAD_PREFIX)
                    .build();

            log.info("线程池初始化成功: {}", THREAD_PREFIX);
        }
    }

    /**
     * 关闭线程池
     */
    private void shutdownThreadPool() {
        if (executorService != null && !executorService.isShutdown()) {
            TaskThreadPoolUtil.shutdownThreadPool(executorService, THREAD_PREFIX);
        }
    }

    /**
     * 查询待同步的提现记录总数
     */
    private int countPendingWithdrawals() {
        try {
            return withdrawApplyMapper.countPendingWithdrawals(SYNC_STATUS_LIST);
        } catch (Exception e) {
            log.error("查询待同步提现记录总数异常", e);
            return 0;
        }
    }

    /**
     * 执行批量同步
     *
     * @param totalCount 总记录数
     * @param startTime  开始时间
     * @return 同步结果
     */
    private SyncResult executeBatchSync(int totalCount, long startTime) {
        SyncResult syncResult = new SyncResult();
        int processedCount = 0;
        int batchNum = 0;

        while (processedCount < totalCount) {
            // 检查是否超时
            if (isTaskTimeout(startTime)) {
                log.error("任务执行超时，已处理 {}/{} 条记录，本次任务结束", processedCount, totalCount);
                break;
            }

            batchNum++;
            log.info("开始处理第 {} 批数据，offset: {}, size: {}", batchNum, processedCount, BATCH_SIZE);

            // 分页查询待同步记录
            List<WithdrawApply> withdrawList = queryPendingWithdrawals(processedCount);
            if (withdrawList == null || withdrawList.isEmpty()) {
                break;
            }

            // 打印线程池状态
            log.debug("当前线程池状态: {}", TaskThreadPoolUtil.getThreadPoolStatus(executorService));

            // 并发处理当前批次
            BatchResult batchResult = processBatch(withdrawList);

            // 累计结果
            syncResult.addSuccess(batchResult.getSuccessCount());
            syncResult.addFail(batchResult.getFailCount());

            processedCount += withdrawList.size();

            log.info("第 {} 批处理完成，成功: {}, 失败: {}, 累计处理: {}/{}",
                    batchNum, batchResult.getSuccessCount(), batchResult.getFailCount(),
                    processedCount, totalCount);
        }

        syncResult.setTotalCount(totalCount);
        syncResult.setProcessedCount(processedCount);

        return syncResult;
    }

    /**
     * 查询待同步的提现记录
     *
     * @param offset 偏移量
     * @return 提现记录列表
     */
    private List<WithdrawApply> queryPendingWithdrawals(int offset) {
        try {
            return withdrawApplyMapper.selectPendingWithdrawals(
                    SYNC_STATUS_LIST, offset, BATCH_SIZE);
        } catch (Exception e) {
            log.error("查询待同步提现记录异常, offset: {}", offset, e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量处理提现记录
     *
     * @param withdrawList 提现记录列表
     * @return 批次处理结果
     */
    private BatchResult processBatch(List<WithdrawApply> withdrawList) {
        BatchResult batchResult = new BatchResult();
        List<Future<Boolean>> futures = new ArrayList<>(withdrawList.size());

        // 提交所有任务到线程池
        for (WithdrawApply withdraw : withdrawList) {
            Future<Boolean> future = executorService.submit(() -> syncWithdrawalStatus(withdraw));
            futures.add(future);
        }

        // 等待所有任务完成并统计结果
        for (Future<Boolean> future : futures) {
            try {
                Boolean success = future.get(HTTP_TIMEOUT_SECONDS + 2, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(success)) {
                    batchResult.incrementSuccess();
                } else {
                    batchResult.incrementFail();
                }
            } catch (TimeoutException e) {
                log.warn("同步提现状态超时");
                batchResult.incrementFail();
                future.cancel(true);
            } catch (Exception e) {
                log.error("获取同步结果异常", e);
                batchResult.incrementFail();
            }
        }

        return batchResult;
    }

    /**
     * 同步单条提现记录状态
     *
     * @param withdraw 提现记录
     * @return 是否成功
     */
    private Boolean syncWithdrawalStatus(WithdrawApply withdraw) {
        if (withdraw == null || withdraw.getWithdrawalId() == null) {
            log.warn("提现记录为空或ID为空");
            return false;
        }

        Long withdrawalId = withdraw.getWithdrawalId();
        log.info("开始同步提现状态, withdrawalId: {}, 当前状态: {}", withdrawalId, withdraw.getStatus());

        try {
            // 构建请求参数
            Map<String, Object> params = buildRequestParams(withdrawalId);

            // 生成签名
            String signContent = SignUtil.buildSignContent(params);
            String sign = SignUtil.sign(signContent, withdrawalConfig.getPrivateKey());
            params.put("sign", sign);

            // 发送HTTP请求
            String json = new ObjectMapper().writeValueAsString(params);
            log.info("查询提现结果请求参数: {}", json);

            log.info("查询提现结果请求的URL为:{}", withdrawalRequestConstant.getWithdrawResultUrl());

            String response = HttpUtil.postJson(withdrawalRequestConstant.getWithdrawResultUrl(), json);
            log.info("查询提现结,响应结果为:{}", response);

            // 解析响应
            HttpResponseDTO httpResponse = new ObjectMapper().readValue(response, HttpResponseDTO.class);
            if (httpResponse.getCode() != 0) {
                log.error("查询提现失败, withdrawalId: {}, code: {}, msg: {}",
                        withdrawalId, httpResponse.getCode(), httpResponse.getMsg());
                return false;
            }

            // 解析并更新状态
            WithdrawalResultResponseDTO resultDTO = JSON.parseObject(
                    JSON.toJSONString(httpResponse.getData()),
                    WithdrawalResultResponseDTO.class
            );

            return updateWithdrawalStatus(withdraw, resultDTO);

        } catch (Exception e) {
            log.error("同步提现状态异常, withdrawalId: {},异常为:{}", withdrawalId, e.getMessage());
            return false;
        }
    }

    /**
     * 构建请求参数
     *
     * @param withdrawalId 提现ID
     * @return 请求参数Map
     */
    private Map<String, Object> buildRequestParams(Long withdrawalId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", withdrawalConfig.getAppId());
        params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("id", withdrawalId);
        return params;
    }

    /**
     * 更新提现状态
     *
     * @param withdraw  原提现记录
     * @param resultDTO 查询结果
     * @return 是否成功
     */
    private Boolean updateWithdrawalStatus(WithdrawApply withdraw, WithdrawalResultResponseDTO resultDTO) {
        log.info("开始更新提现状态, withdrawalId: {}, 原状态: {}, 查询结果: {}",
                withdraw.getWithdrawalId(), withdraw.getStatus(), JSON.toJSONString(resultDTO));
        if (resultDTO == null || resultDTO.getStatus() == null) {
            log.warn("查询结果为空, withdrawalId: {}", withdraw.getWithdrawalId());
            return false;
        }

        try {
            // 只有状态发生变化时才更新
            if (!resultDTO.getStatus().equals(withdraw.getStatus())) {
                WithdrawApply updateRecord = buildUpdateRecord(withdraw, resultDTO);
                int updateCount = withdrawApplyMapper.updateByPrimaryKeySelective(updateRecord);

                if (updateCount > 0) {
                    if (resultDTO.getStatus().equals(WithdrawStatusEnum.FAIL.getCode())) {
                        log.info("提现结果查询, withdrawalId: {}, 原状态: {}, 新状态: {}", withdraw.getWithdrawalId(), withdraw.getStatus(), resultDTO.getStatus());
                        //   失败场景 解除冻结
                        memberService.updateBalanceByUserId(withdraw.getAmount(), withdraw.getUserId());
                    }
                    log.info("提现状态更新成功, withdrawalId: {}, 原状态: {}, 新状态: {}", withdraw.getWithdrawalId(), withdraw.getStatus(), resultDTO.getStatus());
                    return true;
                } else {
                    log.error("提现状态更新失败, withdrawalId: {}", withdraw.getWithdrawalId());
                    return false;
                }
            } else {
                log.info("提现状态未变化, withdrawalId: {}, status: {}",
                        withdraw.getWithdrawalId(), withdraw.getStatus());
                return true;
            }
        } catch (Exception e) {
            log.error("更新提现状态异常, withdrawalId: {}", withdraw.getWithdrawalId(), e);
            return false;
        }
    }

    /**
     * 构建更新记录对象
     *
     * @param withdraw  原记录
     * @param resultDTO 查询结果
     * @return 更新记录
     */
    private WithdrawApply buildUpdateRecord(WithdrawApply withdraw, WithdrawalResultResponseDTO resultDTO) {
        WithdrawApply updateRecord = withdrawApplyMapper.selectByWithdrawalId(resultDTO.getWithdrawalId());
        updateRecord.setStatus(resultDTO.getStatus());
        updateRecord.setUpdateTime(new Date());

        // 如果有提现hash，也一并更新
        if (resultDTO.getHash() != null) {
            updateRecord.setWithdrawalHash(resultDTO.getHash());
        }

        return updateRecord;
    }

    /**
     * 检查任务是否超时
     *
     * @param startTime 开始时间
     * @return 是否超时
     */
    private boolean isTaskTimeout(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        return elapsedTime > TimeUnit.MINUTES.toMillis(MAX_EXECUTION_MINUTES);
    }

    /**
     * 记录同步结果
     *
     * @param syncResult 同步结果
     * @param startTime  开始时间
     */
    private void logSyncResult(SyncResult syncResult, long startTime) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("======== 提现状态同步任务执行完成 ========");
        log.info("总记录数: {}, 实际处理: {}, 成功: {}, 失败: {}, 耗时: {} ms",
                syncResult.getTotalCount(),
                syncResult.getProcessedCount(),
                syncResult.getSuccessCount(),
                syncResult.getFailCount(),
                duration);
    }


    /**
     * 同步结果类
     */
    private static class SyncResult {
        private int totalCount = 0;
        private int processedCount = 0;
        private int successCount = 0;
        private int failCount = 0;

        public void addSuccess(int count) {
            this.successCount += count;
        }

        public void addFail(int count) {
            this.failCount += count;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getProcessedCount() {
            return processedCount;
        }

        public void setProcessedCount(int processedCount) {
            this.processedCount = processedCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailCount() {
            return failCount;
        }
    }

    /**
     * 批次处理结果类
     */
    private static class BatchResult {
        private int successCount = 0;
        private int failCount = 0;

        public void incrementSuccess() {
            this.successCount++;
        }

        public void incrementFail() {
            this.failCount++;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailCount() {
            return failCount;
        }
    }
}
