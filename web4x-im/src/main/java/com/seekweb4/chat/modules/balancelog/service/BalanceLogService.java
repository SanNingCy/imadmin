package com.seekweb4.chat.modules.balancelog.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.mapper.BalanceLogMapper;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;

/**
 * 余额明细Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class BalanceLogService extends CrudService<BalanceLogMapper, BalanceLog> {

	@Autowired
	@Lazy
	private MemberService memberService;

	public BalanceLog get(String id) {
		return super.get(id);
	}
	
	public List<BalanceLog> findList(BalanceLog balanceLog) {
		return super.findList(balanceLog);
	}
	
	public Page<BalanceLog> findPage(Page<BalanceLog> page, BalanceLog balanceLog) {
		return super.findPage(page, balanceLog);
	}
	
	@Transactional(readOnly = false)
	public void save(BalanceLog balanceLog) {
		super.save(balanceLog);
	}

	@Transactional(readOnly = false)
	public void update(BalanceLog balanceLog) {
		super.update(balanceLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(BalanceLog balanceLog) {
		super.delete(balanceLog);
	}
	
	/**
	 * 计算余额明细列表中每条记录的操作前后余额
	 * 由于记录是按时间倒序排列的（最新的在前），从第一条记录开始往前推
	 * @param balanceLogList 余额明细列表
	 */
	public void calculateBalanceInfo(List<BalanceLog> balanceLogList) {
		if (balanceLogList == null || balanceLogList.isEmpty()) {
			return;
		}
		
		// 先收集所有用户ID，批量查询当前余额
		java.util.Set<String> userIdSet = new java.util.HashSet<>();
		for (BalanceLog log : balanceLogList) {
			if (log.getU() != null && log.getU().getId() != null) {
				userIdSet.add(log.getU().getId());
			}
		}
		
		// 批量查询所有用户的当前余额
		java.util.Map<String, BigDecimal> userCurrentBalanceMap = new java.util.HashMap<>();
		for (String userId : userIdSet) {
			Member member = memberService.get(userId);
			BigDecimal currentBalance = BigDecimal.ZERO;
			if (member != null && member.getBalance() != null) {
				currentBalance = member.getBalance();
			}
			userCurrentBalanceMap.put(userId, currentBalance);
		}
		
		// 用于存储每个用户的最新余额（用于计算操作前后余额）
		java.util.Map<String, BigDecimal> userLatestBalanceMap = new java.util.HashMap<>();
		
		// 遍历每条记录，从最新的开始
		for (BalanceLog log : balanceLogList) {
			if (log.getU() == null || log.getU().getId() == null) {
				continue;
			}
			
			String userId = log.getU().getId();
			BigDecimal currentBalance = userCurrentBalanceMap.get(userId);
			if (currentBalance == null) {
				currentBalance = BigDecimal.ZERO;
			}
			
			// 设置用户当前余额（所有记录都设置相同的当前余额）
			log.setCurrentBalance(currentBalance);
			
			// 获取该用户的最新余额（用于计算）
			BigDecimal latestBalance = userLatestBalanceMap.get(userId);
			if (latestBalance == null) {
				// 第一条记录，操作后余额就是当前余额
				latestBalance = currentBalance;
			}
			
			// 计算操作后的余额（就是当前记录发生后的余额）
			BigDecimal afterBalance = latestBalance;
			log.setAfterBalance(afterBalance);
			
			// 计算操作前的余额
			BigDecimal beforeBalance;
			if (log.getMoney() != null) {
				if ("1".equals(log.getState())) {
					// 收入：操作前余额 = 操作后余额 - 金额
					beforeBalance = afterBalance.subtract(log.getMoney());
				} else {
					// 支出：操作前余额 = 操作后余额 + 金额
					beforeBalance = afterBalance.add(log.getMoney());
				}
			} else {
				beforeBalance = afterBalance;
			}
			log.setBeforeBalance(beforeBalance);
			
			// 更新该用户的最新余额，用于下一条记录的计算
			userLatestBalanceMap.put(userId, beforeBalance);
		}
	}
	
}