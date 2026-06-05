package com.seekweb4.chat.agora.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Redis操作工具类
 * 
 * <p>该工具类封装了Redis的常用操作，提供简洁易用的API接口。</p>
 * <p>支持字符串、哈希表、有序集合等多种数据结构，以及分布式锁功能。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>分布式锁管理 - 支持加锁、解锁、尝试加锁等操作</li>
 *   <li>字符串操作 - 基础的键值对存取操作</li>
 *   <li>哈希表操作 - 支持哈希表的增删改查</li>
 *   <li>有序集合操作 - 支持排序和范围查询</li>
 *   <li>过期时间管理 - 支持设置和获取键的过期时间</li>
 * </ul>
 * 
 * <p><strong>数据结构支持：</strong></p>
 * <ul>
 *   <li><strong>String：</strong>基础的键值对存储</li>
 *   <li><strong>Hash：</strong>哈希表存储，适合存储对象</li>
 *   <li><strong>ZSet：</strong>有序集合，支持按分数排序</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * @Autowired
 * private RedisUtil redisUtil;
 * 
 * // 字符串操作
 * redisUtil.set("user:123", "张三", 3600);
 * String username = redisUtil.get("user:123");
 * 
 * // 哈希表操作
 * redisUtil.hashPut("user:profile:123", "name", "张三");
 * Object name = redisUtil.hashGet("user:profile:123", "name");
 * 
 * // 分布式锁
 * String lockKey = "lock:user:123";
 * if (redisUtil.tryLock(lockKey)) {
 *     try {
 *         // 执行需要锁保护的业务逻辑
 *     } finally {
 *         redisUtil.unlock(lockKey);
 *     }
 * }
 * }
 * </pre>
 * 
 * <p><strong>应用场景：</strong></p>
 * <ul>
 *   <li>缓存管理 - 提高数据访问性能</li>
 *   <li>会话存储 - 用户登录状态管理</li>
 *   <li>分布式锁 - 防止并发操作冲突</li>
 *   <li>计数器 - 访问量、点赞数等统计</li>
 *   <li>排行榜 - 使用有序集合实现排名功能</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 * @see StringRedisTemplate
 * @see RedisTemplate
 * @see RedisLockRegistry
 */
@Slf4j
@AllArgsConstructor
public class RedisUtil {
    
    /**
     * 字符串操作模板，用于处理String类型的数据
     */
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 通用Redis操作模板，支持Object类型数据的序列化
     */
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Redis分布式锁注册表，用于管理分布式锁
     */
    private final RedisLockRegistry redisLockRegistry;

    // =========================== 分布式锁相关操作 ===========================
    
    /**
     * 获取分布式锁（阻塞式）
     * 
     * <p>该方法会一直阻塞直到获取到锁为止，使用时需要确保在finally块中释放锁。</p>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>防止重复操作 - 如重复下单、重复支付</li>
     *   <li>资源独占访问 - 如文件写入、数据库更新</li>
     *   <li>计数器安全增减 - 库存扣减等操作</li>
     * </ul>
     * 
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>必须在finally块中调用unlock释放锁</li>
     *   <li>避免长时间持有锁影响性能</li>
     *   <li>考虑锁的超时机制防止死锁</li>
     * </ul>
     * 
     * @param lockKey 锁的唯一标识，建议使用业务相关的命名规则，如"lock:order:123"
     */
    public void lock(String lockKey) {
        Lock lock = obtainLock(lockKey);
        lock.lock();
    }

    /**
     * 获取锁实例
     * 
     * <p>从Redis锁注册表中获取指定键的锁实例。</p>
     * 
     * @param lockKey 锁的唯一标识
     * @return Lock对象实例
     */
    private Lock obtainLock(String lockKey) {
        return redisLockRegistry.obtain(lockKey);
    }

    /**
     * 尝试获取分布式锁（非阻塞式）
     * 
     * <p>立即返回锁获取结果，不会阻塞等待。</p>
     * 
     * <p><strong>适用场景：</strong></p>
     * <ul>
     *   <li>可选性操作 - 如果无法获取锁则跳过操作</li>
     *   <li>高并发场景 - 避免大量线程阻塞等待</li>
     *   <li>定时任务 - 防止任务重复执行</li>
     * </ul>
     * 
     * @param lockKey 锁的唯一标识
     * @return true表示成功获取锁，false表示锁被其他线程持有
     */
    public boolean tryLock(String lockKey) {
        Lock lock = obtainLock(lockKey);
        return lock.tryLock();
    }

