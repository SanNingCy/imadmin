package com.seekweb4.chat.modules.zhuxiao.service;

import java.util.List;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.friendapply.service.FriendApplyService;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.zhuxiao.entity.Zhuxiao;
import com.seekweb4.chat.modules.zhuxiao.mapper.ZhuxiaoMapper;

/**
 * 注销申请Service
 * @author lixinapp
 * @version 2025-07-01
 */
@Service
@Transactional(readOnly = true)
public class ZhuxiaoService extends CrudService<ZhuxiaoMapper, Zhuxiao> {

	@Autowired
	private MemberService memberService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private FriendApplyService friendApplyService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupItemService groupItemService;

	public Zhuxiao get(String id) {
		return super.get(id);
	}
	
	public List<Zhuxiao> findList(Zhuxiao zhuxiao) {
		return super.findList(zhuxiao);
	}
	
	public Page<Zhuxiao> findPage(Page<Zhuxiao> page, Zhuxiao zhuxiao) {
		return super.findPage(page, zhuxiao);
	}
	
	@Transactional(readOnly = false)
	public void save(Zhuxiao zhuxiao) {
		super.save(zhuxiao);
	}
	@Transactional(readOnly = false)
	public void delete(Zhuxiao zhuxiao) {
		super.delete(zhuxiao);
	}

	/**
	 * 注销审核
	 * @param zhuxiao
	 */
	@Transactional(readOnly = false)
	public void examine(Zhuxiao zhuxiao) {
		super.save(zhuxiao);
		if("2".equals(zhuxiao.getState())){
			GroupItem item = new GroupItem();
			item.setU(new Member(zhuxiao.getUid()));
			//item.setDataScope("and a.type != '1'");
			List<GroupItem> list = groupItemService.findList(item);
			for(GroupItem ii:list){
				ImUtils.quitGroup(ii.getU().getId(),ii.getGroup().getId());
				groupItemService.delete(ii);
			}
			friendApplyService.executeDeleteSql("delete from t_friend_apply where uid = '"+zhuxiao.getUid()+"' or uid2 = '"+zhuxiao.getUid()+"'");
			friendService.executeDeleteSql("delete from t_friend where uid = '"+zhuxiao.getUid()+"' or uid2 = '"+zhuxiao.getUid()+"'");
			memberService.delete(new Member(zhuxiao.getUid()));
			ImUtils.deleteUser(zhuxiao.getUid());
		}
	}

}