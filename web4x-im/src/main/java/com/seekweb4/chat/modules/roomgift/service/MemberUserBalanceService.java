package com.seekweb4.chat.modules.roomgift.service;

import java.math.BigDecimal;

/**
 * 用户余额服务接口
 * @author lixinapp
 * @version 2024-10-20
 */
public interface MemberUserBalanceService {
    
    /**
     * 获取用户余额
     * @param userId 用户ID
     * @return 用户余额
     */
    BigDecimal getUserBalance(String userId);
    
    /**
     * 扣除用户余额
     * @param userId 用户ID
     * @param amount 扣除金额
     * @return 是否成功
     */
    boolean deductUserBalance(String userId, BigDecimal amount);
    
    /**
     * 增加用户余额
     * @param userId 用户ID
     * @param amount 增加金额
     * @return 是否成功
     */
    boolean addUserBalance(String userId, BigDecimal amount);
    
    /**
     * 记录余额日志
     * @param userId 用户ID
     * @param title 日志标题
     * @param amount 金额
     * @param state 状态（0：支出，1：收入）
     * @param type 类型
     * @param info 详细信息
     * @return 是否成功
     */
    boolean recordBalanceLog(String userId, String title, BigDecimal amount, String state, String type, String info);
}