    /**
     * 尝试获取分布式锁（带超时时间）
     * 
     * <p>在指定时间内尝试获取锁，超时后返回失败结果。</p>
     * 
     * <p><strong>推荐使用场景：</strong></p>
     * <ul>
     *   <li>需要等待但有时间限制的操作</li>
     *   <li>平衡阻塞和性能的场景</li>
     *   <li>用户交互相关的操作</li>
     * </ul>
     * 
     * @param lockKey 锁的唯一标识
     * @param waitSecond 等待时间（秒），建议设置合理的超时时间，如3-10秒
     * @return true表示在指定时间内成功获取锁，false表示超时或被中断
     */
    public boolean tryLock(String lockKey, long waitSecond) {
        Lock lock = obtainLock(lockKey);
        try {
            return lock.tryLock(waitSecond, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("tryLock exception, lockKey:{}, waitSecond:{}, error:", lockKey, waitSecond, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     * 
     * <p>释放当前线程持有的锁，使其他等待的线程能够获取锁。</p>
     * 
     * <p><strong>重要提示：</strong></p>
     * <ul>
     *   <li>必须在获取锁的同一线程中调用</li>
     *   <li>建议在finally块中调用确保锁被释放</li>
     *   <li>不要释放未持有的锁</li>
     * </ul>
     * 
     * @param lockKey 锁的唯一标识
     * @return true表示成功释放锁，false表示释放过程中出现异常
     */
    public boolean unlock(String lockKey) {
        try {
            Lock lock = obtainLock(lockKey);
            lock.unlock();
        } catch (Exception e) {
            log.error("unlock exception, lockKey:{}, error:", lockKey, e);
            return false;
        }
        return true;
    }

    // =========================== 基础字符串操作 ===========================

    /**
     * 删除指定键
     * 
     * <p>从Redis中删除指定的键及其对应的值。</p>
     * 
     * @param key 要删除的键
     * @return true表示删除成功，false表示键不存在
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 获取字符串值
     * 
     * <p>根据键获取对应的字符串值。</p>
     * 
     * @param key 键名
     * @return 对应的字符串值，不存在时返回null
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取键的过期时间
     * 
     * <p>获取指定键的剩余生存时间（TTL）。</p>
     * 
     * @param key 键名
     * @return 剩余过期时间（秒），-1表示永不过期，-2表示键不存在
     */
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 设置键的过期时间
     * 
     * <p>为已存在的键设置过期时间。</p>
     * 
     * @param key 键名
     * @param expire 过期时间（秒），大于0的整数
     * @return true表示设置成功，false表示设置失败
     */
    public boolean expire(String key, long expire) {
        return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 检查键是否存在
     * 
     * <p>判断指定的键是否在Redis中存在。</p>
     * 
     * @param key 要检查的键
     * @return true表示键存在，false表示键不存在
     */
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 设置字符串值（带过期时间）
     * 
     * <p>设置键值对并指定过期时间，常用于缓存场景。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * // 缓存用户信息1小时
     * redisUtil.set("user:profile:123", userJson, 3600);
     * 
     * // 缓存验证码5分钟
     * redisUtil.set("captcha:phone:13800138000", "1234", 300);
     * </pre>
     * 
     * @param key 键名，建议使用清晰的命名规则
     * @param val 字符串值
     * @param expire 过期时间（秒），建议根据业务需求合理设置
     */
    public void set(String key, String val, long expire) {
        stringRedisTemplate.opsForValue().set(key, val, expire, TimeUnit.SECONDS);
    }

    // =========================== 哈希表操作 ===========================

    /**
     * 删除哈希表中的字段
     * 
     * <p>从哈希表中删除一个或多个指定的字段。</p>
     * 
     * @param key 哈希表的键名
     * @param hashKeys 要删除的字段名数组
     * @return 成功删除的字段数量
     */
    public Long hashDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 获取哈希表的所有字段和值
     * 
     * <p>返回哈希表中所有的字段-值对。</p>
     * 
     * @param key 哈希表的键名
     * @return 包含所有字段-值对的Map
     */
    public Map<Object, Object> hashEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取哈希表中指定字段的值
     * 
     * <p>从哈希表中获取指定字段的值。</p>
     * 
     * @param key 哈希表的键名
     * @param hashKey 字段名
     * @return 字段对应的值，字段不存在时返回null
     */
    public Object hashGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 检查哈希表中是否存在指定字段
     * 
     * <p>判断哈希表中是否包含指定的字段。</p>
     * 
     * @param key 哈希表的键名
     * @param hashKey 要检查的字段名
     * @return true表示字段存在，false表示字段不存在
     */
    public Boolean hashHasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 批量获取哈希表中多个字段的值
     * 
     * <p>一次性获取哈希表中多个字段的值，提高操作效率。</p>
     * 
     * @param key 哈希表的键名
     * @param hashKeys 要获取的字段名集合
     * @return 对应字段值的列表，顺序与输入的字段名顺序一致
     */
    public List<Object> hashMultiGet(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 获取哈希表的所有字段名
     * 
     * <p>返回哈希表中所有字段的名称集合。</p>
     * 
     * @param key 哈希表的键名
     * @return 所有字段名的Set集合
     */
    public Set<Object> hashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 设置哈希表中字段的值
     * 
     * <p>在哈希表中设置指定字段的值，如果字段不存在则创建。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * // 存储用户基本信息
     * redisUtil.hashPut("user:profile:123", "name", "张三");
     * redisUtil.hashPut("user:profile:123", "age", 25);
     * redisUtil.hashPut("user:profile:123", "email", "zhangsan@example.com");
     * </pre>
     * 
     * @param key 哈希表的键名
     * @param hashKey 字段名
     * @param value 字段值
     */
    public void hashPut(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 仅当字段不存在时设置哈希表字段的值
     * 
     * <p>只有当指定字段不存在时才设置值，适用于防止覆盖的场景。</p>
     * 
     * @param key 哈希表的键名
     * @param hashKey 字段名
     * @param value 字段值
     * @return true表示字段不存在且设置成功，false表示字段已存在
     */
    public Boolean hashPutIfAbsent(String key, Object hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 获取哈希表的字段数量
     * 
     * <p>返回哈希表中字段的总数。</p>
     * 
     * @param key 哈希表的键名
     * @return 哈希表中字段的数量
     */
    public Long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表的所有值
     * 
     * <p>返回哈希表中所有字段的值列表。</p>
     * 
     * @param key 哈希表的键名
     * @return 所有字段值的列表
     */
    public List<Object> hashValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    // =========================== 有序集合操作 ===========================

    /**
     * 向有序集合添加元素
     * 
     * <p>向有序集合中添加一个元素，如果元素已存在则更新其分数。</p>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>排行榜 - score为分数，value为用户ID</li>
     *   <li>时间线 - score为时间戳，value为事件ID</li>
     *   <li>优先队列 - score为优先级</li>
     * </ul>
     * 
     * @param key 有序集合的键名
     * @param value 元素值
     * @param score 元素分数，用于排序
     * @return true表示新增元素，false表示更新已存在元素的分数
     */
    public Boolean zsetAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 仅当元素不存在时添加到有序集合
     * 
     * <p>只有当元素不存在于有序集合中时才添加，避免覆盖现有元素。</p>
     * 
     * @param key 有序集合的键名
     * @param value 元素值
     * @param score 元素分数
     * @return true表示元素不存在且添加成功，false表示元素已存在
     */
//    public Boolean zsetAddIfAbsent(String key, Object value, double score) {
//        return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
//    }

    /**
     * 移除并返回有序集合中分数最高的元素
     * 
     * <p>原子性地移除有序集合中分数最高的元素并返回。</p>
     * 
     * <p><strong>适用场景：</strong></p>
     * <ul>
     *   <li>优先队列 - 获取最高优先级任务</li>
     *   <li>排行榜 - 获取并移除第一名</li>
     * </ul>
     * 
     * @param key 有序集合的键名
     * @return 分数最高的元素及其分数，集合为空时返回null
     */
//    public TypedTuple<Object> zsetPopMax(String key) {
//        return redisTemplate.opsForZSet().popMax(key);
//    }

    /**
     * 按排名范围获取有序集合元素
     * 
     * <p>获取有序集合中指定排名范围的元素（按分数从小到大排序）。</p>
     * 
     * @param key 有序集合的键名
     * @param start 起始位置（0表示第一个）
     * @param end 结束位置（-1表示最后一个）
     * @return 指定范围内的元素集合
     */
    public Set<Object> zsetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 按分数范围获取有序集合元素
     * 
     * <p>获取有序集合中分数在指定范围内的所有元素。</p>
     * 
     * @param key 有序集合的键名
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 分数在指定范围内的元素集合
     */
    public Set<Object> zsetRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 按排名范围获取有序集合元素（倒序）
     * 
     * <p>获取有序集合中指定排名范围的元素（按分数从大到小排序）。</p>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>排行榜 - 获取前N名用户</li>
     *   <li>最新内容 - 按时间倒序获取</li>
     * </ul>
     * 
     * @param key 有序集合的键名
     * @param start 起始位置（0表示分数最高的）
     * @param end 结束位置（-1表示分数最低的）
     * @return 指定范围内的元素集合（按分数倒序）
     */
    public Set<Object> zsetRangeReverse(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 从有序集合中移除元素
     * 
     * <p>从有序集合中移除一个或多个指定的元素。</p>
     * 
     * @param key 有序集合的键名
     * @param values 要移除的元素数组
     * @return 成功移除的元素数量
     */
    public Long zsetRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取有序集合的元素数量
     * 
     * <p>返回有序集合中元素的总数。</p>
     * 
     * @param key 有序集合的键名
     * @return 有序集合中元素的数量
     */
    public Long zsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}