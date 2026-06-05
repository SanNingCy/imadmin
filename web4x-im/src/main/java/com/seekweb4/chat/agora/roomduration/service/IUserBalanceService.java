package com.seekweb4.chat.agora.roomduration.service;

import java.math.BigDecimal;

/**
 * 用户余额服务接口
 * 
 * @author Agora
 * @since 1.0.0
 */
public interface IUserBalanceService {
    
    /**
     * 查询用户余额
     * 
     * @param userId 用户ID
     * @return 用户余额
     * @throws Exception 查询异常
     */
    BigDecimal getUserBalance(String userId) throws Exception;
    
    /**
     * 扣除用户积分
     * 
     * @param userId 用户ID
     * @param points 扣除积分
     * @param title 操作标题
     * @param info 备注信息
     * @return 是否成功
     * @throws Exception 扣除异常
     */
    boolean deductUserPoints(String userId, BigDecimal points, String title, String info) throws Exception;
    
    /**
     * 检查用户余额是否足够
     * 
     * @param userId 用户ID
     * @param requiredPoints 需要的积分
     * @return 是否足够
     * @throws Exception 检查异常
     */
    boolean checkUserBalance(String userId, BigDecimal requiredPoints) throws Exception;

    /**
     * 更新用户表的withhold字段
     * @param userId 用户ID
     * @param withhold 扣除金额（直接写入）
     */
    void updateUserWithhold(String userId, BigDecimal withhold) throws Exception;

    /**
     * 累加用户表的withhold字段
     * @param userId 用户ID
     * @param additionalWithhold 要累加的金额
     */
    void addUserWithhold(String userId, BigDecimal additionalWithhold) throws Exception;

    /**
     * 根据用户的ID去查询IDNO这个字段
     */
    String getIdNoByUserId(String userId) throws Exception;
}
