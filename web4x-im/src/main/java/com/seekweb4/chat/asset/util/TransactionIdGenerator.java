package com.seekweb4.chat.asset.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 交易流水号生成工具类
 * 格式: YYYYMMDD + 机器ID(2位) + 序列号(10位) + 随机数(4位)
 * 总长度: 8 + 2 + 10 + 4 = 24位 (< 32位)
 *
 * 特点:
 * 1. 线程安全 - 使用AtomicLong保证并发安全
 * 2. 唯一性 - 日期+机器ID+序列号+随机数组合确保唯一
 * 3. 高性能 - 无锁设计,支持高并发
 * 4. 时间有序 - 前8位为日期,便于按日期查询和归档
 * @author coderpwh
 */
public class TransactionIdGenerator {

    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 机器ID (可根据实际情况配置,范围00-99)
    private final String machineId;

    // 序列号计数器 (使用AtomicLong保证线程安全)
    private final AtomicLong sequence = new AtomicLong(0);

    // 序列号最大值 (10位数字,最大9999999999)
    private static final long MAX_SEQUENCE = 9999999999L;

    // 当前日期缓存
    private volatile String currentDate;

    // 单例模式 - 懒汉式(线程安全)
    private static volatile TransactionIdGenerator instance;

    /**
     * 私有构造函数
     * @param machineId 机器ID (00-99)
     */
    private TransactionIdGenerator(int machineId) {
        if (machineId < 0 || machineId > 99) {
            throw new IllegalArgumentException("机器ID必须在0-99之间");
        }
        this.machineId = String.format("%02d", machineId);
        this.currentDate = getCurrentDate();
    }

    /**
     * 获取单例实例
     * @param machineId 机器ID
     * @return TransactionIdGenerator实例
     */
    public static TransactionIdGenerator getInstance(int machineId) {
        if (instance == null) {
            synchronized (TransactionIdGenerator.class) {
                if (instance == null) {
                    instance = new TransactionIdGenerator(machineId);
                }
            }
        }
        return instance;
    }

    /**
     * 获取默认实例 (机器ID默认为01)
     */
    public static TransactionIdGenerator getInstance() {
        return getInstance(1);
    }

    /**
     * 生成交易流水号
     * 格式: YYYYMMDD(8位) + 机器ID(2位) + 序列号(10位) + 随机数(4位)
     *
     * @return 交易流水号
     */
    public synchronized String generate() {
        // 获取当前日期
        String date = getCurrentDate();

        // 如果日期发生变化,重置序列号
        if (!date.equals(currentDate)) {
            currentDate = date;
            sequence.set(0);
        }

        // 获取序列号并自增
        long seq = sequence.getAndIncrement();

        // 如果序列号超过最大值,抛出异常
        if (seq > MAX_SEQUENCE) {
            throw new RuntimeException("当日流水号已达上限,请明天再试或增加机器节点");
        }

        // 生成4位随机数
        int random = (int) (Math.random() * 10000);

        // 组装流水号: 日期(8) + 机器ID(2) + 序列号(10) + 随机数(4)
        return String.format("%s%s%010d%04d", date, machineId, seq, random);
    }

    /**
     * 批量生成交易流水号
     * @param count 生成数量
     * @return 流水号数组
     */
    public String[] generateBatch(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }

        String[] ids = new String[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generate();
        }
        return ids;
    }

    /**
     * 获取当前日期字符串
     */
    private String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMAT);
    }

    /**
     * 解析流水号中的日期
     * @param transactionId 交易流水号
     * @return 日期字符串 (YYYYMMDD)
     */
    public static String parseDate(String transactionId) {
        if (transactionId == null || transactionId.length() < 8) {
            throw new IllegalArgumentException("无效的交易流水号");
        }
        return transactionId.substring(0, 8);
    }

    /**
     * 解析流水号中的机器ID
     * @param transactionId 交易流水号
     * @return 机器ID
     */
    public static String parseMachineId(String transactionId) {
        if (transactionId == null || transactionId.length() < 10) {
            throw new IllegalArgumentException("无效的交易流水号");
        }
        return transactionId.substring(8, 10);
    }

    /**
     * 验证流水号格式是否正确
     * @param transactionId 交易流水号
     * @return true-有效, false-无效
     */
    public static boolean validate(String transactionId) {
        if (transactionId == null || transactionId.length() != 24) {
            return false;
        }
        return transactionId.matches("\\d{24}");
    }

    /**
     * 重置序列号 (谨慎使用,仅用于测试或特殊场景)
     */
    public synchronized void reset() {
        sequence.set(0);
        currentDate = getCurrentDate();
    }

    // ==================== 测试示例 ====================
    public static void main(String[] args) throws InterruptedException {
        // 创建生成器实例 (机器ID为01)
        TransactionIdGenerator generator = TransactionIdGenerator.getInstance(1);

        System.out.println("=== 单线程生成测试 ===");
        for (int i = 0; i < 5; i++) {
            String id = generator.generate();
            System.out.println("流水号: " + id + " (长度: " + id.length() + ")");
            System.out.println("  - 日期: " + TransactionIdGenerator.parseDate(id));
            System.out.println("  - 机器ID: " + TransactionIdGenerator.parseMachineId(id));
            System.out.println("  - 有效性: " + TransactionIdGenerator.validate(id));
        }

        System.out.println("\n=== 批量生成测试 ===");
        String[] batchIds = generator.generateBatch(3);
        for (String id : batchIds) {
            System.out.println("批量流水号: " + id);
        }

        System.out.println("\n=== 多线程并发测试 ===");
        // 创建10个线程,每个线程生成5个流水号
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    String id = generator.generate();
                    System.out.println("线程" + threadId + " - " + id);
                }
            });
            threads[i].start();
        }

        // 等待所有线程执行完毕
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n=== 测试完成 ===");
    }
}