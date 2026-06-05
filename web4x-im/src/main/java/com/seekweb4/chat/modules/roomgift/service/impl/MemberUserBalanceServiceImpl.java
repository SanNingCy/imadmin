package com.seekweb4.chat.modules.roomgift.service.impl;


import com.seekweb4.chat.modules.roomgift.mapper.MemberUserBalanceMapper;
import com.seekweb4.chat.modules.roomgift.service.MemberUserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户余额服务实现类
 * @author lixinapp
 * @version 2024-10-20
 */
@Slf4j
@Service
public class MemberUserBalanceServiceImpl implements MemberUserBalanceService {
    
    @Autowired
    private MemberUserBalanceMapper memberUserBalanceMapper;
    
    @Override
    public BigDecimal getUserBalance(String userId) {
        try {
            BigDecimal balance = memberUserBalanceMapper.getUserBalance(userId);
            return balance != null ? balance : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("查询用户余额失败, userId: {}", userId, e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductUserBalance(String userId, BigDecimal amount) {
        try {
            // 检查用户余额是否足够
            BigDecimal currentBalance = getUserBalance(userId);
            if (currentBalance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("用户余额不足，当前余额: " + currentBalance + "，需要: " + amount);
            }
            
            // 扣除用户余额
            int updateCount = memberUserBalanceMapper.deductUserBalance(userId, amount);
            if (updateCount == 0) {
                throw new IllegalArgumentException("扣除用户余额失败，用户不存在");
            }
            
            log.info("扣除用户余额成功, userId: {}, amount: {}", userId, amount);
            return true;
            
        } catch (IllegalArgumentException e) {
            // 对于业务异常，直接重新抛出，不包装成RuntimeException
            log.error("扣除用户余额失败, userId: {}, amount: {}", userId, amount, e);
            throw e;
        } catch (Exception e) {
            log.error("扣除用户余额失败, userId: {}, amount: {}", userId, amount, e);
            throw new RuntimeException("扣除用户余额失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUserBalance(String userId, BigDecimal amount) {
        try {
            int updateCount = memberUserBalanceMapper.addUserBalance(userId, amount);
            if (updateCount == 0) {
                throw new IllegalArgumentException("增加用户余额失败，用户不存在");
            }
            
            log.info("增加用户余额成功, userId: {}, amount: {}", userId, amount);
            return true;
            
        } catch (IllegalArgumentException e) {
            // 对于业务异常，直接重新抛出，不包装成RuntimeException
            log.error("增加用户余额失败, userId: {}, amount: {}", userId, amount, e);
            throw e;
        } catch (Exception e) {
            log.error("增加用户余额失败, userId: {}, amount: {}", userId, amount, e);
            throw new RuntimeException("增加用户余额失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean recordBalanceLog(String userId, String title, BigDecimal amount, String state, String type, String info) {
        try {
            String logId = java.util.UUID.randomUUID().toString().replace("-", "");
            int result = memberUserBalanceMapper.recordBalanceLog(logId, userId, title, amount, state, type, info);
            
            if (result > 0) {
                log.info("记录余额日志成功, userId: {}, title: {}, amount: {}", userId, title, amount);
                return true;
            } else {
                log.error("记录余额日志失败, userId: {}, title: {}", userId, title);
                return false;
            }
            
        } catch (Exception e) {
            log.error("记录余额日志失败, userId: {}, title: {}", userId, title, e);
            return false;
        }
    }
}
