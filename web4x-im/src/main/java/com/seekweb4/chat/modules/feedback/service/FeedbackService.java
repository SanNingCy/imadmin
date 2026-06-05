package com.seekweb4.chat.modules.feedback.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.roomgift.service.MemberUserBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.feedback.entity.Feedback;
import com.seekweb4.chat.modules.feedback.mapper.FeedbackMapper;

import org.mybatis.spring.SqlSessionTemplate;

import jakarta.annotation.Resource;

/**
 * 意见反馈Service
 * @author lixinapp
 * @version 2022-12-19
 */
@Service
@Transactional(readOnly = true)
public class FeedbackService extends CrudService<FeedbackMapper, Feedback> {
	@Resource
	private MemberUserBalanceService memberUserBalanceService;

	@Resource
	private SqlSessionTemplate sqlSessionTemplate;

	/** 余额明细 type：与礼物(6)等区分，意见反馈采纳奖励 */
	private static final String BALANCE_LOG_TYPE_FEEDBACK_REWARD = "8";
	private static final String DEFAULT_REPLY_CONTENT = "感谢反馈！您的建议我们已成功接收，后续产品优化会参考您的意见，祝您使用愉快。";

	public Feedback get(String id) {
		return super.get(id);
	}
	
	public List<Feedback> findList(Feedback feedback) {
		return super.findList(feedback);
	}
	
	public Page<Feedback> findPage(Page<Feedback> page, Feedback feedback) {
		return super.findPage(page, feedback);
	}
	
	/**
	 * 管理员回复：写入回复内容、回复时间；若当前为「待处理」则置为「已回复」(1)。
	 * 已采纳(2) 仅更新回复文案，不降级状态。
	 */
	@Transactional(rollbackFor = Exception.class)
	public void replyById(String id, String reply) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id不能为空");
		}
		Feedback fb = get(id);
		if (fb == null) {
			throw new IllegalArgumentException("记录不存在");
		}
		String finalReply = StringUtils.isBlank(reply) ? DEFAULT_REPLY_CONTENT : reply.trim();
		fb.setReply(finalReply);
		fb.setReplyDate(new Date());
		int st = fb.getStatus() == null ? 0 : fb.getStatus();
		if (st == 0) {
			fb.setStatus(1);
		}
		save(fb);
	}

	/**
	 * 采纳：状态改为已采纳(2)，并按 rewardAmount 发放代币（与 {@link #save} 内逻辑一致）。
	 * 仅允许在「已回复」(1) 时采纳；奖励金额须大于 0 才会入账。
	 */
	@Transactional(rollbackFor = Exception.class)
	public void adoptById(String id, BigDecimal rewardAmount) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id不能为空");
		}
		Feedback fb = get(id);
		if (fb == null) {
			throw new IllegalArgumentException("记录不存在");
		}
		int st = fb.getStatus() == null ? 0 : fb.getStatus();
		if (st != 1) {
			throw new IllegalStateException("请先回复后再采纳（当前状态不可采纳）");
		}
		if (rewardAmount != null) {
			fb.setRewardAmount(rewardAmount.setScale(2, RoundingMode.HALF_UP));
		}
		fb.setStatus(2);
		save(fb);
	}

	@Transactional(rollbackFor = Exception.class)
	public void save(Feedback feedback) {
		if (StringUtils.isNotBlank(feedback.getId())) {
			// 同一 SqlSession 内对同一 id 可能缓存同一 Java 实例；reply/adopt 已先改 feedback 再进 save，
			// 再 get(id) 会得到「已改过的同一引用」，oldSt 误为 2，导致采纳发奖被跳过。清本地缓存后再查库得到真实旧状态。
			sqlSessionTemplate.clearCache();
			Feedback old = get(feedback.getId());
			if (old != null) {
				applyReplyDateIfChanged(old, feedback);
				applyAdoptReward(old, feedback);
			}
		}
		super.save(feedback);
	}

	/**
	 * 后台部分字段更新时合并库内数据，避免 null 覆盖；仅对 null 字段回填。
	 */
	public void mergeFromExistingForUpdate(Feedback feedback) {
		if (feedback == null || StringUtils.isBlank(feedback.getId())) {
			return;
		}
		Feedback db = get(feedback.getId());
		if (db == null) {
			return;
		}
		if (feedback.getMember() == null || StringUtils.isBlank(safeMemberId(feedback.getMember()))) {
			feedback.setMember(db.getMember());
		}
		if (feedback.getContent() == null) {
			feedback.setContent(db.getContent());
		}
		if (feedback.getPhone() == null) {
			feedback.setPhone(db.getPhone());
		}
		if (feedback.getImages() == null) {
			feedback.setImages(db.getImages());
		}
		if (feedback.getReply() == null) {
			feedback.setReply(db.getReply());
		}
		if (feedback.getReplyDate() == null) {
			feedback.setReplyDate(db.getReplyDate());
		}
		if (feedback.getStatus() == null) {
			feedback.setStatus(db.getStatus());
		}
		if (feedback.getRewardAmount() == null) {
			feedback.setRewardAmount(db.getRewardAmount());
		}
		if (feedback.getIsReward() == null) {
			feedback.setIsReward(db.getIsReward());
		}
	}

	private static String safeMemberId(Member m) {
		return m == null ? null : m.getId();
	}

	private static void applyReplyDateIfChanged(Feedback old, Feedback feedback) {
		if (feedback.getReply() == null) {
			return;
		}
		if (StringUtils.isBlank(feedback.getReply())) {
			return;
		}
		String prev = old.getReply() == null ? "" : old.getReply();
		if (!feedback.getReply().equals(prev)) {
			feedback.setReplyDate(new Date());
		}
	}

	/**
	 * 首次进入「已采纳」(status=2) 且配置了奖励金额时：入账 + 写 t_balance_log（info=回复内容），并标记 is_reward=1。
	 */
	private void applyAdoptReward(Feedback old, Feedback feedback) {
		int oldSt = old.getStatus() == null ? 0 : old.getStatus();
		Integer newSt = feedback.getStatus() != null ? feedback.getStatus() : old.getStatus();
		if (newSt == null) {
			newSt = 0;
		}

		boolean becomingAdopted = newSt == 2 && oldSt != 2;
		int oldRewardFlag = old.getIsReward() == null ? 0 : old.getIsReward();

		if (!becomingAdopted || oldRewardFlag == 1) {
			return;
		}

		BigDecimal amount = feedback.getRewardAmount() != null ? feedback.getRewardAmount() : old.getRewardAmount();
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		amount = amount.setScale(2, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		String uid = old.getMember() != null ? old.getMember().getId() : null;
		if (StringUtils.isBlank(uid)) {
			throw new IllegalStateException("反馈记录缺少用户，无法发放奖励");
		}

		memberUserBalanceService.addUserBalance(uid, amount);

		String info = feedback.getReply();
		if (StringUtils.isBlank(info)) {
			info = old.getReply();
		}
		if (info == null) {
			info = "";
		}

		boolean logged = memberUserBalanceService.recordBalanceLog(uid, "意见反馈采纳奖励", amount, "1", BALANCE_LOG_TYPE_FEEDBACK_REWARD, info);
		if (!logged) {
			throw new IllegalStateException("记录余额明细失败");
		}

		feedback.setIsReward(1);
		feedback.setRewardAmount(amount);
	}
	
	@Transactional(readOnly = false)
	public void delete(Feedback feedback) {
		super.delete(feedback);
	}
	
}