package com.seekweb4.chat.modules.memberChangeLog.service;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.changenamelog.entity.ChangeNameLog;
import com.seekweb4.chat.modules.changenamelog.service.ChangeNameLogService;
import com.seekweb4.chat.modules.changepaypwdlog.entity.ChangePaypwdLog;
import com.seekweb4.chat.modules.changepaypwdlog.service.ChangePaypwdLogService;
import com.seekweb4.chat.modules.changephonelog.entity.ChangePhoneLog;
import com.seekweb4.chat.modules.changephonelog.service.ChangePhoneLogService;
import com.seekweb4.chat.modules.changepwdlog.entity.ChangePwdLog;
import com.seekweb4.chat.modules.changepwdlog.service.ChangePwdLogService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.memberChangeLog.vo.MemberChangeLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 用户信息修改记录统一Service
 * @author system
 * @version 2025-12-05
 */
@Service
@Transactional(readOnly = true)
public class MemberChangeLogService {

	@Autowired
	private ChangeNameLogService changeNameLogService;
	
	@Autowired
	private ChangePwdLogService changePwdLogService;
	
	@Autowired
	private ChangePhoneLogService changePhoneLogService;
	
	@Autowired
	private ChangePaypwdLogService changePaypwdLogService;

	/**
	 * 根据ID查询修改记录
	 * @param id 记录ID
	 * @param changeType 修改类型（nickname/password/phone/paypwd）
	 * @return 修改记录VO
	 */
	public MemberChangeLogVO get(String id, String changeType) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(changeType)) {
			return null;
		}
		
		MemberChangeLogVO vo = null;
		
		switch (changeType) {
			case "nickname":
				ChangeNameLog nameLog = changeNameLogService.get(id);
				if (nameLog != null) {
					vo = convertToVO(nameLog);
					vo.setChangeType("nickname");
					vo.setChangeTypeName("修改昵称");
					vo.setOldValue(nameLog.getOldName());
					vo.setNewValue(nameLog.getNewName());
				}
				break;
			case "password":
				ChangePwdLog pwdLog = changePwdLogService.get(id);
				if (pwdLog != null) {
					vo = convertToVO(pwdLog);
					vo.setChangeType("password");
					vo.setChangeTypeName("修改登录密码");
					vo.setOldValue("******");
					vo.setNewValue("******");
				}
				break;
			case "phone":
				ChangePhoneLog phoneLog = changePhoneLogService.get(id);
				if (phoneLog != null) {
					vo = convertToVO(phoneLog);
					vo.setChangeType("phone");
					vo.setChangeTypeName("修改手机号");
					vo.setOldValue(phoneLog.getOldPhone());
					vo.setNewValue(phoneLog.getNewPhone());
				}
				break;
			case "paypwd":
				ChangePaypwdLog paypwdLog = changePaypwdLogService.get(id);
				if (paypwdLog != null) {
					vo = convertToVO(paypwdLog);
					vo.setChangeType("paypwd");
					vo.setChangeTypeName("修改支付密码");
					vo.setOldValue("******");
					vo.setNewValue("******");
				}
				break;
		}
		
		return vo;
	}

	/**
	 * 查询所有类型的修改记录（分页）
	 * @param page 分页对象
	 * @param changeType 修改类型（可选，nickname/password/phone/paypwd）
	 * @param userId 用户ID（可选）
	 * @param uId 用户ID（可选，与userId功能相同）
	 * @param uIdno 用户IDNO（可选）
	 * @param uNickname 用户昵称（可选）
	 * @param beginCreateDate 开始创建时间（可选）
	 * @param endCreateDate 结束创建时间（可选）
	 * @return 分页结果
	 */
	public Page<MemberChangeLogVO> findPage(Page<MemberChangeLogVO> page, String changeType, String userId, String uId, String uIdno, String uNickname, Date beginCreateDate, Date endCreateDate) {
		// 如果userId为空，使用uId
		if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(uId)) {
			userId = uId;
		}
		List<MemberChangeLogVO> allList = new ArrayList<>();
		
		// 查询修改昵称记录
		if (StringUtils.isBlank(changeType) || "nickname".equals(changeType)) {
			ChangeNameLog queryName = new ChangeNameLog();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryName.setU(u);
			List<ChangeNameLog> nameList = changeNameLogService.findList(queryName);
			for (ChangeNameLog log : nameList) {
				// 时间过滤
				if (!isDateInRange(log.getCreateDate(), beginCreateDate, endCreateDate)) {
					continue;
				}
				MemberChangeLogVO vo = convertToVO(log);
				vo.setChangeType("nickname");
				vo.setChangeTypeName("修改昵称");
				vo.setOldValue(log.getOldName());
				vo.setNewValue(log.getNewName());
				allList.add(vo);
			}
		}
		
		// 查询修改密码记录
		if (StringUtils.isBlank(changeType) || "password".equals(changeType)) {
			ChangePwdLog queryPwd = new ChangePwdLog();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryPwd.setU(u);
			List<ChangePwdLog> pwdList = changePwdLogService.findList(queryPwd);
			for (ChangePwdLog log : pwdList) {
				// 时间过滤
				if (!isDateInRange(log.getCreateDate(), beginCreateDate, endCreateDate)) {
					continue;
				}
				MemberChangeLogVO vo = convertToVO(log);
				vo.setChangeType("password");
				vo.setChangeTypeName("修改登录密码");
				vo.setOldValue("******"); // 密码不显示明文
				vo.setNewValue("******");
				allList.add(vo);
			}
		}
		
		// 查询修改手机号记录
		if (StringUtils.isBlank(changeType) || "phone".equals(changeType)) {
			ChangePhoneLog queryPhone = new ChangePhoneLog();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryPhone.setU(u);
			List<ChangePhoneLog> phoneList = changePhoneLogService.findList(queryPhone);
			for (ChangePhoneLog log : phoneList) {
				// 时间过滤
				if (!isDateInRange(log.getCreateDate(), beginCreateDate, endCreateDate)) {
					continue;
				}
				MemberChangeLogVO vo = convertToVO(log);
				vo.setChangeType("phone");
				vo.setChangeTypeName("修改手机号");
				vo.setOldValue(log.getOldPhone());
				vo.setNewValue(log.getNewPhone());
				allList.add(vo);
			}
		}
		
		// 查询修改支付密码记录
		if (StringUtils.isBlank(changeType) || "paypwd".equals(changeType)) {
			ChangePaypwdLog queryPaypwd = new ChangePaypwdLog();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryPaypwd.setU(u);
			List<ChangePaypwdLog> paypwdList = changePaypwdLogService.findList(queryPaypwd);
			for (ChangePaypwdLog log : paypwdList) {
				// 时间过滤
				if (!isDateInRange(log.getCreateDate(), beginCreateDate, endCreateDate)) {
					continue;
				}
				MemberChangeLogVO vo = convertToVO(log);
				vo.setChangeType("paypwd");
				vo.setChangeTypeName("修改支付密码");
				vo.setOldValue("******"); // 密码不显示明文
				vo.setNewValue("******");
				allList.add(vo);
			}
		}
		
		// 按创建时间倒序排序
		allList.sort(Comparator.comparing(MemberChangeLogVO::getCreateDate).reversed());
		
		// 手动分页
		int total = allList.size();
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		int start = (pageNo - 1) * pageSize;
		int end = Math.min(start + pageSize, total);
		
		List<MemberChangeLogVO> pageList = new ArrayList<>();
		if (start < total) {
			pageList = allList.subList(start, end);
		}
		
		page.setList(pageList);
		page.setCount(total);
		
		return page;
	}

	/**
	 * 将ChangeNameLog转换为VO
	 */
	private MemberChangeLogVO convertToVO(ChangeNameLog log) {
		MemberChangeLogVO vo = new MemberChangeLogVO();
		vo.setId(log.getId());
		vo.setU(log.getU());
		vo.setIp(log.getIp());
		vo.setIpcity(log.getIpcity());
		vo.setCreateDate(log.getCreateDate());
		vo.setCreateBy(log.getCreateBy());
		return vo;
	}

	/**
	 * 将ChangePwdLog转换为VO
	 */
	private MemberChangeLogVO convertToVO(ChangePwdLog log) {
		MemberChangeLogVO vo = new MemberChangeLogVO();
		vo.setId(log.getId());
		vo.setU(log.getU());
		vo.setIp(log.getIp());
		vo.setIpcity(log.getIpcity());
		vo.setCreateDate(log.getCreateDate());
		vo.setCreateBy(log.getCreateBy());
		return vo;
	}

	/**
	 * 将ChangePhoneLog转换为VO
	 */
	private MemberChangeLogVO convertToVO(ChangePhoneLog log) {
		MemberChangeLogVO vo = new MemberChangeLogVO();
		vo.setId(log.getId());
		vo.setU(log.getU());
		vo.setIp(log.getIp());
		vo.setIpcity(log.getIpcity());
		vo.setCreateDate(log.getCreateDate());
		vo.setCreateBy(log.getCreateBy());
		return vo;
	}

	/**
	 * 将ChangePaypwdLog转换为VO
	 */
	private MemberChangeLogVO convertToVO(ChangePaypwdLog log) {
		MemberChangeLogVO vo = new MemberChangeLogVO();
		vo.setId(log.getId());
		vo.setU(log.getU());
		vo.setIp(log.getIp());
		vo.setIpcity(log.getIpcity());
		vo.setCreateDate(log.getCreateDate());
		vo.setCreateBy(log.getCreateBy());
		return vo;
	}
	
	/**
	 * 判断日期是否在指定范围内
	 * @param date 要判断的日期
	 * @param beginDate 开始日期（可为null）
	 * @param endDate 结束日期（可为null）
	 * @return true表示在范围内，false表示不在范围内
	 */
	private boolean isDateInRange(Date date, Date beginDate, Date endDate) {
		if (date == null) {
			return false;
		}
		if (beginDate != null && endDate != null) {
			return !date.before(beginDate) && !date.after(endDate);
		} else if (beginDate != null) {
			return !date.before(beginDate);
		} else if (endDate != null) {
			return !date.after(endDate);
		}
		return true; // 如果都没有指定，则不过滤
	}
}

