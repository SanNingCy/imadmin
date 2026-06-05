package com.seekweb4.chat.modules.friendapply.service;

import java.util.List;

import com.seekweb4.chat.api.utils.PinyinUtils;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.friend.entity.Friend;
import com.seekweb4.chat.modules.friend.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.friendapply.entity.FriendApply;
import com.seekweb4.chat.modules.friendapply.mapper.FriendApplyMapper;

/**
 * 好友申请记录Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class FriendApplyService extends CrudService<FriendApplyMapper, FriendApply> {
	@Autowired
	private FriendService friendService;

	public FriendApply get(String id) {
		return super.get(id);
	}
	
	public List<FriendApply> findList(FriendApply friendApply) {
		return super.findList(friendApply);
	}
	
	public Page<FriendApply> findPage(Page<FriendApply> page, FriendApply friendApply) {
		return super.findPage(page, friendApply);
	}
	
	@Transactional(readOnly = false)
	public void save(FriendApply friendApply) {
		super.save(friendApply);
	}
	
	@Transactional(readOnly = false)
	public void delete(FriendApply friendApply) {
		super.delete(friendApply);
	}

	/**
	 * 审批
	 * @param apply
	 */
	@Transactional(readOnly = false)
	public void examine(FriendApply apply) {
		super.save(apply);
		if("2".equals(apply.getState())){
			Friend friend = new Friend();
			friend.setU(apply.getU());
			friend.setUid2(apply.getUid2());
			friend.setZimu(PinyinUtils.getFirstLetter(apply.getUid2().getNickname()));
			friend.setMdr("0");
			friend.setIsTop("0");
			friendService.save(friend);
			Friend friend2 = new Friend();
			friend2.setU(apply.getUid2());
			friend2.setUid2(apply.getU());
			friend2.setZimu(PinyinUtils.getFirstLetter(apply.getU().getNickname()));
			friend2.setMdr("0");
			friend2.setIsTop("0");
			friendService.save(friend2);
			ImUtils.addFrinend(apply.getUid2().getId(),apply.getU().getId(),true);

			ImUtils.sendMsg(apply.getU().getId(),1003,"",apply.getUid2().getId());
			ImUtils.sendMsg(apply.getUid2().getId(),1003,"",apply.getU().getId());

			ImUtils.sendMsg(apply.getUid2().getId(),1, StringUtils.isNotBlank(apply.getUid2().getHuanying())?apply.getUid2().getHuanying():"你好，我已通过你的好友申请。",apply.getU().getId());
		}
	}
}