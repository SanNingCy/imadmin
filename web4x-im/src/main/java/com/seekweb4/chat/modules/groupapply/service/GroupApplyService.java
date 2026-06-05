package com.seekweb4.chat.modules.groupapply.service;

import java.util.List;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.grouphuanying.service.GroupHuanyingService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.groupapply.entity.GroupApply;
import com.seekweb4.chat.modules.groupapply.mapper.GroupApplyMapper;

/**
 * 入群申请Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class GroupApplyService extends CrudService<GroupApplyMapper, GroupApply> {
	@Autowired
	private GroupItemService groupItemService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupHuanyingService groupHuanyingService;

	public GroupApply get(String id) {
		return super.get(id);
	}
	
	public List<GroupApply> findList(GroupApply groupApply) {
		return super.findList(groupApply);
	}
	
	public Page<GroupApply> findPage(Page<GroupApply> page, GroupApply groupApply) {
		return super.findPage(page, groupApply);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupApply groupApply) {
		super.save(groupApply);
	}

	@Transactional(readOnly = false)
	public void delete(GroupApply groupApply) {
		super.delete(groupApply);
	}

	/**
	 * 审核
	 * @param apply
	 */
	@Transactional(readOnly = false)
	public void examine(GroupApply apply,String state) {
		if("2".equals(state)){
			if("2".equals(apply.getType())){	//1：用户收到的 2：群管理员、群主收到的
				if(apply.getU() == null){//属于用户自己扫码申请
					GroupItem item = new GroupItem();
					item.setU(apply.getUid2());
					item.setGroup(apply.getGroup());
					item.setNickname(apply.getUid2().getNickname());
					item.setType("3");
					item.setIsjy("0");
					groupItemService.save(item);
					// 更新群头像
					groupItemService.updateGroupAvatar(apply.getGroup().getId());
					if(apply.getU() != null){
						ImUtils.addGroupMember(apply.getU().getId(),apply.getGroup().getId(),apply.getUid2().getId());
					}else {
						Group group = groupService.get(apply.getGroup().getId());
						ImUtils.addGroupMember(group.getU().getId(),apply.getGroup().getId(),apply.getUid2().getId());
					}
					groupHuanyingService.sendHuanying(apply.getGroup().getId());
				}else {
					GroupApply a = new GroupApply();
					a.setU(apply.getU());
					a.setUid2(apply.getUid2());
					a.setGroup(apply.getGroup());
					a.setInfo("您的好友'"+apply.getU().getNickname()+"'邀请您加入");
					a.setState("1");
					a.setType("1");
					a.setShowids(apply.getUid2().getId());
					super.save(a);
				}
			}else {
				GroupItem item = new GroupItem();
				item.setU(apply.getUid2());
				item.setGroup(apply.getGroup());
				item.setNickname(apply.getUid2().getNickname());
				item.setType("3");
				item.setIsjy("0");
				groupItemService.save(item);
				// 更新群头像
				groupItemService.updateGroupAvatar(apply.getGroup().getId());
				//Group group = groupService.get(apply.getGroup().getId());
				ImUtils.addGroupMember(apply.getU().getId(),apply.getGroup().getId(),apply.getUid2().getId());
				groupHuanyingService.sendHuanying(apply.getGroup().getId());
				//其他同一人请求都通过
				super.executeUpdateSql("update t_group_apply set state = 2 where group_id = '"+apply.getGroup().getId()+"' and uid2 = '"+apply.getUid2().getId()+"' and state = '1' and id != '"+apply.getId()+"'");
			}
		}
		apply.setState(state);
		super.save(apply);
	}

}