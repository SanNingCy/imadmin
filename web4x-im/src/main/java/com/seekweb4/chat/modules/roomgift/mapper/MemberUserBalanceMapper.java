package com.seekweb4.chat.modules.roomgift.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.member.entity.Member;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface MemberUserBalanceMapper extends BaseMapper<Member> {
    /**
     * 根据用户id查询余额
     * @param id
     * @return
     */
    Member selectMemberById(Long id);
    
    /**
     * 获取用户余额
     * @param userId 用户ID
     * @return 用户余额
     */
    BigDecimal getUserBalance(@Param("userId") String userId);
    
    /**
     * 扣除用户余额
     * @param userId 用户ID
     * @param amount 扣除金额
     * @return 影响行数
     */
    int deductUserBalance(@Param("userId") String userId, @Param("amount") BigDecimal amount);
    
    /**
     * 增加用户余额
     * @param userId 用户ID
     * @param amount 增加金额
     * @return 影响行数
     */
    int addUserBalance(@Param("userId") String userId, @Param("amount") BigDecimal amount);
    
    /**
     * 记录余额日志
     * @param logId 日志ID
     * @param userId 用户ID
     * @param title 标题
     * @param amount 金额
     * @param state 状态
     * @param type 类型
     * @param info 信息
     * @return 影响行数
     */
    int recordBalanceLog(@Param("logId") String logId, @Param("userId") String userId, 
                        @Param("title") String title, @Param("amount") BigDecimal amount,
                        @Param("state") String state, @Param("type") String type, 
                        @Param("info") String info);
}
