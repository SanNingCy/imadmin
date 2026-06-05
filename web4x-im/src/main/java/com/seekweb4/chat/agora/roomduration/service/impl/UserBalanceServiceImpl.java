package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomduration.service.IUserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户余额服务实现类
 * 
 * @author Agora
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserBalanceServiceImpl implements IUserBalanceService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public BigDecimal getUserBalance(String userId) throws Exception {
        String sql = "SELECT balance FROM t_member WHERE id = ?";
        try {
            BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
            return balance != null ? balance : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("查询用户余额失败, userId: {}", userId, e);
            throw new Exception("查询用户余额失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductUserPoints(String userId, BigDecimal points, String title, String info) throws Exception {
        try {
            // 1. 检查用户余额是否足够
            BigDecimal currentBalance = getUserBalance(userId);
            if (currentBalance.compareTo(points) < 0) {
                throw new Exception("用户余额不足，当前余额: " + currentBalance + "，需要: " + points);
            }
            
            // 2. 扣除用户余额
            String updateBalanceSql = "UPDATE t_member SET balance = balance - ? WHERE id = ?";
            int updateCount = jdbcTemplate.update(updateBalanceSql, points, userId);
            if (updateCount == 0) {
                throw new Exception("扣除用户余额失败，用户不存在或余额不足");
            }
            
            // 3. 记录余额日志
            String insertLogSql = "INSERT INTO t_balance_log (id, uid, title, money, state, type, create_date, update_date, info) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)";
            String logId = java.util.UUID.randomUUID().toString().replace("-", "");
            jdbcTemplate.update(insertLogSql, logId, userId, title, points, "0", "5", info);
            
            log.info("扣除用户积分成功, userId: {}, points: {}, title: {}", userId, points, title);
            return true;
            
        } catch (Exception e) {
            log.error("扣除用户积分失败, userId: {}, points: {}", userId, points, e);
            throw e;
        }
    }
    
    @Override
    public boolean checkUserBalance(String userId, BigDecimal requiredPoints) throws Exception {
        BigDecimal currentBalance = getUserBalance(userId);
        return currentBalance.compareTo(requiredPoints) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserWithhold(String userId, BigDecimal withhold) throws Exception {
        try {
            String sql = "UPDATE t_member SET withhold = ? WHERE id = ?";
            int n = jdbcTemplate.update(sql, withhold, userId);
            if (n == 0) {
                throw new Exception("更新withhold失败，用户不存在");
            }
            log.info("更新withhold成功, userId: {}, withhold: {}", userId, withhold);
        } catch (Exception e) {
            log.error("更新withhold失败, userId: {}, withhold: {}", userId, withhold, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserWithhold(String userId, BigDecimal additionalWithhold) throws Exception {
        try {
            String sql = "UPDATE t_member SET withhold = COALESCE(withhold, 0) + ? WHERE id = ?";
            int n = jdbcTemplate.update(sql, additionalWithhold, userId);
            if (n == 0) {
                throw new Exception("累加withhold失败，用户不存在");
            }
            log.info("累加withhold成功, userId: {}, additionalWithhold: {}", userId, additionalWithhold);
        } catch (Exception e) {
            log.error("累加withhold失败, userId: {}, additionalWithhold: {}", userId, additionalWithhold, e);
            throw e;
        }
    }

    @Override
    public String getIdNoByUserId(String userId) throws Exception {
        String sql = "SELECT idno FROM t_member WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            log.error("获取idNo失败, userId: {}", userId, e);
            throw e;
        }
    }
}
