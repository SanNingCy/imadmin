package com.seekweb4.chat.asset.util;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定时任务线程池工具类
 * 用于管理定时任务中的线程池资源
 *
 * @author coderpwh
 */
@Slf4j
public class TaskThreadPoolUtil {

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * 默认最大线程数
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 10;

    /**
     * 线程空闲时间（秒）
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * 默认队列容量
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 200;

    /**
     * 线程池关闭等待时间（秒）
     */
    private static final int SHUTDOWN_TIMEOUT = 30;

    /**
     * 私有构造函数，防止实例化
     */
    private TaskThreadPoolUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 创建固定大小的线程池
     *
     * @param poolSize     线程池大小
     * @param threadPrefix 线程名称前缀
     * @return ExecutorService
     */
    public static ExecutorService createFixedThreadPool(int poolSize, String threadPrefix) {
        return createThreadPool(poolSize, poolSize, DEFAULT_QUEUE_CAPACITY, threadPrefix);
    }

    /**
     * 创建自定义线程池
     *
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param queueCapacity 队列容量
     * @param threadPrefix  线程名称前缀
     * @return ExecutorService
     */
    public static ExecutorService createThreadPool(int corePoolSize,
                                                   int maxPoolSize,
                                                   int queueCapacity,
                                                   String threadPrefix) {
        if (corePoolSize <= 0 || maxPoolSize <= 0 || corePoolSize > maxPoolSize) {
            throw new IllegalArgumentException("Invalid thread pool parameters");
        }

        log.info("创建线程池: threadPrefix={}, corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                threadPrefix, corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new CustomThreadFactory(threadPrefix),
                new CustomRejectedExecutionHandler(threadPrefix)
        );

        // 允许核心线程超时
        executor.allowCoreThreadTimeOut(true);

        return executor;
    }

    /**
     * 创建默认配置的线程池
     *
     * @param threadPrefix 线程名称前缀
     * @return ExecutorService
     */
    public static ExecutorService createDefaultThreadPool(String threadPrefix) {
        return createThreadPool(
                DEFAULT_CORE_POOL_SIZE,
                DEFAULT_MAX_POOL_SIZE,
                DEFAULT_QUEUE_CAPACITY,
                threadPrefix
        );
    }

    /**
     * 优雅关闭线程池
     *
     * @param executor     线程池
     * @param threadPrefix 线程名称前缀（用于日志）
     */
    public static void shutdownThreadPool(ExecutorService executor, String threadPrefix) {
        if (executor == null || executor.isShutdown()) {
            return;
        }

        log.info("开始关闭线程池: {}", threadPrefix);

        try {
            // 停止接收新任务
            executor.shutdown();

            // 等待已提交任务完成
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                log.warn("线程池在{}秒内未完成关闭，尝试强制关闭: {}", SHUTDOWN_TIMEOUT, threadPrefix);

                // 取消正在执行的任务
                executor.shutdownNow();

                // 再次等待任务响应取消信号
                if (!executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                    log.error("线程池无法完全关闭: {}", threadPrefix);
                }
            }

            log.info("线程池关闭成功: {}", threadPrefix);

        } catch (InterruptedException e) {
            log.error("关闭线程池时被中断: {}", threadPrefix, e);

            // 重新尝试取消任务
            executor.shutdownNow();

            // 保留中断状态
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取线程池状态信息
     *
     * @param executor 线程池
     * @return 状态描述
     */
    public static String getThreadPoolStatus(ExecutorService executor) {
        if (executor == null) {
            return "ThreadPool is null";
        }

        if (!(executor instanceof ThreadPoolExecutor)) {
            return "Not a ThreadPoolExecutor instance";
        }

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;

        return String.format(
                "ThreadPool[Active: %d, Completed: %d, Task: %d, Queue: %d, Pool: %d/%d]",
                tpe.getActiveCount(),
                tpe.getCompletedTaskCount(),
                tpe.getTaskCount(),
                tpe.getQueue().size(),
                tpe.getPoolSize(),
                tpe.getMaximumPoolSize()
        );
    }

    /**
     * 自定义线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        public CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());

            // 设置为非守护线程，确保任务执行完成
            thread.setDaemon(false);

            // 设置优先级为普通优先级
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }

            return thread;
        }
    }

    /**
     * 自定义拒绝策略
     * 当队列满时，使用调用者线程执行任务
     */
    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        private final String threadPrefix;

        public CustomRejectedExecutionHandler(String threadPrefix) {
            this.threadPrefix = threadPrefix;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("线程池队列已满，任务被拒绝: {}, 将在调用者线程中执行", threadPrefix);

            // 如果线程池没有关闭，则在调用者线程中执行任务
            if (!executor.isShutdown()) {
                try {
                    r.run();
                } catch (Exception e) {
                    log.error("调用者线程执行任务异常: {}", threadPrefix, e);
                }
            }
        }
    }

    /**
     * 线程池配置构建器
     */
    public static class ThreadPoolBuilder {

        private int corePoolSize = DEFAULT_CORE_POOL_SIZE;

        private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;

        private String threadPrefix;

        public ThreadPoolBuilder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public ThreadPoolBuilder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public ThreadPoolBuilder queueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        public ThreadPoolBuilder threadPrefix(String threadPrefix) {
            this.threadPrefix = threadPrefix;
            return this;
        }

        public ExecutorService build() {
            if (threadPrefix == null || threadPrefix.trim().isEmpty()) {
                throw new IllegalArgumentException("Thread prefix cannot be null or empty");
            }

            return createThreadPool(corePoolSize, maxPoolSize, queueCapacity, threadPrefix);
        }
    }

    /**
     * 创建线程池构建器
     *
     * @return ThreadPoolBuilder
     */
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }
}